package com.safezone.product.dto;

import com.safezone.product.entity.ProductCategory;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
