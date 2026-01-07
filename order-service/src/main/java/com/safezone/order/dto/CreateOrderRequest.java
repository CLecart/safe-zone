package com.safezone.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Request DTO for creating a new order.
 * <p>
 * Contains the user ID, list of order items, and optional
 * shipping/billing addresses.
 * </p>
 *
 * @param userId the ID of the user placing the order (required)
 * @param items the list of items to order (at least one required)
 * @param shippingAddress the shipping address (max 500 characters)
 * @param billingAddress the billing address (max 500 characters)
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2024-01-06
 */
public record CreateOrderRequest(
        @NotNull(message = "User ID is required")
        Long userId,

        @NotEmpty(message = "Order must have at least one item")
        @Valid
        List<OrderItemRequest> items,

        @Size(max = 500, message = "Shipping address cannot exceed 500 characters")
        String shippingAddress,

        @Size(max = 500, message = "Billing address cannot exceed 500 characters")
        String billingAddress
) {}
