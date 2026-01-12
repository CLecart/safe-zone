package com.safezone.product.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Comprehensive test suite for {@link Product} entity builder pattern.
 * 
 * <p>
 * <strong>Purpose:</strong>
 * Tests the Lombok-generated builder for Product entity to achieve 100% code
 * coverage.
 * Builder pattern enables flexible object construction with optional fields.
 * 
 * <p>
 * <strong>Coverage Strategy:</strong>
 * <ul>
 * <li>Test builder with all fields populated</li>
 * <li>Test builder with minimal required fields</li>
 * <li>Test builder with partial field combinations</li>
 * <li>Test lifecycle callbacks (@PrePersist, @PreUpdate)</li>
 * <li>Test all ProductCategory enum values</li>
 * <li>Test boolean active flag variations</li>
 * </ul>
 * 
 * <p>
 * <strong>Target:</strong> Achieve 100% instruction and branch coverage for
 * Product.ProductBuilder.
 * 
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2026-01-12
 * @see Product
 * @see Product.ProductBuilder
 * @see ProductCategory
 */
@DisplayName("Product Builder Pattern Tests")
class ProductBuilderTest {

    /**
     * Tests Product builder with all fields populated.
     * 
     * <p>
     * <strong>Scenario:</strong> Construct Product entity with complete data.
     * 
     * <p>
     * <strong>Given:</strong> All Product fields have values.
     * 
     * <p>
     * <strong>When:</strong> Builder builds Product instance.
     * 
     * <p>
     * <strong>Then:</strong> Product contains all specified field values.
     * 
     * <p>
     * <strong>Coverage:</strong> Tests all Product builder setter methods.
     */
    @Test
    @DisplayName("Should build product with all fields")
    void shouldBuildProductWithAllFields() {
        // Arrange: Prepare all field values
        LocalDateTime now = LocalDateTime.now();

        // Act: Build Product with all fields
        Product product = Product.builder()
                .id(1L)
                .name("Premium Laptop")
                .description("High-performance laptop with 16GB RAM and 512GB SSD")
                .price(new BigDecimal("1299.99"))
                .stockQuantity(50)
                .sku("LAPTOP-PREM-001")
                .category(ProductCategory.ELECTRONICS)
                .active(true)
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Assert: Verify all fields are set correctly
        assertThat(product.getId()).isEqualTo(1L);
        assertThat(product.getName()).isEqualTo("Premium Laptop");
        assertThat(product.getDescription()).isEqualTo("High-performance laptop with 16GB RAM and 512GB SSD");
        assertThat(product.getPrice()).isEqualByComparingTo("1299.99");
        assertThat(product.getStockQuantity()).isEqualTo(50);
        assertThat(product.getSku()).isEqualTo("LAPTOP-PREM-001");
        assertThat(product.getCategory()).isEqualTo(ProductCategory.ELECTRONICS);
        assertThat(product.getActive()).isTrue();
        assertThat(product.getCreatedAt()).isEqualTo(now);
        assertThat(product.getUpdatedAt()).isEqualTo(now);
    }

    /**
     * Tests Product builder with minimal required fields.
     * 
     * <p>
     * <strong>Scenario:</strong> Construct Product with only mandatory fields.
     * 
     * <p>
     * <strong>Given:</strong> Only name, price, stockQuantity, sku, category
     * provided.
     * 
     * <p>
     * <strong>When:</strong> Builder builds Product.
     * 
     * <p>
     * <strong>Then:</strong> Product has required fields, optionals are null.
     * 
     * <p>
     * <strong>Coverage:</strong> Tests minimal builder usage.
     */
    @Test
    @DisplayName("Should build product with minimal fields")
    void shouldBuildProductWithMinimalFields() {
        // Act: Build Product with minimal fields
        Product product = Product.builder()
                .name("Basic Item")
                .price(new BigDecimal("19.99"))
                .stockQuantity(100)
                .sku("BASIC-001")
                .category(ProductCategory.OTHER)
                .build();

        // Assert: Verify required fields and null optionals
        assertThat(product.getName()).isEqualTo("Basic Item");
        assertThat(product.getPrice()).isEqualByComparingTo("19.99");
        assertThat(product.getStockQuantity()).isEqualTo(100);
        assertThat(product.getSku()).isEqualTo("BASIC-001");
        assertThat(product.getCategory()).isEqualTo(ProductCategory.OTHER);
        assertThat(product.getDescription()).isNull();
        assertThat(product.getActive()).isNull();
    }

