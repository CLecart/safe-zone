package com.safezone.order.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Coverage tests for OrderItem entity to reach 100%.
 *
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2026-01-12
 */
@DisplayName("OrderItem Entity Coverage Tests")
class OrderItemEntityCoverageTest {

    @Test
    @DisplayName("OrderItem builder creates valid instance")
    void orderItemBuilderCreatesValidInstance() {
        Order order = Order.builder().id(1L).build();

        OrderItem item = OrderItem.builder()
                .id(10L)
                .order(order)
                .productId(100L)
                .productName("Test Product")
                .productSku("TEST-SKU")
                .quantity(5)
                .unitPrice(BigDecimal.valueOf(19.99))
                .subtotal(BigDecimal.valueOf(99.95))
                .build();

        assertThat(item.getId()).isEqualTo(10L);
        assertThat(item.getOrder()).isEqualTo(order);
        assertThat(item.getProductId()).isEqualTo(100L);
        assertThat(item.getProductName()).isEqualTo("Test Product");
        assertThat(item.getProductSku()).isEqualTo("TEST-SKU");
        assertThat(item.getQuantity()).isEqualTo(5);
        assertThat(item.getUnitPrice()).isEqualByComparingTo(BigDecimal.valueOf(19.99));
        assertThat(item.getSubtotal()).isEqualByComparingTo(BigDecimal.valueOf(99.95));
    }

    @Test
    @DisplayName("OrderItem setters modify fields correctly")
    void orderItemSettersModifyFieldsCorrectly() {
        OrderItem item = new OrderItem();
        Order order = Order.builder().id(2L).build();

        item.setId(20L);
        item.setOrder(order);
        item.setProductId(200L);
        item.setProductName("Updated Product");
        item.setProductSku("UPD-SKU");
        item.setQuantity(3);
        item.setUnitPrice(BigDecimal.valueOf(29.99));
        item.setSubtotal(BigDecimal.valueOf(89.97));

        assertThat(item.getId()).isEqualTo(20L);
        assertThat(item.getOrder()).isEqualTo(order);
        assertThat(item.getProductId()).isEqualTo(200L);
        assertThat(item.getProductName()).isEqualTo("Updated Product");
        assertThat(item.getProductSku()).isEqualTo("UPD-SKU");
        assertThat(item.getQuantity()).isEqualTo(3);
        assertThat(item.getUnitPrice()).isEqualByComparingTo(BigDecimal.valueOf(29.99));
        assertThat(item.getSubtotal()).isEqualByComparingTo(BigDecimal.valueOf(89.97));
    }

    @Test
    @DisplayName("OrderItem AllArgsConstructor creates valid instance")
    void orderItemAllArgsConstructorCreatesValidInstance() {
        Order order = Order.builder().id(3L).build();

        OrderItem item = new OrderItem(
                30L,
                order,
                300L,
                "Constructor Product",
                "CONS-SKU",
                2,
                BigDecimal.valueOf(49.99),
                BigDecimal.valueOf(99.98));

        assertThat(item.getId()).isEqualTo(30L);
        assertThat(item.getProductId()).isEqualTo(300L);
        assertThat(item.getQuantity()).isEqualTo(2);
    }

    @Test
    @DisplayName("OrderItem NoArgsConstructor creates empty instance")
    void orderItemNoArgsConstructorCreatesEmptyInstance() {
        OrderItem item = new OrderItem();

        assertThat(item).isNotNull();
        assertThat(item.getId()).isNull();
        assertThat(item.getProductId()).isNull();
    }

    @Test
    @DisplayName("calculateSubtotal computes correct value")
    void calculateSubtotalComputesCorrectValue() {
        OrderItem item = OrderItem.builder()
                .quantity(4)
                .unitPrice(BigDecimal.valueOf(12.50))
                .build();

        item.calculateSubtotal();

        assertThat(item.getSubtotal()).isEqualByComparingTo(BigDecimal.valueOf(50.00));
    }

    @Test
    @DisplayName("calculateSubtotal handles decimal quantities correctly")
    void calculateSubtotalHandlesDecimalQuantitiesCorrectly() {
        OrderItem item = OrderItem.builder()
                .quantity(7)
                .unitPrice(BigDecimal.valueOf(19.99))
                .build();

        item.calculateSubtotal();

        assertThat(item.getSubtotal()).isEqualByComparingTo(BigDecimal.valueOf(139.93));
    }
}
