package com.safezone.product.dto;

import com.safezone.product.entity.ProductCategory;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for product responses.
 * Contains all product information returned by API endpoints.
 *
 * @param id            the unique product identifier
 * @param name          the product display name
 * @param description   the product description
 * @param price         the current product price
 * @param stockQuantity the current stock level
 * @param sku           the Stock Keeping Unit identifier
 * @param category      the product category
 * @param active        whether the product is active for sale
 * @param createdAt     when the product was created
 * @param updatedAt     when the product was last modified
 *
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2026-01-06
 */
public record ProductResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Integer stockQuantity,
        String sku,
        ProductCategory category,
        Boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
