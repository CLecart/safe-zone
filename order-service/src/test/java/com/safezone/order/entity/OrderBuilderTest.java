package com.safezone.order.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Comprehensive test suite for {@link Order} and {@link OrderItem} entity
 * builder patterns.
 * 
 * <p>
 * <strong>Purpose:</strong>
 * Tests Lombok-generated builders for Order and OrderItem entities to achieve
 * 100% code coverage.
 * Builder pattern enables flexible object construction with optional fields.
 * 
 * <p>
 * <strong>Coverage Strategy:</strong>
 * <ul>
 * <li>Test builders with all fields populated</li>
 * <li>Test builders with minimal required fields</li>
 * <li>Test builders with partial field combinations</li>
 * <li>Test default values (@Builder.Default)</li>
 * <li>Test lifecycle callbacks (@PrePersist, @PreUpdate)</li>
 * <li>Test business methods (addItem, removeItem, calculateTotalAmount,
 * calculateSubtotal)</li>
 * </ul>
 * 
 * <p>
 * <strong>Target:</strong> Achieve 100% instruction and branch coverage for
 * OrderBuilder and OrderItemBuilder.
 * 
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2026-01-12
 * @see Order
 * @see OrderItem
 * @see Order.OrderBuilder
 * @see OrderItem.OrderItemBuilder
 */
@DisplayName("Order and OrderItem Builder Tests")
class OrderBuilderTest {
        @org.junit.jupiter.api.Test
        @org.junit.jupiter.api.DisplayName("Sanity: top-level test to satisfy Sonar S2187")
        void topLevelSanityTest() {
                // sentinel test: construct a minimal Order via the builder and
                // assert real properties so static analyzers see a meaningful
                // assertion rather than a literal boolean.
                Order order = Order.builder()
                                .orderNumber("SENTINEL-TEST")
                                .userId(0L)
                                .totalAmount(BigDecimal.ZERO)
                                .build();
                assertThat(order).isNotNull();
                assertThat(order.getOrderNumber()).isEqualTo("SENTINEL-TEST");
        }

        // ==================== ORDER BUILDER TESTS ====================

        /**
         * Nested test class for Order entity builder pattern.
         * Tests comprehensive coverage of Order.OrderBuilder.
         */
        @Nested
        @DisplayName("Order Builder Tests")
        class OrderBuilderTests {

