package com.safezone.order.dto;

import com.safezone.order.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO for order information.
 * <p>
 * Contains complete order details including order number, status,
 * items, addresses, and timestamps.
 * </p>
 *
 * @param id the unique identifier of the order
 * @param orderNumber the unique order number for reference
 * @param userId the ID of the user who placed the order
 * @param status the current status of the order
 * @param totalAmount the total amount of the order
 * @param shippingAddress the shipping address
 * @param billingAddress the billing address
 * @param items the list of order items
 * @param createdAt the timestamp when the order was created
 * @param updatedAt the timestamp when the order was last updated
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2024-01-06
 */
public record OrderResponse(
        Long id,
        String orderNumber,
        Long userId,
        OrderStatus status,
        BigDecimal totalAmount,
        String shippingAddress,
        String billingAddress,
        List<OrderItemResponse> items,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
