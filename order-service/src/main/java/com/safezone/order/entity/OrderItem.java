package com.safezone.order.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Entity representing an individual item within an order.
 * Contains product reference, quantity, pricing, and subtotal calculation.
 *
 * <p>Order items are managed through the parent {@link Order} entity
 * and automatically calculate their subtotal based on quantity and unit price.</p>
 *
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2026-01-06
 * @see Order
 */
@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    /** Unique identifier for the order item. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The parent order containing this item. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    /** Reference to the product being ordered. */
    @Column(nullable = false)
    private Long productId;

    /** Name of the product at time of order (denormalized for history). */
    @Column(nullable = false, length = 100)
    private String productName;

    /** SKU of the product at time of order (denormalized for history). */
    @Column(nullable = false, length = 50)
    private String productSku;

    /** Quantity of items ordered. */
    @Column(nullable = false)
    private Integer quantity;

    /** Price per unit at time of order. */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    /** Calculated subtotal (quantity × unitPrice). */
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    /**
     * Calculates and sets the subtotal based on quantity and unit price.
     * Should be called whenever quantity or unit price changes.
     */
    public void calculateSubtotal() {
        this.subtotal = this.unitPrice.multiply(BigDecimal.valueOf(this.quantity));
    }
}