    /**
     * Tests Product builder with ELECTRONICS category.
     * 
     * <p>
     * <strong>Scenario:</strong> Build electronic product.
     * 
     * <p>
     * <strong>Given:</strong> Category=ELECTRONICS.
     * 
     * <p>
     * <strong>When:</strong> Builder builds Product.
     * 
     * <p>
     * <strong>Then:</strong> Product has ELECTRONICS category.
     * 
     * <p>
     * <strong>Coverage:</strong> Tests category builder with ELECTRONICS enum.
     */
    @Test
    @DisplayName("Should build product with ELECTRONICS category")
    void shouldBuildProductWithElectronicsCategory() {
        // Act: Build Product in ELECTRONICS category
        Product product = Product.builder()
                .name("Smartphone")
                .price(new BigDecimal("699.00"))
                .stockQuantity(30)
                .sku("PHONE-001")
                .category(ProductCategory.ELECTRONICS)
                .build();

        // Assert: Verify category
        assertThat(product.getCategory()).isEqualTo(ProductCategory.ELECTRONICS);
    }

    /**
     * Tests Product builder with CLOTHING category.
     * 
     * <p>
     * <strong>Scenario:</strong> Build clothing product.
     * 
     * <p>
     * <strong>Given:</strong> Category=CLOTHING.
     * 
     * <p>
     * <strong>When:</strong> Builder builds Product.
     * 
     * <p>
     * <strong>Then:</strong> Product has CLOTHING category.
     * 
     * <p>
     * <strong>Coverage:</strong> Tests category builder with CLOTHING enum.
     */
    @Test
    @DisplayName("Should build product with CLOTHING category")
    void shouldBuildProductWithClothingCategory() {
        // Act: Build Product in CLOTHING category
        Product product = Product.builder()
                .name("T-Shirt")
                .price(new BigDecimal("29.99"))
                .stockQuantity(200)
                .sku("TSHIRT-001")
                .category(ProductCategory.CLOTHING)
                .build();

        // Assert: Verify category
        assertThat(product.getCategory()).isEqualTo(ProductCategory.CLOTHING);
    }

    /**
     * Tests Product builder with BOOKS category.
     * 
     * <p>
     * <strong>Scenario:</strong> Build book product.
     * 
     * <p>
     * <strong>Given:</strong> Category=BOOKS.
     * 
     * <p>
     * <strong>When:</strong> Builder builds Product.
     * 
     * <p>
     * <strong>Then:</strong> Product has BOOKS category.
     * 
     * <p>
     * <strong>Coverage:</strong> Tests category builder with BOOKS enum.
     */
    @Test
    @DisplayName("Should build product with BOOKS category")
    void shouldBuildProductWithBooksCategory() {
        // Act: Build Product in BOOKS category
        Product product = Product.builder()
                .name("Java Programming Guide")
                .price(new BigDecimal("49.99"))
                .stockQuantity(75)
                .sku("BOOK-JAVA-001")
                .category(ProductCategory.BOOKS)
                .build();

        // Assert: Verify category
        assertThat(product.getCategory()).isEqualTo(ProductCategory.BOOKS);
    }