                /**
                 * Tests Order builder with all fields populated.
                 * 
                 * <p>
                 * <strong>Scenario:</strong> Construct Order entity with complete data.
                 * 
                 * <p>
                 * <strong>Given:</strong> All Order fields have values.
                 * 
                 * <p>
                 * <strong>When:</strong> Builder builds Order instance.
                 * 
                 * <p>
                 * <strong>Then:</strong> Order contains all specified field values.
                 * 
                 * <p>
                 * <strong>Coverage:</strong> Tests all Order builder setter methods.
                 */
                @Test
                @DisplayName("Should build order with all fields")
                void shouldBuildOrderWithAllFields() {
                        // Arrange: Prepare all field values
                        LocalDateTime now = LocalDateTime.now();
                        List<OrderItem> items = new ArrayList<>();

                        // Act: Build Order with all fields
                        Order order = Order.builder()
                                        .id(1L)
                                        .orderNumber("ORD-2026-001")
                                        .userId(100L)
                                        .status(OrderStatus.CONFIRMED)
                                        .totalAmount(new BigDecimal("299.99"))
                                        .shippingAddress("123 Main St, Paris 75001")
                                        .billingAddress("456 Billing Ave, Paris 75002")
                                        .items(items)
                                        .createdAt(now)
                                        .updatedAt(now)
                                        .build();

                        // Assert: Verify all fields set correctly
                        assertThat(order.getId()).isEqualTo(1L);
                        assertThat(order.getOrderNumber()).isEqualTo("ORD-2026-001");
                        assertThat(order.getUserId()).isEqualTo(100L);
                        assertThat(order.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
                        assertThat(order.getTotalAmount()).isEqualByComparingTo("299.99");
                        assertThat(order.getShippingAddress()).isEqualTo("123 Main St, Paris 75001");
                        assertThat(order.getBillingAddress()).isEqualTo("456 Billing Ave, Paris 75002");
                        assertThat(order.getItems()).isEqualTo(items);
                        assertThat(order.getCreatedAt()).isEqualTo(now);
                        assertThat(order.getUpdatedAt()).isEqualTo(now);
                }

                /**
                 * Tests Order builder with minimal required fields.
                 * 
                 * <p>
                 * <strong>Scenario:</strong> Construct Order with only mandatory fields.
                 * 
                 * <p>
                 * <strong>Given:</strong> Only orderNumber, userId, totalAmount.
                 * 
                 * <p>
                 * <strong>When:</strong> Builder builds Order.
                 * 
                 * <p>
                 * <strong>Then:</strong> Order has required fields, optionals are null/default.
                 * 
                 * <p>
                 * <strong>Coverage:</strong> Tests builder with minimal fields.
                 */
                @Test
                @DisplayName("Should build order with minimal fields")
                void shouldBuildOrderWithMinimalFields() {
                        // Act: Build Order with minimal fields
                        Order order = Order.builder()
                                        .orderNumber("ORD-MIN-001")
                                        .userId(200L)
                                        .totalAmount(new BigDecimal("49.99"))
                                        .build();

                        // Assert: Verify required fields and null optionals
                        assertThat(order.getOrderNumber()).isEqualTo("ORD-MIN-001");
                        assertThat(order.getUserId()).isEqualTo(200L);
                        assertThat(order.getTotalAmount()).isEqualByComparingTo("49.99");
                        assertThat(order.getStatus()).isNull();
                        assertThat(order.getShippingAddress()).isNull();
                        assertThat(order.getBillingAddress()).isNull();
                }

                /**
                 * Tests Order builder with shipping and billing addresses.
                 * 
                 * <p>
                 * <strong>Scenario:</strong> Order with different shipping/billing addresses.
                 * 
                 * <p>
                 * <strong>Given:</strong> Shipping and billing addresses provided.
                 * 
                 * <p>
                 * <strong>When:</strong> Builder builds Order.
                 * 
                 * <p>
                 * <strong>Then:</strong> Order has specified addresses.
                 * 
                 * <p>
                 * <strong>Coverage:</strong> Tests address builder methods.
                 */
                @Test
                @DisplayName("Should build order with addresses")
                void shouldBuildOrderWithAddresses() {
                        // Act: Build Order with addresses
                        Order order = Order.builder()
                                        .orderNumber("ORD-ADDR-001")
                                        .userId(300L)
                                        .totalAmount(new BigDecimal("150.00"))
                                        .shippingAddress("789 Ship St, Lyon 69001")
                                        .billingAddress("321 Bill Ave, Lyon 69002")
                                        .build();

                        // Assert: Verify addresses
                        assertThat(order.getShippingAddress()).isEqualTo("789 Ship St, Lyon 69001");
                        assertThat(order.getBillingAddress()).isEqualTo("321 Bill Ave, Lyon 69002");
                }

                /**
                 * Tests Order builder with all order statuses.
                 * 
                 * <p>
                 * <strong>Scenario:</strong> Build orders in different lifecycle states.
                 * 
                 * <p>
                 * <strong>Given:</strong> Various OrderStatus values.
                 * 
                 * <p>
                 * <strong>When:</strong> Builder builds Orders with each status.
                 * 
                 * <p>
                 * <strong>Then:</strong> Orders have correct status values.
                 * 
                 * <p>
                 * <strong>Coverage:</strong> Tests status builder method with all enum values.
                 */
                @Test
                @DisplayName("Should build order with different statuses")
                void shouldBuildOrderWithDifferentStatuses() {
                        // Act & Assert: Test each status
                        Order pending = Order.builder()
                                        .orderNumber("ORD-PEND")
                                        .userId(1L)
                                        .totalAmount(BigDecimal.TEN)
                                        .status(OrderStatus.PENDING)
                                        .build();
                        assertThat(pending.getStatus()).isEqualTo(OrderStatus.PENDING);

                        Order confirmed = Order.builder()
                                        .orderNumber("ORD-CONF")
                                        .userId(1L)
                                        .totalAmount(BigDecimal.TEN)
                                        .status(OrderStatus.CONFIRMED)
                                        .build();
                        assertThat(confirmed.getStatus()).isEqualTo(OrderStatus.CONFIRMED);

                        Order shipped = Order.builder()
                                        .orderNumber("ORD-SHIP")
                                        .userId(1L)
                                        .totalAmount(BigDecimal.TEN)
                                        .status(OrderStatus.SHIPPED)
                                        .build();
                        assertThat(shipped.getStatus()).isEqualTo(OrderStatus.SHIPPED);
                }

                /**
                 * Tests @PrePersist lifecycle callback.
                 * 
                 * <p>
                 * <strong>Scenario:</strong> JPA callback sets default values before insert.
                 * 
                 * <p>
                 * <strong>Given:</strong> Order built without status/timestamps.
                 * 
                 * <p>
                 * <strong>When:</strong> onCreate() is called (simulates JPA).
                 * 
                 * <p>
                 * <strong>Then:</strong> Default status=PENDING and timestamps are set.
                 * 
                 * <p>
                 * <strong>Coverage:</strong> Tests onCreate() lifecycle method.
                 */
                @Test
                @DisplayName("Should set defaults on @PrePersist")
                void shouldSetDefaultsOnPrePersist() {
                        // Arrange: Build Order without defaults
                        Order order = Order.builder()
                                        .orderNumber("ORD-NEW")
                                        .userId(1L)
                                        .totalAmount(BigDecimal.TEN)
                                        .build();

                        // Act: Simulate @PrePersist
                        order.onCreate();

                        // Assert: Verify defaults set
                        assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
                        assertThat(order.getCreatedAt()).isNotNull();
                        assertThat(order.getUpdatedAt()).isNotNull();
                }

                /**
                 * Tests @PrePersist lifecycle callback with pre-existing status.
                 * 
                 * <p>
                 * <strong>Scenario:</strong> JPA callback respects explicitly set status.
                 * 
                 * <p>
                 * <strong>Given:</strong> Order built with status=CONFIRMED.
                 * 
                 * <p>
                 * <strong>When:</strong> onCreate() is called (simulates JPA).
                 * 
                 * <p>
                 * <strong>Then:</strong> Default status is NOT applied, existing status
                 * preserved.
                 * 
                 * <p>
                 * <strong>Coverage:</strong> Tests onCreate() when status is not null (else
                 * branch).
                 */
                @Test
                @DisplayName("Should preserve explicitly set status on @PrePersist")
                void shouldPreserveExplicitStatusOnPrePersist() {
                        // Arrange: Build Order with explicit status
                        Order order = Order.builder()
                                        .orderNumber("ORD-EXPLICIT")
                                        .userId(1L)
                                        .totalAmount(BigDecimal.TEN)
                                        .status(OrderStatus.CONFIRMED)
                                        .build();

                        // Act: Simulate @PrePersist
                        order.onCreate();

                        // Assert: Verify explicit status preserved
                        assertThat(order.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
                        assertThat(order.getCreatedAt()).isNotNull();
                        assertThat(order.getUpdatedAt()).isNotNull();
                }

                /**
                 * Tests @PreUpdate lifecycle callback.
                 * 
                 * <p>
                 * <strong>Scenario:</strong> JPA callback refreshes timestamp before update.
                 * 
                 * <p>
                 * <strong>Given:</strong> Order with old updatedAt timestamp.
                 * 
                 * <p>
                 * <strong>When:</strong> onUpdate() is called (simulates JPA).
                 * 
                 * <p>
                 * <strong>Then:</strong> UpdatedAt is refreshed to current time.
                 * 
                 * <p>
                 * <strong>Coverage:</strong> Tests onUpdate() lifecycle method.
                 */
                @Test
                @DisplayName("Should refresh timestamp on @PreUpdate")
                void shouldRefreshTimestampOnPreUpdate() {
                        // Arrange: Build Order with old timestamp
                        LocalDateTime old = LocalDateTime.of(2026, 1, 1, 10, 0);
                        Order order = Order.builder()
                                        .orderNumber("ORD-UPD")
                                        .userId(1L)
                                        .totalAmount(BigDecimal.TEN)
                                        .updatedAt(old)
                                        .build();

                        // Act: Simulate @PreUpdate
                        order.onUpdate();

                        // Assert: Verify timestamp refreshed
                        assertThat(order.getUpdatedAt()).isNotEqualTo(old);
                        assertThat(order.getUpdatedAt()).isAfter(old);
                }

                /**
                 * Tests addItem() business method.
                 * 
                 * <p>
                 * <strong>Scenario:</strong> Add OrderItem to Order with bidirectional link.
                 * 
                 * <p>
                 * <strong>Given:</strong> Order and OrderItem exist.
                 * 
                 * <p>
                 * <strong>When:</strong> addItem() is called.
                 * 
                 * <p>
                 * <strong>Then:</strong> Item is added to order and item.order is set.
                 * 
                 * <p>
                 * <strong>Coverage:</strong> Tests addItem() method.
                 */
                @Test
                @DisplayName("Should add item with bidirectional relationship")
                void shouldAddItemWithBidirectionalRelationship() {
                        // Arrange: Build Order and OrderItem
                        Order order = Order.builder()
                                        .orderNumber("ORD-ITEM")
                                        .userId(1L)
                                        .totalAmount(BigDecimal.ZERO)
                                        .build();

                        OrderItem item = OrderItem.builder()
                                        .productId(10L)
                                        .productName("Test Product")
                                        .productSku("SKU-001")
                                        .quantity(2)
                                        .unitPrice(new BigDecimal("25.00"))
                                        .subtotal(new BigDecimal("50.00"))
                                        .build();

                        // Act: Add item to order
                        order.addItem(item);

                        // Assert: Verify bidirectional relationship
                        assertThat(order.getItems()).contains(item);
                        assertThat(item.getOrder()).isEqualTo(order);
                }

                /**
                 * Tests removeItem() business method.
                 * 
                 * <p>
                 * <strong>Scenario:</strong> Remove OrderItem from Order and clear
                 * relationship.
                 * 
                 * <p>
                 * <strong>Given:</strong> Order contains OrderItem.
                 * 
                 * <p>
                 * <strong>When:</strong> removeItem() is called.
                 * 
                 * <p>
                 * <strong>Then:</strong> Item removed from order and item.order is null.
                 * 
                 * <p>
                 * <strong>Coverage:</strong> Tests removeItem() method.
                 */
                @Test
                @DisplayName("Should remove item and clear relationship")
                void shouldRemoveItemAndClearRelationship() {
                        // Arrange: Build Order with item
                        OrderItem item = OrderItem.builder()
                                        .productId(10L)
                                        .productName("Test Product")
                                        .productSku("SKU-001")
                                        .quantity(1)
                                        .unitPrice(BigDecimal.TEN)
                                        .subtotal(BigDecimal.TEN)
                                        .build();

                        Order order = Order.builder()
                                        .orderNumber("ORD-REM")
                                        .userId(1L)
                                        .totalAmount(BigDecimal.TEN)
                                        .build();

                        order.addItem(item);

                        // Act: Remove item
                        order.removeItem(item);

                        // Assert: Verify item removed and relationship cleared
                        assertThat(order.getItems()).doesNotContain(item);
                        assertThat(item.getOrder()).isNull();
                }

                /**
                 * Tests calculateTotalAmount() business method.
                 * 
                 * <p>
                 * <strong>Scenario:</strong> Calculate order total from all items.
                 * 
                 * <p>
                 * <strong>Given:</strong> Order contains multiple items with subtotals.
                 * 
                 * <p>
                 * <strong>When:</strong> calculateTotalAmount() is called.
                 * 
                 * <p>
                 * <strong>Then:</strong> Total equals sum of all item subtotals.
                 * 
                 * <p>
                 * <strong>Coverage:</strong> Tests calculateTotalAmount() method.
                 */
                @Test
                @DisplayName("Should calculate total from all items")
                void shouldCalculateTotalFromAllItems() {
                        // Arrange: Build Order with multiple items
                        Order order = Order.builder()
                                        .orderNumber("ORD-CALC")
                                        .userId(1L)
                                        .totalAmount(BigDecimal.ZERO)
                                        .build();

                        OrderItem item1 = OrderItem.builder()
                                        .productId(1L)
                                        .productName("Product 1")
                                        .productSku("SKU-1")
                                        .quantity(2)
                                        .unitPrice(new BigDecimal("10.00"))
                                        .subtotal(new BigDecimal("20.00"))
                                        .build();

                        OrderItem item2 = OrderItem.builder()
                                        .productId(2L)
                                        .productName("Product 2")
                                        .productSku("SKU-2")
                                        .quantity(1)
                                        .unitPrice(new BigDecimal("30.00"))
                                        .subtotal(new BigDecimal("30.00"))
                                        .build();

                        order.addItem(item1);
                        order.addItem(item2);

                        // Act: Calculate total
                        order.calculateTotalAmount();

                        // Assert: Verify total is sum of subtotals (20 + 30 = 50)
                        assertThat(order.getTotalAmount()).isEqualByComparingTo("50.00");
                }

                /**
                 * Tests calculateTotalAmount() with empty items list.
                 * 
                 * <p>
                 * <strong>Scenario:</strong> Calculate total when order has no items.
                 * 
                 * <p>
                 * <strong>Given:</strong> Order with empty items list.
                 * 
                 * <p>
                 * <strong>When:</strong> calculateTotalAmount() is called.
                 * 
                 * <p>
                 * <strong>Then:</strong> Total equals zero.
                 * 
                 * <p>
                 * <strong>Coverage:</strong> Tests edge case of empty items.
                 */
                @Test
                @DisplayName("Should calculate zero total for empty order")
                void shouldCalculateZeroTotalForEmptyOrder() {
                        // Arrange: Build Order with no items
                        Order order = Order.builder()
                                        .orderNumber("ORD-EMPTY")
                                        .userId(1L)
                                        .totalAmount(new BigDecimal("999.99"))
                                        .build();

                        // Act: Calculate total
                        order.calculateTotalAmount();

                        // Assert: Verify total is zero
                        assertThat(order.getTotalAmount()).isEqualByComparingTo("0.00");
                }
        }

        // ==================== ORDER ITEM BUILDER TESTS ====================

        /**
         * Nested test class for OrderItem entity builder pattern.
         * Tests comprehensive coverage of OrderItem.OrderItemBuilder.
         */
        @Nested
        @DisplayName("OrderItem Builder Tests")
        class OrderItemBuilderTests {

                /**
                 * Tests OrderItem builder with all fields populated.
                 * 
                 * <p>
                 * <strong>Scenario:</strong> Construct OrderItem with complete data.
                 * 
                 * <p>
                 * <strong>Given:</strong> All OrderItem fields have values.
                 * 
                 * <p>
                 * <strong>When:</strong> Builder builds OrderItem.
                 * 
                 * <p>
                 * <strong>Then:</strong> OrderItem contains all field values.
                 * 
                 * <p>
                 * <strong>Coverage:</strong> Tests all OrderItem builder setters.
                 */
                @Test
                @DisplayName("Should build order item with all fields")
                void shouldBuildOrderItemWithAllFields() {
                        // Arrange: Create parent order
                        Order order = Order.builder()
                                        .orderNumber("ORD-001")
                                        .userId(1L)
                                        .totalAmount(BigDecimal.ZERO)
                                        .build();

                        // Act: Build OrderItem with all fields
                        OrderItem item = OrderItem.builder()
                                        .id(1L)
                                        .order(order)
                                        .productId(100L)
                                        .productName("Premium Widget")
                                        .productSku("WIDGET-PREM-001")
                                        .quantity(5)
                                        .unitPrice(new BigDecimal("19.99"))
                                        .subtotal(new BigDecimal("99.95"))
                                        .build();

                        // Assert: Verify all fields
                        assertThat(item.getId()).isEqualTo(1L);
                        assertThat(item.getOrder()).isEqualTo(order);
                        assertThat(item.getProductId()).isEqualTo(100L);
                        assertThat(item.getProductName()).isEqualTo("Premium Widget");
                        assertThat(item.getProductSku()).isEqualTo("WIDGET-PREM-001");
                        assertThat(item.getQuantity()).isEqualTo(5);
                        assertThat(item.getUnitPrice()).isEqualByComparingTo("19.99");
                        assertThat(item.getSubtotal()).isEqualByComparingTo("99.95");
                }

                /**
                 * Tests OrderItem builder with minimal fields.
                 * 
                 * <p>
                 * <strong>Scenario:</strong> Construct OrderItem with required fields only.
                 * 
                 * <p>
                 * <strong>Given:</strong> Only productId, productName, productSku, quantity,
                 * unitPrice.
                 * 
                 * <p>
                 * <strong>When:</strong> Builder builds OrderItem.
                 * 
                 * <p>
                 * <strong>Then:</strong> OrderItem has required fields, optionals null.
                 * 
                 * <p>
                 * <strong>Coverage:</strong> Tests minimal builder usage.
                 */
                @Test
                @DisplayName("Should build order item with minimal fields")
                void shouldBuildOrderItemWithMinimalFields() {
                        // Act: Build OrderItem with minimal fields
                        OrderItem item = OrderItem.builder()
                                        .productId(200L)
                                        .productName("Basic Item")
                                        .productSku("BASIC-001")
                                        .quantity(1)
                                        .unitPrice(new BigDecimal("9.99"))
                                        .subtotal(new BigDecimal("9.99"))
                                        .build();

                        // Assert: Verify required fields
                        assertThat(item.getProductId()).isEqualTo(200L);
                        assertThat(item.getProductName()).isEqualTo("Basic Item");
                        assertThat(item.getProductSku()).isEqualTo("BASIC-001");
                        assertThat(item.getQuantity()).isEqualTo(1);
                        assertThat(item.getUnitPrice()).isEqualByComparingTo("9.99");
                        assertThat(item.getSubtotal()).isEqualByComparingTo("9.99");
                        assertThat(item.getOrder()).isNull();
                }

                /**
                 * Tests calculateSubtotal() business method.
                 * 
                 * <p>
                 * <strong>Scenario:</strong> Calculate subtotal from quantity × unitPrice.
                 * 
                 * <p>
                 * <strong>Given:</strong> OrderItem with quantity=3, unitPrice=15.50.
                 * 
                 * <p>
                 * <strong>When:</strong> calculateSubtotal() is called.
                 * 
                 * <p>
                 * <strong>Then:</strong> Subtotal equals 46.50 (3 × 15.50).
                 * 
                 * <p>
                 * <strong>Coverage:</strong> Tests calculateSubtotal() method.
                 */
                @Test
                @DisplayName("Should calculate subtotal from quantity and unit price")
                void shouldCalculateSubtotalFromQuantityAndUnitPrice() {
                        // Arrange: Build OrderItem with quantity and unitPrice
                        OrderItem item = OrderItem.builder()
                                        .productId(1L)
                                        .productName("Test")
                                        .productSku("SKU")
                                        .quantity(3)
                                        .unitPrice(new BigDecimal("15.50"))
                                        .subtotal(BigDecimal.ZERO)
                                        .build();

                        // Act: Calculate subtotal
                        item.calculateSubtotal();

                        // Assert: Verify subtotal = 3 × 15.50 = 46.50
                        assertThat(item.getSubtotal()).isEqualByComparingTo("46.50");
                }

                /**
                 * Tests calculateSubtotal() with quantity of 1.
                 * 
                 * <p>
                 * <strong>Scenario:</strong> Calculate subtotal for single item.
                 * 
                 * <p>
                 * <strong>Given:</strong> Quantity=1, unitPrice=25.00.
                 * 
                 * <p>
                 * <strong>When:</strong> calculateSubtotal() is called.
                 * 
                 * <p>
                 * <strong>Then:</strong> Subtotal equals unitPrice (25.00).
                 * 
                 * <p>
                 * <strong>Coverage:</strong> Tests edge case of quantity=1.
                 */
                @Test
                @DisplayName("Should calculate subtotal for single quantity")
                void shouldCalculateSubtotalForSingleQuantity() {
                        // Arrange: Build OrderItem with quantity 1
                        OrderItem item = OrderItem.builder()
                                        .productId(1L)
                                        .productName("Single")
                                        .productSku("SKU")
                                        .quantity(1)
                                        .unitPrice(new BigDecimal("25.00"))
                                        .subtotal(BigDecimal.ZERO)
                                        .build();

                        // Act: Calculate subtotal
                        item.calculateSubtotal();

                        // Assert: Verify subtotal equals unit price
                        assertThat(item.getSubtotal()).isEqualByComparingTo("25.00");
                }

                /**
                 * Tests calculateSubtotal() with large quantity.
                 * 
                 * <p>
                 * <strong>Scenario:</strong> Calculate subtotal for bulk order.
                 * 
                 * <p>
                 * <strong>Given:</strong> Quantity=100, unitPrice=2.50.
                 * 
                 * <p>
                 * <strong>When:</strong> calculateSubtotal() is called.
                 * 
                 * <p>
                 * <strong>Then:</strong> Subtotal equals 250.00 (100 × 2.50).
                 * 
                 * <p>
                 * <strong>Coverage:</strong> Tests calculation with larger numbers.
                 */
                @Test
                @DisplayName("Should calculate subtotal for large quantity")
                void shouldCalculateSubtotalForLargeQuantity() {
                        // Arrange: Build OrderItem with large quantity
                        OrderItem item = OrderItem.builder()
                                        .productId(1L)
                                        .productName("Bulk Item")
                                        .productSku("BULK-SKU")
                                        .quantity(100)
                                        .unitPrice(new BigDecimal("2.50"))
                                        .subtotal(BigDecimal.ZERO)
                                        .build();

                        // Act: Calculate subtotal
                        item.calculateSubtotal();

                        // Assert: Verify subtotal = 100 × 2.50 = 250.00
                        assertThat(item.getSubtotal()).isEqualByComparingTo("250.00");
                }

                /**
                 * Tests OrderItem with decimal unit price.
                 * 
                 * <p>
                 * <strong>Scenario:</strong> Build item with precise decimal pricing.
                 * 
                 * <p>
                 * <strong>Given:</strong> UnitPrice=12.345 (3 decimal places).
                 * 
                 * <p>
                 * <strong>When:</strong> Builder builds OrderItem.
                 * 
                 * <p>
                 * <strong>Then:</strong> Price precision is maintained.
                 * 
                 * <p>
                 * <strong>Coverage:</strong> Tests decimal precision handling.
                 */
                @Test
                @DisplayName("Should handle decimal unit prices")
                void shouldHandleDecimalUnitPrices() {
                        // Act: Build OrderItem with decimal price
                        OrderItem item = OrderItem.builder()
                                        .productId(1L)
                                        .productName("Decimal Item")
                                        .productSku("DEC-SKU")
                                        .quantity(1)
                                        .unitPrice(new BigDecimal("12.345"))
                                        .subtotal(new BigDecimal("12.345"))
                                        .build();

                        // Assert: Verify decimal precision maintained
                        assertThat(item.getUnitPrice()).isEqualByComparingTo("12.345");
                        assertThat(item.getSubtotal()).isEqualByComparingTo("12.345");
                }
        }
}
