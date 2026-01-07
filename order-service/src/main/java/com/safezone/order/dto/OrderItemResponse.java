package com.safezone.order.dto;

import java.math.BigDecimal;

/**
 * Response DTO for an order item.
 * <p>
 * Contains complete item information including product details,
 * quantity, pricing, and calculated subtotal.
 * </p>
 *
 * @param id the unique identifier of the order item
 * @param productId the ID of the associated product
 * @param productName the name of the product
 * @param productSku the SKU of the product
 * @param quantity the quantity ordered
 * @param unitPrice the price per unit
 * @param subtotal the calculated subtotal (quantity × unitPrice)
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2024-01-06
 */
public record OrderItemResponse(
        Long id,
        Long productId,
        String productName,
        String productSku,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal
) {}
