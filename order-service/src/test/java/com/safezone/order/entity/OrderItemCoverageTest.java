package com.safezone.order.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Coverage tests for {@link OrderItem} entity.
 * Focuses on builder variations and all getter/setter combinations.
 *
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2026-01-09
 */
@DisplayName("OrderItem Coverage Tests")
class OrderItemCoverageTest {

    private OrderItem testItem;

    @BeforeEach
    void setUp() {
        testItem = OrderItem.builder()
                .id(1L)
                .productId(1L)
                .productName("Test Product")
                .productSku("TEST-001")
                .quantity(5)
                .unitPrice(BigDecimal.valueOf(99.99))
                .subtotal(BigDecimal.valueOf(499.95))
                .build();
    }

    @Test
    @DisplayName("Builder creates OrderItem with all fields")
    void builderCreatesOrderItem() {
        assertThat(testItem)
                .extracting("id", "productId", "quantity", "unitPrice")
                .containsExactly(1L, 1L, 5, BigDecimal.valueOf(99.99));
    }

    @Test
    @DisplayName("Builder with minimal fields")
    void builderWithMinimalFields() {
        OrderItem item = OrderItem.builder()
                .productId(2L)
                .productName("Product 2")
                .productSku("SKU-2")
                .quantity(3)
                .unitPrice(BigDecimal.valueOf(49.99))
                .subtotal(BigDecimal.valueOf(149.97))
                .build();

        assertThat(item)
                .extracting("productId", "quantity", "unitPrice")
                .containsExactly(2L, 3, BigDecimal.valueOf(49.99));
        assertThat(item.getId()).isNull();
    }

    @Test
    @DisplayName("All setters modify OrderItem correctly")
    void settersModifyFields() {
        testItem.setId(2L);
        testItem.setProductId(3L);
        testItem.setQuantity(10);
        testItem.setUnitPrice(BigDecimal.valueOf(199.99));

        assertThat(testItem)
                .extracting("id", "productId", "quantity", "unitPrice")
                .containsExactly(2L, 3L, 10, BigDecimal.valueOf(199.99));
    }

    @Test
    @DisplayName("All getters retrieve correct values")
    void gettersReturnCorrectValues() {
        assertThat(testItem.getId()).isEqualTo(1L);
        assertThat(testItem.getProductId()).isEqualTo(1L);
        assertThat(testItem.getQuantity()).isEqualTo(5);
        assertThat(testItem.getUnitPrice()).isEqualTo(BigDecimal.valueOf(99.99));
    }

    @Test
    @DisplayName("Builder with zero quantity")
    void builderWithZeroQuantity() {
        OrderItem item = OrderItem.builder()
                .productId(1L)
                .productName("Product")
                .productSku("SKU")
                .quantity(0)
                .unitPrice(BigDecimal.ZERO)
                .subtotal(BigDecimal.ZERO)
                .build();

        assertThat(item.getQuantity()).isZero();
        assertThat(item.getUnitPrice()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Builder with large quantity")
    void builderWithLargeQuantity() {
        OrderItem item = OrderItem.builder()
                .productId(1L)
                .productName("Product")
                .productSku("SKU")
                .quantity(1000)
                .unitPrice(BigDecimal.valueOf(999.99))
                .subtotal(BigDecimal.valueOf(999990.00))
                .build();

        assertThat(item.getQuantity()).isEqualTo(1000);
    }
}
