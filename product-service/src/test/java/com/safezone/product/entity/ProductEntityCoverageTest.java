package com.safezone.product.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Coverage tests for Product entity to reach 100%.
 *
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2026-01-12
 */
@DisplayName("Product Entity Coverage Tests")
class ProductEntityCoverageTest {

    @Test
    @DisplayName("Product builder creates valid instance")
    void productBuilderCreatesValidInstance() {
        LocalDateTime now = LocalDateTime.now();

        Product product = Product.builder()
                .id(1L)
                .name("Test Product")
                .description("Description")
                .price(BigDecimal.valueOf(99.99))
                .stockQuantity(100)
                .sku("TEST-001")
                .category(ProductCategory.ELECTRONICS)
                .active(true)
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertThat(product.getId()).isEqualTo(1L);
        assertThat(product.getName()).isEqualTo("Test Product");
        assertThat(product.getDescription()).isEqualTo("Description");
        assertThat(product.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(99.99));
        assertThat(product.getStockQuantity()).isEqualTo(100);
        assertThat(product.getSku()).isEqualTo("TEST-001");
        assertThat(product.getCategory()).isEqualTo(ProductCategory.ELECTRONICS);
        assertThat(product.getActive()).isTrue();
        assertThat(product.getCreatedAt()).isEqualTo(now);
        assertThat(product.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Product setters modify fields correctly")
    void productSettersModifyFieldsCorrectly() {
        Product product = new Product();
        LocalDateTime now = LocalDateTime.now();

        product.setId(2L);
        product.setName("Updated");
        product.setDescription("New Desc");
        product.setPrice(BigDecimal.TEN);
        product.setStockQuantity(50);
        product.setSku("SKU-002");
        product.setCategory(ProductCategory.BOOKS);
        product.setActive(false);
        product.setCreatedAt(now);
        product.setUpdatedAt(now);

        assertThat(product.getId()).isEqualTo(2L);
        assertThat(product.getName()).isEqualTo("Updated");
        assertThat(product.getDescription()).isEqualTo("New Desc");
        assertThat(product.getPrice()).isEqualByComparingTo(BigDecimal.TEN);
        assertThat(product.getStockQuantity()).isEqualTo(50);
        assertThat(product.getSku()).isEqualTo("SKU-002");
        assertThat(product.getCategory()).isEqualTo(ProductCategory.BOOKS);
        assertThat(product.getActive()).isFalse();
        assertThat(product.getCreatedAt()).isEqualTo(now);
        assertThat(product.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Product all-args constructor initializes correctly")
    void productAllArgsConstructorInitializesCorrectly() {
        LocalDateTime now = LocalDateTime.now();

        Product product = new Product(
                3L,
                "All Args",
                "Test Description",
                BigDecimal.valueOf(50),
                200,
                "ALLARGS-001",
                ProductCategory.ELECTRONICS,
                true,
                now,
                now);

        assertThat(product.getId()).isEqualTo(3L);
        assertThat(product.getName()).isEqualTo("All Args");
        assertThat(product.getDescription()).isEqualTo("Test Description");
        assertThat(product.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(50));
        assertThat(product.getStockQuantity()).isEqualTo(200);
        assertThat(product.getSku()).isEqualTo("ALLARGS-001");
        assertThat(product.getCategory()).isEqualTo(ProductCategory.ELECTRONICS);
        assertThat(product.getActive()).isTrue();
        assertThat(product.getCreatedAt()).isEqualTo(now);
        assertThat(product.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Product no-args constructor creates empty instance")
    void productNoArgsConstructorCreatesEmptyInstance() {
        Product product = new Product();

        assertThat(product.getId()).isNull();
        assertThat(product.getName()).isNull();
        assertThat(product.getDescription()).isNull();
        assertThat(product.getPrice()).isNull();
        assertThat(product.getStockQuantity()).isNull();
        assertThat(product.getSku()).isNull();
        assertThat(product.getCategory()).isNull();
        assertThat(product.getActive()).isNull();
        assertThat(product.getCreatedAt()).isNull();
        assertThat(product.getUpdatedAt()).isNull();
    }

    @Test
    @DisplayName("Product onCreate() sets timestamps and active flag")
    void productOnCreateSetsTimestamps() {
        Product product = new Product();
        LocalDateTime beforeCreate = LocalDateTime.now();

        product.onCreate();

        LocalDateTime afterCreate = LocalDateTime.now();
        assertThat(product.getCreatedAt()).isNotNull();
        assertThat(product.getUpdatedAt()).isNotNull();
        assertThat(product.getCreatedAt()).isBetween(beforeCreate, afterCreate);
        assertThat(product.getUpdatedAt()).isBetween(beforeCreate, afterCreate);
        assertThat(product.getActive()).isTrue();
    }

    @Test
    @DisplayName("Product onUpdate() refreshes timestamp")
    void productOnUpdateRefreshesTimestamp() {
        Product product = new Product();
        LocalDateTime createdTime = LocalDateTime.now().minusSeconds(5);
        product.setCreatedAt(createdTime);
        product.setUpdatedAt(createdTime);

        LocalDateTime beforeUpdate = LocalDateTime.now();
        product.onUpdate();
        LocalDateTime afterUpdate = LocalDateTime.now();

        assertThat(product.getCreatedAt()).isEqualTo(createdTime);
        assertThat(product.getUpdatedAt()).isNotNull().isAfter(createdTime);
        assertThat(product.getUpdatedAt()).isBetween(beforeUpdate, afterUpdate);
    }
}
