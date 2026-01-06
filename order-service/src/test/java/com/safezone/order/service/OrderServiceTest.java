package com.safezone.order.service;

import com.safezone.common.exception.BusinessException;
import com.safezone.common.exception.ResourceNotFoundException;
import com.safezone.order.client.ProductServiceClient;
import com.safezone.order.dto.CreateOrderRequest;
import com.safezone.order.dto.OrderItemRequest;
import com.safezone.order.dto.OrderResponse;
import com.safezone.order.dto.ProductDto;
import com.safezone.order.entity.Order;
import com.safezone.order.entity.OrderItem;
import com.safezone.order.entity.OrderStatus;
import com.safezone.order.mapper.OrderMapper;
import com.safezone.order.repository.OrderRepository;
import com.safezone.order.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private ProductServiceClient productServiceClient;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Order testOrder;
    private OrderResponse testOrderResponse;
    private ProductDto testProduct;

    @BeforeEach
    void setUp() {
        testProduct = new ProductDto(
                1L,
                "Test Product",
                "TEST-001",
                BigDecimal.valueOf(99.99),
                100,
                true
        );

        testOrder = Order.builder()
                .id(1L)
                .orderNumber("ORD-20260106-ABC12345")
                .userId(1L)
                .status(OrderStatus.PENDING)
                .totalAmount(BigDecimal.valueOf(99.99))
                .shippingAddress("123 Test St")
                .build();

        testOrderResponse = new OrderResponse(
                1L,
                "ORD-20260106-ABC12345",
                1L,
                OrderStatus.PENDING,
                BigDecimal.valueOf(99.99),
                "123 Test St",
                null,
                List.of(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Nested
    @DisplayName("Create Order Tests")
    class CreateOrderTests {

        @Test
        @DisplayName("Should create order successfully")
        void shouldCreateOrderSuccessfully() {
            CreateOrderRequest request = new CreateOrderRequest(
                    1L,
                    List.of(new OrderItemRequest(1L, 2)),
                    "123 Test St",
                    null
            );

            given(productServiceClient.getProductById(1L)).willReturn(Optional.of(testProduct));
            given(productServiceClient.checkProductAvailability(1L, 2)).willReturn(true);
            given(productServiceClient.updateStock(anyLong(), anyInt())).willReturn(Mono.empty());
            given(orderRepository.save(any(Order.class))).willReturn(testOrder);
            given(orderMapper.toResponse(any(Order.class))).willReturn(testOrderResponse);

            OrderResponse result = orderService.createOrder(request);

            assertThat(result).isNotNull();
            assertThat(result.orderNumber()).isNotNull();
            verify(orderRepository).save(any(Order.class));
        }

        @Test
        @DisplayName("Should throw exception when product not found")
        void shouldThrowExceptionWhenProductNotFound() {
            CreateOrderRequest request = new CreateOrderRequest(
                    1L,
                    List.of(new OrderItemRequest(999L, 2)),
                    "123 Test St",
                    null
            );

            given(productServiceClient.getProductById(999L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> orderService.createOrder(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Product not found");
        }

        @Test
        @DisplayName("Should throw exception when insufficient stock")
        void shouldThrowExceptionWhenInsufficientStock() {
            CreateOrderRequest request = new CreateOrderRequest(
                    1L,
                    List.of(new OrderItemRequest(1L, 200)),
                    "123 Test St",
                    null
            );

            given(productServiceClient.getProductById(1L)).willReturn(Optional.of(testProduct));
            given(productServiceClient.checkProductAvailability(1L, 200)).willReturn(false);

            assertThatThrownBy(() -> orderService.createOrder(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Insufficient stock");
        }
    }

    @Nested
    @DisplayName("Get Order Tests")
    class GetOrderTests {

        @Test
        @DisplayName("Should get order by ID")
        void shouldGetOrderById() {
            given(orderRepository.findById(1L)).willReturn(Optional.of(testOrder));
            given(orderMapper.toResponse(testOrder)).willReturn(testOrderResponse);

            OrderResponse result = orderService.getOrderById(1L);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Should throw exception when order not found")
        void shouldThrowExceptionWhenOrderNotFound() {
            given(orderRepository.findById(999L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> orderService.getOrderById(999L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Order not found");
        }

        @Test
        @DisplayName("Should get order by order number")
        void shouldGetOrderByNumber() {
            given(orderRepository.findByOrderNumber("ORD-20260106-ABC12345"))
                    .willReturn(Optional.of(testOrder));
            given(orderMapper.toResponse(testOrder)).willReturn(testOrderResponse);

            OrderResponse result = orderService.getOrderByNumber("ORD-20260106-ABC12345");

            assertThat(result).isNotNull();
            assertThat(result.orderNumber()).isEqualTo("ORD-20260106-ABC12345");
        }

        @Test
        @DisplayName("Should get orders by user ID")
        void shouldGetOrdersByUserId() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Order> orderPage = new PageImpl<>(List.of(testOrder), pageable, 1);

            given(orderRepository.findByUserId(1L, pageable)).willReturn(orderPage);
            given(orderMapper.toResponse(testOrder)).willReturn(testOrderResponse);

            Page<OrderResponse> result = orderService.getOrdersByUserId(1L, pageable);

            assertThat(result.getContent()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("Update Order Status Tests")
    class UpdateOrderStatusTests {

        @Test
        @DisplayName("Should update order status")
        void shouldUpdateOrderStatus() {
            given(orderRepository.findById(1L)).willReturn(Optional.of(testOrder));
            given(orderRepository.save(any(Order.class))).willReturn(testOrder);
            given(orderMapper.toResponse(any(Order.class))).willReturn(testOrderResponse);

            OrderResponse result = orderService.updateOrderStatus(1L, OrderStatus.CONFIRMED);

            assertThat(result).isNotNull();
            verify(orderRepository).save(any(Order.class));
        }

        @Test
        @DisplayName("Should throw exception for invalid status transition from cancelled")
        void shouldThrowExceptionForInvalidTransitionFromCancelled() {
            testOrder.setStatus(OrderStatus.CANCELLED);
            given(orderRepository.findById(1L)).willReturn(Optional.of(testOrder));

            assertThatThrownBy(() -> orderService.updateOrderStatus(1L, OrderStatus.PROCESSING))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Cannot change status");
        }
    }

    @Nested
    @DisplayName("Cancel Order Tests")
    class CancelOrderTests {

        @Test
        @DisplayName("Should cancel pending order")
        void shouldCancelPendingOrder() {
            testOrder.setStatus(OrderStatus.PENDING);
            testOrder.setItems(List.of(OrderItem.builder()
                    .productId(1L)
                    .quantity(2)
                    .build()));

            given(orderRepository.findById(1L)).willReturn(Optional.of(testOrder));
            given(orderRepository.save(any(Order.class))).willReturn(testOrder);
            given(orderMapper.toResponse(any(Order.class))).willReturn(testOrderResponse);
            given(productServiceClient.updateStock(anyLong(), anyInt())).willReturn(Mono.empty());

            OrderResponse result = orderService.cancelOrder(1L);

            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("Should throw exception when cancelling shipped order")
        void shouldThrowExceptionWhenCancellingShippedOrder() {
            testOrder.setStatus(OrderStatus.SHIPPED);
            given(orderRepository.findById(1L)).willReturn(Optional.of(testOrder));

            assertThatThrownBy(() -> orderService.cancelOrder(1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("cannot be cancelled");
        }
    }
}
