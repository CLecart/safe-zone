package com.safezone.product.dto;

import com.safezone.product.entity.ProductCategory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * Data Transfer Object for product creation requests.
 * Contains validated fields required to create a new product in the catalog.
 *
 * @param name          the product display name (2-100 characters)
 * @param description   optional product description (max 1000 characters)
 * @param price         the product price (must be greater than 0)
 * @param stockQuantity the initial stock quantity (non-negative)
 * @param sku           the unique Stock Keeping Unit identifier (3-50 characters)
 * @param category      the product category
 *
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2026-01-06
 */
public record CreateProductRequest(
        @NotBlank(message = "Product name is required")
        @Size(min = 2, max = 100, message = "Product name must be between 2 and 100 characters")
        String name,

        @Size(max = 1000, message = "Description cannot exceed 1000 characters")
        String description,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.01", message = "Price must be greater than 0")
        BigDecimal price,

        @NotNull(message = "Stock quantity is required")
        @Min(value = 0, message = "Stock quantity cannot be negative")
        Integer stockQuantity,

        @NotBlank(message = "SKU is required")
        @Size(min = 3, max = 50, message = "SKU must be between 3 and 50 characters")
        String sku,

        @NotNull(message = "Category is required")
        ProductCategory category
) {}
