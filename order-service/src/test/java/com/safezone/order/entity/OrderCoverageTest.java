package com.safezone.order.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Coverage tests for {@link Order} entity.
 * Focuses on builder variations, all getter/setter combinations, and
 * calculateTotalAmount().
 *
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2026-01-09
 */
@DisplayName("Order Coverage Tests")
class OrderCoverageTest {

    private Order testOrder;
    private OrderItem testItem;

    @BeforeEach
    void setUp() {
        testItem = OrderItem.builder()
                .id(1L)
                .productId(1L)
                .productName("Test Product")
                .productSku("TEST-001")
                .quantity(2)
                .unitPrice(BigDecimal.valueOf(99.99))
                .subtotal(BigDecimal.valueOf(199.98))
                .build();

        testOrder = Order.builder()
                .id(1L)
                .orderNumber("ORD-20260106-ABC12345")
                .userId(1L)
                .status(OrderStatus.PENDING)
                .totalAmount(BigDecimal.valueOf(199.98))
                .shippingAddress("123 Test St")
                .items(List.of(testItem))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Builder creates Order with all fields")
    void builderCreatesOrder() {
        assertThat(testOrder)
                .extracting("id", "orderNumber", "userId", "status", "shippingAddress")
                .containsExactly(1L, "ORD-20260106-ABC12345", 1L, OrderStatus.PENDING, "123 Test St");
        assertThat(testOrder.getItems()).hasSize(1);
    }

    @Test
    @DisplayName("Builder with minimal fields")
    void builderWithMinimalFields() {
        Order order = Order.builder()
                .orderNumber("ORD-TEST-001")
                .userId(2L)
                .status(OrderStatus.PENDING)
                .shippingAddress("Test Address")
                .build();

        assertThat(order)
                .extracting("orderNumber", "userId", "status")
                .containsExactly("ORD-TEST-001", 2L, OrderStatus.PENDING);
        assertThat(order.getId()).isNull();
        assertThat(order.getTotalAmount()).isNull();
    }

    @Test
    @DisplayName("All setters modify Order correctly")
    void settersModifyFields() {
        testOrder.setId(2L);
        testOrder.setOrderNumber("ORD-NEW-001");
        testOrder.setUserId(3L);
        testOrder.setStatus(OrderStatus.CONFIRMED);
        testOrder.setTotalAmount(BigDecimal.valueOf(299.99));
        testOrder.setShippingAddress("New Address");

        assertThat(testOrder)
                .extracting("id", "orderNumber", "userId", "status", "shippingAddress")
                .containsExactly(2L, "ORD-NEW-001", 3L, OrderStatus.CONFIRMED, "New Address");
        assertThat(testOrder.getTotalAmount()).isEqualTo(BigDecimal.valueOf(299.99));
    }

    @Test
    @DisplayName("All getters retrieve correct values")
    void gettersReturnCorrectValues() {
        assertThat(testOrder.getId()).isEqualTo(1L);
        assertThat(testOrder.getOrderNumber()).isEqualTo("ORD-20260106-ABC12345");
        assertThat(testOrder.getUserId()).isEqualTo(1L);
        assertThat(testOrder.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(testOrder.getTotalAmount()).isEqualTo(BigDecimal.valueOf(199.98));
        assertThat(testOrder.getShippingAddress()).isEqualTo("123 Test St");
    }

    @Test
    @DisplayName("calculateTotalAmount sums all items correctly")
    void calculateTotalAmountComputesCorrectly() {
        List<OrderItem> items = new ArrayList<>();
        OrderItem item1 = OrderItem.builder()
                .productId(1L)
                .productName("Product 1")
                .productSku("SKU-1")
                .quantity(2)
                .unitPrice(BigDecimal.valueOf(50.00))
                .build();
        item1.calculateSubtotal();

        OrderItem item2 = OrderItem.builder()
                .productId(2L)
                .productName("Product 2")
                .productSku("SKU-2")
                .quantity(3)
                .unitPrice(BigDecimal.valueOf(30.00))
                .build();
        item2.calculateSubtotal();

        items.add(item1);
        items.add(item2);

        Order order = Order.builder()
                .orderNumber("ORD-CALC-001")
                .userId(1L)
                .status(OrderStatus.PENDING)
                .shippingAddress("Test St")
                .items(items)
                .build();

        order.calculateTotalAmount();

        // 2*50 + 3*30 = 100 + 90 = 190
        assertThat(order.getTotalAmount()).isEqualTo(BigDecimal.valueOf(190.00));
    }

    @Test
    @DisplayName("calculateTotalAmount with single item")
    void calculateTotalAmountWithSingleItem() {
        List<OrderItem> items = new ArrayList<>();
        OrderItem item = OrderItem.builder()
                .productId(1L)
                .productName("Product 1")
                .productSku("SKU-1")
                .quantity(5)
                .unitPrice(BigDecimal.valueOf(25.50))
                .build();
        item.calculateSubtotal();
        items.add(item);

        Order order = Order.builder()
                .orderNumber("ORD-SINGLE-001")
                .userId(1L)
                .status(OrderStatus.PENDING)
                .shippingAddress("Test St")
                .items(items)
                .build();

        order.calculateTotalAmount();

        // 5 * 25.50 = 127.50
        assertThat(order.getTotalAmount()).isEqualTo(BigDecimal.valueOf(127.50));
    }

    @Test
    @DisplayName("calculateTotalAmount with empty items list")
    void calculateTotalAmountWithEmptyItems() {
        Order order = Order.builder()
                .orderNumber("ORD-EMPTY-001")
                .userId(1L)
                .status(OrderStatus.PENDING)
                .shippingAddress("Test St")
                .items(new ArrayList<>())
                .build();

        order.calculateTotalAmount();

        assertThat(order.getTotalAmount()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Builder with REFUNDED status")
    void builderWithRefundedStatus() {
        Order order = Order.builder()
                .orderNumber("ORD-REFUND-001")
                .userId(1L)
                .status(OrderStatus.REFUNDED)
                .shippingAddress("Test St")
                .items(new ArrayList<>())
                .build();

        assertThat(order.getStatus()).isEqualTo(OrderStatus.REFUNDED);
    }

    @Test
    @DisplayName("Builder with CANCELLED status")
    void builderWithCancelledStatus() {
        Order order = Order.builder()
                .orderNumber("ORD-CANCEL-001")
                .userId(1L)
                .status(OrderStatus.CANCELLED)
                .shippingAddress("Test St")
                .items(new ArrayList<>())
                .build();

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    @DisplayName("Items setter modifies items list")
    void itemsSetterModifiesList() {
        List<OrderItem> newItems = new ArrayList<>();
        newItems.add(OrderItem.builder()
                .productId(5L)
                .quantity(1)
                .unitPrice(BigDecimal.valueOf(100.00))
                .build());

        testOrder.setItems(newItems);

        assertThat(testOrder.getItems()).hasSize(1);
        assertThat(testOrder.getItems().get(0).getProductId()).isEqualTo(5L);
    }
}
