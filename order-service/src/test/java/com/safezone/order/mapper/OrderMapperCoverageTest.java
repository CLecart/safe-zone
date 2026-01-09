package com.safezone.order.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.safezone.order.dto.OrderResponse;
import com.safezone.order.entity.Order;
import com.safezone.order.entity.OrderItem;
import com.safezone.order.entity.OrderStatus;

/**
 * Coverage tests for {@link OrderMapper} MapStruct generated code.
 * Focuses on null handling, empty lists, and multiple entity conversions.
 *
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2026-01-09
 */
@SpringBootTest
@DisplayName("OrderMapper Coverage Tests")
class OrderMapperCoverageTest {

    @Autowired
    private OrderMapper orderMapper;

    private Order testOrder;

    @BeforeEach
    void setUp() {
        OrderItem item = OrderItem.builder()
                .id(1L)
                .productId(1L)
                .quantity(2)
                .unitPrice(BigDecimal.valueOf(99.99))
                .build();

        testOrder = Order.builder()
                .id(1L)
                .orderNumber("ORD-TEST-001")
                .userId(1L)
                .status(OrderStatus.PENDING)
                .totalAmount(BigDecimal.valueOf(199.98))
                .shippingAddress("Test Address")
                .items(List.of(item))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("toResponse maps Order entity to response DTO")
    void toResponseMapsOrderCorrectly() {
        OrderResponse response = orderMapper.toResponse(testOrder);

        assertThat(response)
                .extracting("id", "orderNumber", "userId", "status", "totalAmount", "shippingAddress")
                .containsExactly(1L, "ORD-TEST-001", 1L, OrderStatus.PENDING, BigDecimal.valueOf(199.98),
                        "Test Address");
        assertThat(response.items()).hasSize(1);
    }

    @Test
    @DisplayName("toResponse with different status")
    void toResponseWithDifferentStatus() {
        testOrder.setStatus(OrderStatus.CONFIRMED);
        OrderResponse response = orderMapper.toResponse(testOrder);

        assertThat(response.status()).isEqualTo(OrderStatus.CONFIRMED);
    }

    @Test
    @DisplayName("toResponse with null items list")
    void toResponseWithNullItems() {
        Order orderWithNullItems = Order.builder()
                .id(2L)
                .orderNumber("ORD-NULL-001")
                .userId(2L)
                .status(OrderStatus.PENDING)
                .totalAmount(BigDecimal.ZERO)
                .shippingAddress("Test")
                .items(null)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        OrderResponse response = orderMapper.toResponse(orderWithNullItems);

        assertThat(response.id()).isEqualTo(2L);
        // items will be null or empty depending on mapper configuration
    }

    @Test
    @DisplayName("toResponseList converts multiple orders")
    void toResponseListConvertsMultiple() {
        List<Order> orders = new ArrayList<>();
        orders.add(testOrder);
        orders.add(Order.builder()
                .id(2L)
                .orderNumber("ORD-SECOND-001")
                .userId(2L)
                .status(OrderStatus.CONFIRMED)
                .totalAmount(BigDecimal.valueOf(299.99))
                .shippingAddress("Another Address")
                .items(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());

        List<OrderResponse> responses = orderMapper.toResponseList(orders);

        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).orderNumber()).isEqualTo("ORD-TEST-001");
        assertThat(responses.get(1).orderNumber()).isEqualTo("ORD-SECOND-001");
    }

    @Test
    @DisplayName("toResponseList handles empty list")
    void toResponseListHandlesEmpty() {
        List<OrderResponse> responses = orderMapper.toResponseList(new ArrayList<>());

        assertThat(responses).isEmpty();
    }

    @Test
    @DisplayName("toItemResponse maps OrderItem correctly")
    void toItemResponseMapsCorrectly() {
        OrderItem item = OrderItem.builder()
                .id(1L)
                .productId(1L)
                .quantity(5)
                .unitPrice(BigDecimal.valueOf(99.99))
                .build();

        var response = orderMapper.toItemResponse(item);

        assertThat(response.productId()).isEqualTo(1L);
        assertThat(response.quantity()).isEqualTo(5);
        assertThat(response.unitPrice()).isEqualTo(BigDecimal.valueOf(99.99));
    }

    @Test
    @DisplayName("toItemResponse with null values")
    void toItemResponseWithNulls() {
        OrderItem item = OrderItem.builder()
                .productId(2L)
                .quantity(0)
                .unitPrice(BigDecimal.ZERO)
                .build();

        var response = orderMapper.toItemResponse(item);

        assertThat(response.productId()).isEqualTo(2L);
        assertThat(response.quantity()).isZero();
    }
}