    /**
     * Tests Product builder with HOME category.
     * 
     * <p>
     * <strong>Scenario:</strong> Build home goods product.
     * 
     * <p>
     * <strong>Given:</strong> Category=HOME.
     * 
     * <p>
     * <strong>When:</strong> Builder builds Product.
     * 
     * <p>
     * <strong>Then:</strong> Product has HOME_GARDEN category.
     * 
     * <p>
     * <strong>Coverage:</strong> Tests category builder with HOME_GARDEN enum.
     */
    @Test
    @DisplayName("Should build product with HOME_GARDEN category")
    void shouldBuildProductWithHomeGardenCategory() {
        // Act: Build Product in HOME_GARDEN category
        Product product = Product.builder()
                .name("Coffee Maker")
                .price(new BigDecimal("79.99"))
                .stockQuantity(40)
                .sku("HOME-COFFEE-001")
                .category(ProductCategory.HOME_GARDEN)
                .build();

        // Assert: Verify category
        assertThat(product.getCategory()).isEqualTo(ProductCategory.HOME_GARDEN);
    }

    /**
     * Tests Product builder with SPORTS category.
     * 
     * <p>
     * <strong>Scenario:</strong> Build sports equipment product.
     * 
     * <p>
     * <strong>Given:</strong> Category=SPORTS.
     * 
     * <p>
     * <strong>When:</strong> Builder builds Product.
     * 
     * <p>
     * <strong>Then:</strong> Product has SPORTS category.
     * 
     * <p>
     * <strong>Coverage:</strong> Tests category builder with SPORTS enum.
     */
    @Test
    @DisplayName("Should build product with SPORTS category")
    void shouldBuildProductWithSportsCategory() {
        // Act: Build Product in SPORTS category
        Product product = Product.builder()
                .name("Tennis Racket")
                .price(new BigDecimal("129.99"))
                .stockQuantity(25)
                .sku("SPORT-TENNIS-001")
                .category(ProductCategory.SPORTS)
                .build();

        // Assert: Verify category
        assertThat(product.getCategory()).isEqualTo(ProductCategory.SPORTS);
    }

    /**
     * Tests Product builder with active flag set to true.
     * 
     * <p>
     * <strong>Scenario:</strong> Build active product.
     * 
     * <p>
     * <strong>Given:</strong> Active=true.
     * 
     * <p>
     * <strong>When:</strong> Builder builds Product.
     * 
     * <p>
     * <strong>Then:</strong> Product is active.
     * 
     * <p>
     * <strong>Coverage:</strong> Tests active builder method with true.
     */
    @Test
    @DisplayName("Should build active product")
    void shouldBuildActiveProduct() {
        // Act: Build active Product
        Product product = Product.builder()
                .name("Active Product")
                .price(BigDecimal.TEN)
                .stockQuantity(1)
                .sku("ACTIVE-001")
                .category(ProductCategory.OTHER)
                .active(true)
                .build();

        // Assert: Verify active flag
        assertThat(product.getActive()).isTrue();
    }

    /**
     * Tests Product builder with active flag set to false.
     * 
     * <p>
     * <strong>Scenario:</strong> Build inactive product.
     * 
     * <p>
     * <strong>Given:</strong> Active=false.
     * 
     * <p>
     * <strong>When:</strong> Builder builds Product.
     * 
     * <p>
     * <strong>Then:</strong> Product is inactive.
     * 
     * <p>
     * <strong>Coverage:</strong> Tests active builder method with false.
     */
    @Test
    @DisplayName("Should build inactive product")
    void shouldBuildInactiveProduct() {
        // Act: Build inactive Product
        Product product = Product.builder()
                .name("Inactive Product")
                .price(BigDecimal.TEN)
                .stockQuantity(0)
                .sku("INACTIVE-001")
                .category(ProductCategory.OTHER)
                .active(false)
                .build();

        // Assert: Verify active flag is false
        assertThat(product.getActive()).isFalse();
    }

