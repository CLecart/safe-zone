package com.safezone.order.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Coverage tests for {@link OrderStatus} enum.
 * Tests all enum values are accessible.
 *
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2026-01-09
 */
@DisplayName("OrderStatus Enum Coverage Tests")
class OrderStatusCoverageTest {

    @Test
    @DisplayName("All OrderStatus enum values exist")
    void allEnumValuesExist() {
        OrderStatus[] statuses = OrderStatus.values();

        assertThat(statuses)
                .contains(
                        OrderStatus.PENDING,
                        OrderStatus.CONFIRMED,
                        OrderStatus.PROCESSING,
                        OrderStatus.SHIPPED,
                        OrderStatus.DELIVERED,
                        OrderStatus.CANCELLED,
                        OrderStatus.REFUNDED)
                .hasSize(7);
    }

    @Test
    @DisplayName("OrderStatus valueOf works correctly")
    void valueOfReturnsCorrectEnum() {
        assertThat(OrderStatus.valueOf("PENDING")).isEqualTo(OrderStatus.PENDING);
        assertThat(OrderStatus.valueOf("CONFIRMED")).isEqualTo(OrderStatus.CONFIRMED);
        assertThat(OrderStatus.valueOf("CANCELLED")).isEqualTo(OrderStatus.CANCELLED);
        assertThat(OrderStatus.valueOf("REFUNDED")).isEqualTo(OrderStatus.REFUNDED);
    }

    @Test
    @DisplayName("OrderStatus name returns correct string")
    void nameReturnsCorrectString() {
        assertThat(OrderStatus.PENDING.name()).isEqualTo("PENDING");
        assertThat(OrderStatus.PROCESSING.name()).isEqualTo("PROCESSING");
        assertThat(OrderStatus.SHIPPED.name()).isEqualTo("SHIPPED");
    }
}
