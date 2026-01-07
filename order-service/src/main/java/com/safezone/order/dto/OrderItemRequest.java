package com.safezone.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for a single order item.
 * <p>
 * Contains the product identifier and the quantity to order.
 * </p>
 *
 * @param productId the ID of the product to order (required)
 * @param quantity the quantity to order (minimum 1)
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2024-01-06
 */
public record OrderItemRequest(
        @NotNull(message = "Product ID is required")
        Long productId,

        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        Integer quantity
) {}
