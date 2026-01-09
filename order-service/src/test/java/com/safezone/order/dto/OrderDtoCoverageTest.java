package com.safezone.order.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Coverage tests for DTOs.
 * Tests record constructors, accessors, and edge cases.
 *
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2026-01-09
 */
@DisplayName("Order DTO Coverage Tests")
class OrderDtoCoverageTest {

    @Test
    @DisplayName("CreateOrderRequest with all fields")
    void createOrderRequestWithAllFields() {
        CreateOrderRequest request = new CreateOrderRequest(
                1L,
                java.util.List.of(new OrderItemRequest(1L, 5)),
                "123 Test St",
                null);

        assertThat(request.userId()).isEqualTo(1L);
        assertThat(request.items()).hasSize(1);
        assertThat(request.shippingAddress()).isEqualTo("123 Test St");
        assertThat(request.billingAddress()).isNull();
    }

    @Test
    @DisplayName("OrderItemRequest construction")
    void orderItemRequestConstruction() {
        OrderItemRequest item = new OrderItemRequest(2L, 3);

        assertThat(item.productId()).isEqualTo(2L);
        assertThat(item.quantity()).isEqualTo(3);
    }

    @Test
    @DisplayName("ProductDto with all fields")
    void productDtoWithAllFields() {
        ProductDto product = new ProductDto(
                1L,
                "Test Product",
                "TEST-001",
                BigDecimal.valueOf(99.99),
                100,
                true);

        assertThat(product.id()).isEqualTo(1L);
        assertThat(product.name()).isEqualTo("Test Product");
        assertThat(product.sku()).isEqualTo("TEST-001");
        assertThat(product.price()).isEqualTo(BigDecimal.valueOf(99.99));
        assertThat(product.stockQuantity()).isEqualTo(100);
        assertThat(product.active()).isTrue();
    }

    @Test
    @DisplayName("OrderItemResponse construction")
    void orderItemResponseConstruction() {
        OrderItemResponse item = new OrderItemResponse(
                1L,
                1L,
                "Test Product",
                "TEST-001",
                5,
                BigDecimal.valueOf(99.99),
                BigDecimal.valueOf(499.95));

        assertThat(item.productId()).isEqualTo(1L);
        assertThat(item.quantity()).isEqualTo(5);
        assertThat(item.unitPrice()).isEqualTo(BigDecimal.valueOf(99.99));
        assertThat(item.subtotal()).isEqualTo(BigDecimal.valueOf(499.95));
    }
}