    /**
     * Tests Product builder with description field.
     * 
     * <p>
     * <strong>Scenario:</strong> Build product with detailed description.
     * 
     * <p>
     * <strong>Given:</strong> Description text provided.
     * 
     * <p>
     * <strong>When:</strong> Builder builds Product.
     * 
     * <p>
     * <strong>Then:</strong> Product has specified description.
     * 
     * <p>
     * <strong>Coverage:</strong> Tests description builder method.
     */
    @Test
    @DisplayName("Should build product with description")
    void shouldBuildProductWithDescription() {
        // Act: Build Product with description
        String desc = "This is a detailed product description with specifications and features.";
        Product product = Product.builder()
                .name("Described Product")
                .description(desc)
                .price(BigDecimal.TEN)
                .stockQuantity(1)
                .sku("DESC-001")
                .category(ProductCategory.OTHER)
                .build();

        // Assert: Verify description
        assertThat(product.getDescription()).isEqualTo(desc);
    }

    /**
     * Tests Product builder with various stock quantities.
     * 
     * <p>
     * <strong>Scenario:</strong> Build products with different inventory levels.
     * 
     * <p>
     * <strong>Given:</strong> Various stockQuantity values.
     * 
     * <p>
     * <strong>When:</strong> Builder builds Products.
     * 
     * <p>
     * <strong>Then:</strong> Products have specified stock quantities.
     * 
     * <p>
     * <strong>Coverage:</strong> Tests stockQuantity builder method.
     */
    @Test
    @DisplayName("Should build product with various stock quantities")
    void shouldBuildProductWithVariousStockQuantities() {
        // Act & Assert: Test zero stock
        Product zeroStock = Product.builder()
                .name("Out of Stock")
                .price(BigDecimal.TEN)
                .stockQuantity(0)
                .sku("ZERO-001")
                .category(ProductCategory.OTHER)
                .build();
        assertThat(zeroStock.getStockQuantity()).isZero();

        // Act & Assert: Test single item
        Product singleStock = Product.builder()
                .name("Last Item")
                .price(BigDecimal.TEN)
                .stockQuantity(1)
                .sku("SINGLE-001")
                .category(ProductCategory.OTHER)
                .build();
        assertThat(singleStock.getStockQuantity()).isEqualTo(1);

        // Act & Assert: Test large quantity
        Product bulkStock = Product.builder()
                .name("Bulk Item")
                .price(BigDecimal.TEN)
                .stockQuantity(1000)
                .sku("BULK-001")
                .category(ProductCategory.OTHER)
                .build();
        assertThat(bulkStock.getStockQuantity()).isEqualTo(1000);
    }

    /**
     * Tests Product builder with various price points.
     * 
     * <p>
     * <strong>Scenario:</strong> Build products with different price values.
     * 
     * <p>
     * <strong>Given:</strong> Various price BigDecimal values.
     * 
     * <p>
     * <strong>When:</strong> Builder builds Products.
     * 
     * <p>
     * <strong>Then:</strong> Products have specified prices.
     * 
     * <p>
     * <strong>Coverage:</strong> Tests price builder method with various decimals.
     */
    @Test
    @DisplayName("Should build product with various prices")
    void shouldBuildProductWithVariousPrices() {
        // Act & Assert: Test low price
        Product cheap = Product.builder()
                .name("Cheap Item")
                .price(new BigDecimal("0.99"))
                .stockQuantity(1)
                .sku("CHEAP-001")
                .category(ProductCategory.OTHER)
                .build();
        assertThat(cheap.getPrice()).isEqualByComparingTo("0.99");

        // Act & Assert: Test high price
        Product expensive = Product.builder()
                .name("Expensive Item")
                .price(new BigDecimal("9999.99"))
                .stockQuantity(1)
                .sku("EXPENSIVE-001")
                .category(ProductCategory.OTHER)
                .build();
        assertThat(expensive.getPrice()).isEqualByComparingTo("9999.99");
    }

