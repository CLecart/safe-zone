package com.safezone.product.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Product Entity Tests")
class ProductTest {

    @Test
    @DisplayName("Builder creates product with fields")
    void builderCreatesProduct() {
        Product p = Product.builder()
                .id(1L)
                .name("N")
                .description("D")
                .price(BigDecimal.TEN)
                .stockQuantity(3)
                .sku("S")
                .category(ProductCategory.ELECTRONICS)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        assertThat(p.getName()).isEqualTo("N");
        assertThat(p.getStockQuantity()).isEqualTo(3);
        assertThat(p.getActive()).isTrue();
    }

    @Test
    @DisplayName("onCreate sets timestamps and defaults active")
    void onCreateSetsTimestampsAndActive() {
        Product p = Product.builder()
                .name("N")
                .price(BigDecimal.ONE)
                .stockQuantity(1)
                .sku("S")
                .category(ProductCategory.BOOKS)
                .build();

        p.onCreate();

        assertThat(p.getCreatedAt()).isNotNull();
        assertThat(p.getUpdatedAt()).isNotNull();
        assertThat(p.getActive()).isTrue();
    }

    @Test
    @DisplayName("onUpdate refreshes updatedAt")
    void onUpdateRefreshesUpdatedAt() {
        Product p = Product.builder()
                .name("N")
                .price(BigDecimal.ONE)
                .stockQuantity(1)
                .sku("S")
                .category(ProductCategory.BOOKS)
                .build();
        p.onCreate();
        LocalDateTime before = p.getUpdatedAt();
        p.onUpdate();
        assertThat(p.getUpdatedAt()).isNotNull();
        assertThat(p.getUpdatedAt().isBefore(before)).isFalse();
    }
}
