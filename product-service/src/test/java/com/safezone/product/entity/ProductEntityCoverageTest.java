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
    @DisplayName("Product AllArgsConstructor creates valid instance")
    void productAllArgsConstructorCreatesValidInstance() {
        LocalDateTime now = LocalDateTime.now();

        Product product = new Product(
                3L,
                "Constructor Product",
                "Constructed",
                BigDecimal.valueOf(199.99),
                75,
                "CONS-001",
                ProductCategory.CLOTHING,
                true,
                now,
                now);

        assertThat(product.getId()).isEqualTo(3L);
        assertThat(product.getName()).isEqualTo("Constructor Product");
        assertThat(product.getDescription()).isEqualTo("Constructed");
        assertThat(product.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(199.99));
        assertThat(product.getStockQuantity()).isEqualTo(75);
        assertThat(product.getSku()).isEqualTo("CONS-001");
        assertThat(product.getCategory()).isEqualTo(ProductCategory.CLOTHING);
        assertThat(product.getActive()).isTrue();
    }

    @Test
    @DisplayName("Product NoArgsConstructor creates empty instance")
    void productNoArgsConstructorCreatesEmptyInstance() {
        Product product = new Product();

        assertThat(product).isNotNull();
        assertThat(product.getId()).isNull();
        assertThat(product.getName()).isNull();
    }

    @Test
    @DisplayName("PrePersist sets createdAt and updatedAt")
    void prePersistSetsTimestamps() {
        Product product = new Product();
        product.onCreate();

        assertThat(product.getCreatedAt()).isNotNull();
        assertThat(product.getUpdatedAt()).isNotNull();
        assertThat(product.getActive()).isTrue();
    }

    @Test
    @DisplayName("PreUpdate updates updatedAt timestamp")
    void preUpdateUpdatesTimestamp() {
        Product product = new Product();
        product.onCreate();
        LocalDateTime originalUpdatedAt = product.getUpdatedAt();

        // Simulate time passing
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        product.onUpdate();

        assertThat(product.getUpdatedAt()).isAfter(originalUpdatedAt);
    }
}
