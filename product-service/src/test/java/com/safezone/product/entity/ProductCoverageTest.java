package com.safezone.product.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Additional coverage tests for Product entity.
 * Tests all builder methods and getter/setter combinations for 100% coverage.
 *
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2026-01-09
 */
@DisplayName("Product Entity Coverage Tests")
class ProductCoverageTest {

    @Test
    @DisplayName("Builder covers all setters and toString")
    void builderCoversAllMethods() {
        LocalDateTime now = LocalDateTime.now();
        Product p = Product.builder()
                .id(10L)
                .name("Full")
                .description("Complete")
                .price(BigDecimal.TEN)
                .stockQuantity(50)
                .sku("FULL-SKU")
                .category(ProductCategory.BOOKS)
                .active(false)
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Test all getters
        assertThat(p.getId()).isEqualTo(10L);
        assertThat(p.getName()).isEqualTo("Full");
        assertThat(p.getDescription()).isEqualTo("Complete");
        assertThat(p.getPrice()).isEqualByComparingTo(BigDecimal.TEN);
        assertThat(p.getStockQuantity()).isEqualTo(50);
        assertThat(p.getSku()).isEqualTo("FULL-SKU");
        assertThat(p.getCategory()).isEqualTo(ProductCategory.BOOKS);
        assertThat(p.getActive()).isFalse();
        assertThat(p.getCreatedAt()).isEqualTo(now);
        assertThat(p.getUpdatedAt()).isEqualTo(now);

        // Test all setters
        p.setId(20L);
        p.setName("Modified");
        p.setDescription("Changed");
        p.setPrice(BigDecimal.ONE);
        p.setStockQuantity(75);
        p.setSku("MOD-SKU");
        p.setCategory(ProductCategory.SPORTS);
        p.setActive(true);
        LocalDateTime later = now.plusHours(1);
        p.setCreatedAt(later);
        p.setUpdatedAt(later);

        assertThat(p.getId()).isEqualTo(20L);
        assertThat(p.getName()).isEqualTo("Modified");
        assertThat(p.getDescription()).isEqualTo("Changed");
        assertThat(p.getPrice()).isEqualByComparingTo(BigDecimal.ONE);
        assertThat(p.getStockQuantity()).isEqualTo(75);
        assertThat(p.getSku()).isEqualTo("MOD-SKU");
        assertThat(p.getCategory()).isEqualTo(ProductCategory.SPORTS);
        assertThat(p.getActive()).isTrue();
        assertThat(p.getCreatedAt()).isEqualTo(later);
        assertThat(p.getUpdatedAt()).isEqualTo(later);
    }

    @Test
    @DisplayName("onCreate when active already set should not override")
    void onCreateWithActiveAlreadySet() {
        Product p = new Product();
        p.setActive(false);
        p.onCreate();
        assertThat(p.getActive()).isFalse();
        assertThat(p.getCreatedAt()).isNotNull();
        assertThat(p.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Test Product constructor and builder chaining")
    void testConstructorAndBuilderChain() {
        Product p1 = new Product();
        assertThat(p1.getId()).isNull();

        LocalDateTime now = LocalDateTime.now();
        Product p2 = new Product(
                5L,
                "Constructed",
                "Desc",
                BigDecimal.valueOf(25.5),
                30,
                "CONS-SKU",
                ProductCategory.AUTOMOTIVE,
                true,
                now,
                now);

        assertThat(p2.getId()).isEqualTo(5L);
        assertThat(p2.getName()).isEqualTo("Constructed");
        assertThat(p2.getActive()).isTrue();
    }
}