    /**
     * Tests Product builder with timestamps.
     * 
     * <p>
     * <strong>Scenario:</strong> Build product with explicit timestamps.
     * 
     * <p>
     * <strong>Given:</strong> CreatedAt and updatedAt timestamps provided.
     * 
     * <p>
     * <strong>When:</strong> Builder builds Product.
     * 
     * <p>
     * <strong>Then:</strong> Product has specified timestamps.
     * 
     * <p>
     * <strong>Coverage:</strong> Tests timestamp builder methods.
     */
    @Test
    @DisplayName("Should build product with timestamps")
    void shouldBuildProductWithTimestamps() {
        // Arrange: Create timestamps
        LocalDateTime created = LocalDateTime.of(2026, 1, 1, 10, 0);
        LocalDateTime updated = LocalDateTime.of(2026, 1, 12, 15, 30);

        // Act: Build Product with timestamps
        Product product = Product.builder()
                .name("Timestamped Product")
                .price(BigDecimal.TEN)
                .stockQuantity(1)
                .sku("TIME-001")
                .category(ProductCategory.OTHER)
                .createdAt(created)
                .updatedAt(updated)
                .build();

        // Assert: Verify timestamps
        assertThat(product.getCreatedAt()).isEqualTo(created);
        assertThat(product.getUpdatedAt()).isEqualTo(updated);
    }

    /**
     * Tests @PrePersist lifecycle callback.
     * 
     * <p>
     * <strong>Scenario:</strong> JPA callback sets default values before insert.
     * 
     * <p>
     * <strong>Given:</strong> Product built without active flag and timestamps.
     * 
     * <p>
     * <strong>When:</strong> onCreate() is called manually (simulates JPA).
     * 
     * <p>
     * <strong>Then:</strong> Default values are set (active=true, timestamps=now).
     * 
     * <p>
     * <strong>Coverage:</strong> Tests onCreate() lifecycle method.
     */
    @Test
    @DisplayName("Should set default values on @PrePersist")
    void shouldSetDefaultValuesOnPrePersist() {
        // Arrange: Build Product without defaults
        Product product = Product.builder()
                .name("New Product")
                .price(BigDecimal.TEN)
                .stockQuantity(1)
                .sku("NEW-001")
                .category(ProductCategory.OTHER)
                .build();

        // Act: Simulate @PrePersist
        product.onCreate();

        // Assert: Verify default values are set
        assertThat(product.getActive()).isTrue();
        assertThat(product.getCreatedAt()).isNotNull();
        assertThat(product.getUpdatedAt()).isNotNull();
    }

    /**
     * Tests @PreUpdate lifecycle callback.
     * 
     * <p>
     * <strong>Scenario:</strong> JPA callback updates timestamp before update.
     * 
     * <p>
     * <strong>Given:</strong> Product with old updatedAt timestamp.
     * 
     * <p>
     * <strong>When:</strong> onUpdate() is called manually (simulates JPA).
     * 
     * <p>
     * <strong>Then:</strong> UpdatedAt timestamp is refreshed to current time.
     * 
     * <p>
     * <strong>Coverage:</strong> Tests onUpdate() lifecycle method.
     */
    @Test
    @DisplayName("Should refresh timestamp on @PreUpdate")
    void shouldRefreshTimestampOnPreUpdate() {
        // Arrange: Build Product with old timestamp
        LocalDateTime oldTimestamp = LocalDateTime.of(2026, 1, 1, 10, 0);
        Product product = Product.builder()
                .name("Update Product")
                .price(BigDecimal.TEN)
                .stockQuantity(1)
                .sku("UPDATE-001")
                .category(ProductCategory.OTHER)
                .updatedAt(oldTimestamp)
                .build();

        // Act: Simulate @PreUpdate
        product.onUpdate();

        // Assert: Verify updatedAt is refreshed
        assertThat(product.getUpdatedAt()).isNotEqualTo(oldTimestamp);
        assertThat(product.getUpdatedAt()).isAfter(oldTimestamp);
    }
}
