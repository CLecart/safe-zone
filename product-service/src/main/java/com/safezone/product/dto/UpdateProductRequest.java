package com.safezone.product.dto;

import com.safezone.product.entity.ProductCategory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * Data Transfer Object for product update requests.
 * Contains optional fields for partial product updates.
 *
 * <p>All fields are optional; only provided fields will be updated.</p>
 *
 * @param name          the new product name (optional, 2-100 characters)
 * @param description   the new description (optional, max 1000 characters)
 * @param price         the new price (optional, must be greater than 0 if provided)
 * @param stockQuantity the new stock quantity (optional, non-negative if provided)
 * @param category      the new category (optional)
 * @param active        the new active status (optional)
 *
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2026-01-06
 */
public record UpdateProductRequest(
        @Size(min = 2, max = 100, message = "Product name must be between 2 and 100 characters")
        String name,

        @Size(max = 1000, message = "Description cannot exceed 1000 characters")
        String description,

        @DecimalMin(value = "0.01", message = "Price must be greater than 0")
        BigDecimal price,

        @Min(value = 0, message = "Stock quantity cannot be negative")
        Integer stockQuantity,

        ProductCategory category,

        Boolean active
) {}
