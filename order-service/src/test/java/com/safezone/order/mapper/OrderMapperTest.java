package com.safezone.order.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.safezone.order.dto.OrderItemResponse;
import com.safezone.order.dto.OrderResponse;
import com.safezone.order.entity.Order;
import com.safezone.order.entity.OrderItem;
import com.safezone.order.entity.OrderStatus;

/**
 * Tests for OrderMapper to reach 100% coverage.
 *
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2026-01-12
 */
@SpringBootTest
@DisplayName("OrderMapper Tests")
class OrderMapperTest {

    @Autowired
    private OrderMapper mapper;

    @Test
    @DisplayName("toResponse maps order entity to DTO")
    void toResponseMapsOrder() {
        Order order = Order.builder()
                .id(1L)
                .orderNumber("ORD-001")
                .userId(100L)
                .status(OrderStatus.PENDING)
                .totalAmount(BigDecimal.valueOf(299.99))
                .shippingAddress("123 Ship St")
                .billingAddress("456 Bill Ave")
                .items(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        OrderResponse dto = mapper.toResponse(order);

        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.orderNumber()).isEqualTo("ORD-001");
        assertThat(dto.userId()).isEqualTo(100L);
        assertThat(dto.status()).isEqualTo(OrderStatus.PENDING);
        assertThat(dto.totalAmount()).isEqualByComparingTo(BigDecimal.valueOf(299.99));
    }

    @Test
    @DisplayName("toResponse handles null order")
    void toResponseHandlesNullOrder() {
        OrderResponse dto = mapper.toResponse(null);
        assertThat(dto).isNull();
    }

    @Test
    @DisplayName("toItemResponse maps order item entity to DTO")
    void toItemResponseMapsOrderItem() {
        OrderItem item = OrderItem.builder()
                .id(10L)
                .productId(500L)
                .productName("Test Product")
                .productSku("TEST-SKU")
                .quantity(3)
                .unitPrice(BigDecimal.valueOf(49.99))
                .subtotal(BigDecimal.valueOf(149.97))
                .build();

        OrderItemResponse dto = mapper.toItemResponse(item);

        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(10L);
        assertThat(dto.productId()).isEqualTo(500L);
        assertThat(dto.productName()).isEqualTo("Test Product");
        assertThat(dto.productSku()).isEqualTo("TEST-SKU");
        assertThat(dto.quantity()).isEqualTo(3);
        assertThat(dto.unitPrice()).isEqualByComparingTo(BigDecimal.valueOf(49.99));
        assertThat(dto.subtotal()).isEqualByComparingTo(BigDecimal.valueOf(149.97));
    }

    @Test
    @DisplayName("toItemResponse handles null order item")
    void toItemResponseHandlesNullOrderItem() {
        OrderItemResponse dto = mapper.toItemResponse(null);
        assertThat(dto).isNull();
    }

    @Test
    @DisplayName("toResponseList maps order list to DTOs")
    void toResponseListMapsOrderList() {
        Order order1 = Order.builder()
                .id(1L)
                .orderNumber("ORD-001")
                .userId(100L)
                .status(OrderStatus.CONFIRMED)
                .totalAmount(BigDecimal.valueOf(99.99))
                .items(new ArrayList<>())
                .build();

        Order order2 = Order.builder()
                .id(2L)
                .orderNumber("ORD-002")
                .userId(200L)
                .status(OrderStatus.PROCESSING)
                .totalAmount(BigDecimal.valueOf(199.99))
                .items(new ArrayList<>())
                .build();

        List<OrderResponse> dtoList = mapper.toResponseList(List.of(order1, order2));

        assertThat(dtoList).hasSize(2);
        assertThat(dtoList.get(0).id()).isEqualTo(1L);
        assertThat(dtoList.get(0).orderNumber()).isEqualTo("ORD-001");
        assertThat(dtoList.get(1).id()).isEqualTo(2L);
        assertThat(dtoList.get(1).orderNumber()).isEqualTo("ORD-002");
    }

    @Test
    @DisplayName("toResponseList handles null list")
    void toResponseListHandlesNullList() {
        List<OrderResponse> dtoList = mapper.toResponseList(null);
        assertThat(dtoList).isNull();
    }

    @Test
    @DisplayName("toResponseList handles empty list")
    void toResponseListHandlesEmptyList() {
        List<OrderResponse> dtoList = mapper.toResponseList(new ArrayList<>());
        assertThat(dtoList).isEmpty();
    }
}
