package com.safezone.order.entity;

/**
 * Enumeration of order lifecycle states.
 * Represents the progression of an order from creation to completion.
 *
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2026-01-06
 */
public enum OrderStatus {
    /** Order created but not yet confirmed. */
    PENDING,
    /** Order confirmed and payment received. */
    CONFIRMED,
    /** Order being prepared for shipment. */
    PROCESSING,
    /** Order shipped and in transit. */
    SHIPPED,
    /** Order successfully delivered to customer. */
    DELIVERED,
    /** Order cancelled before fulfillment. */
    CANCELLED,
    /** Order refunded after delivery. */
    REFUNDED
}
