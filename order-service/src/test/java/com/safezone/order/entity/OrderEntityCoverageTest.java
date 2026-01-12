package com.safezone.order.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Coverage tests for Order entity to reach 100%.
 *
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2026-01-12
 */
@DisplayName("Order Entity Coverage Tests")
class OrderEntityCoverageTest {

    @Test
    @DisplayName("Order builder creates valid instance")
    void orderBuilderCreatesValidInstance() {
        LocalDateTime now = LocalDateTime.now();

        Order order = Order.builder()
                .id(1L)
                .orderNumber("ORD-001")
                .userId(10L)
                .status(OrderStatus.PENDING)
                .totalAmount(BigDecimal.valueOf(299.99))
                .shippingAddress("123 Test St")
                .billingAddress("456 Bill Ave")
                .items(new ArrayList<>())
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertThat(order.getId()).isEqualTo(1L);
        assertThat(order.getOrderNumber()).isEqualTo("ORD-001");
        assertThat(order.getUserId()).isEqualTo(10L);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(order.getTotalAmount()).isEqualByComparingTo(BigDecimal.valueOf(299.99));
        assertThat(order.getShippingAddress()).isEqualTo("123 Test St");
        assertThat(order.getBillingAddress()).isEqualTo("456 Bill Ave");
        assertThat(order.getItems()).isNotNull().isEmpty();
        assertThat(order.getCreatedAt()).isEqualTo(now);
        assertThat(order.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Order setters modify fields correctly")
    void orderSettersModifyFieldsCorrectly() {
        Order order = new Order();
        LocalDateTime now = LocalDateTime.now();

        order.setId(2L);
        order.setOrderNumber("ORD-002");
        order.setUserId(20L);
        order.setStatus(OrderStatus.CONFIRMED);
        order.setTotalAmount(BigDecimal.valueOf(499.99));
        order.setShippingAddress("789 Ship Rd");
        order.setBillingAddress("321 Pay Ln");
        order.setItems(new ArrayList<>());
        order.setCreatedAt(now);
        order.setUpdatedAt(now);

        assertThat(order.getId()).isEqualTo(2L);
        assertThat(order.getOrderNumber()).isEqualTo("ORD-002");
        assertThat(order.getUserId()).isEqualTo(20L);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
        assertThat(order.getTotalAmount()).isEqualByComparingTo(BigDecimal.valueOf(499.99));
        assertThat(order.getShippingAddress()).isEqualTo("789 Ship Rd");
        assertThat(order.getBillingAddress()).isEqualTo("321 Pay Ln");
    }

    @Test
    @DisplayName("Order AllArgsConstructor creates valid instance")
    void orderAllArgsConstructorCreatesValidInstance() {
        LocalDateTime now = LocalDateTime.now();

        Order order = new Order(
                3L,
                "ORD-003",
                30L,
                OrderStatus.PROCESSING,
                BigDecimal.valueOf(199.99),
                "111 Construct St",
                "222 Build Ave",
                new ArrayList<>(),
                now,
                now);

        assertThat(order.getId()).isEqualTo(3L);
        assertThat(order.getOrderNumber()).isEqualTo("ORD-003");
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PROCESSING);
    }

    @Test
    @DisplayName("Order NoArgsConstructor creates empty instance")
    void orderNoArgsConstructorCreatesEmptyInstance() {
        Order order = new Order();

        assertThat(order).isNotNull();
        assertThat(order.getId()).isNull();
        assertThat(order.getOrderNumber()).isNull();
    }

    @Test
    @DisplayName("PrePersist sets createdAt and updatedAt")
    void prePersistSetsTimestamps() {
        Order order = new Order();
        order.onCreate();

        assertThat(order.getCreatedAt()).isNotNull();
        assertThat(order.getUpdatedAt()).isNotNull();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
    }

    @Test
    @DisplayName("PreUpdate updates updatedAt timestamp")
    void preUpdateUpdatesTimestamp() {
        Order order = new Order();
        order.onCreate();
        LocalDateTime originalUpdatedAt = order.getUpdatedAt();

        // Simulate time passing
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        order.onUpdate();

        assertThat(order.getUpdatedAt()).isAfter(originalUpdatedAt);
    }

    @Test
    @DisplayName("addItem adds OrderItem to order")
    void addItemAddsOrderItemToOrder() {
        Order order = Order.builder().items(new ArrayList<>()).build();
        OrderItem item = OrderItem.builder()
                .productId(100L)
                .quantity(2)
                .unitPrice(BigDecimal.valueOf(50.00))
                .build();

        order.addItem(item);

        assertThat(order.getItems()).hasSize(1);
        assertThat(order.getItems().get(0)).isEqualTo(item);
        assertThat(item.getOrder()).isEqualTo(order);
    }

    @Test
    @DisplayName("removeItem removes OrderItem from order")
    void removeItemRemovesOrderItemFromOrder() {
        Order order = Order.builder().items(new ArrayList<>()).build();
        OrderItem item = OrderItem.builder()
                .productId(100L)
                .quantity(2)
                .unitPrice(BigDecimal.valueOf(50.00))
                .build();

        order.addItem(item);
        assertThat(order.getItems()).hasSize(1);

        order.removeItem(item);

        assertThat(order.getItems()).isEmpty();
        assertThat(item.getOrder()).isNull();
    }
}
