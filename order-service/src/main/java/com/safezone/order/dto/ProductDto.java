package com.safezone.order.dto;

import java.math.BigDecimal;

public record ProductDto(
        Long id,
        String name,
        String sku,
        BigDecimal price,
        Integer stockQuantity,
        Boolean active
) {}
