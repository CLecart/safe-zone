package com.safezone.order.dto;

import java.math.BigDecimal;

/**
 * Data Transfer Object for product information from the Product Service.
 * <p>
 * Used by the Order Service to cache product details when processing orders.
 * </p>
 *
 * @param id the unique identifier of the product
 * @param name the name of the product
 * @param sku the Stock Keeping Unit code
 * @param price the current price of the product
 * @param stockQuantity the available stock quantity
 * @param active whether the product is currently active
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2024-01-06
 */
public record ProductDto(
        Long id,
        String name,
        String sku,
        BigDecimal price,
        Integer stockQuantity,
        Boolean active
) {}
