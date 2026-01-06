package com.safezone.order.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a customer order in the e-commerce system.
 * Contains order details, status, items, and shipping information.
 *
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2026-01-06
 */
@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    /** Unique identifier for the order. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Human-readable unique order number. */
    @Column(nullable = false, unique = true, length = 50)
    private String orderNumber;

    /** Reference to the user who placed the order. */
    @Column(nullable = false)
    private Long userId;

    /** Current status in the order lifecycle. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    /** Total order amount including all items. */
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    /** Delivery address for the order. */
    @Column(length = 500)
    private String shippingAddress;

    /** Billing address for payment. */
    @Column(length = 500)
    private String billingAddress;

    /** List of items included in this order. */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    /** Timestamp when the order was created. */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Timestamp of the last order update. */
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * JPA lifecycle callback executed before persisting a new entity.
     * Sets timestamps and default status.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = OrderStatus.PENDING;
        }
    }

    /**
     * JPA lifecycle callback executed before updating an existing entity.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Adds an item to the order and establishes bidirectional relationship.
     *
     * @param item the order item to add
     */
    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }

    /**
     * Removes an item from the order and clears the relationship.
     *
     * @param item the order item to remove
     */
    public void removeItem(OrderItem item) {
        items.remove(item);
        item.setOrder(null);
    }

    /**
     * Recalculates the total order amount from all items.
     */
    public void calculateTotalAmount() {
        this.totalAmount = items.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
