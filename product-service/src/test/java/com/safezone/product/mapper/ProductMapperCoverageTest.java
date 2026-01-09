package com.safezone.product.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.safezone.product.dto.CreateProductRequest;
import com.safezone.product.dto.ProductResponse;
import com.safezone.product.entity.Product;
import com.safezone.product.entity.ProductCategory;

/**
 * Additional coverage tests for ProductMapper to reach 100%.
 * Tests null handling and edge cases in MapStruct-generated implementation.
 *
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2026-01-09
 */
@SpringBootTest
@DisplayName("ProductMapper Coverage Tests")
class ProductMapperCoverageTest {

    @Autowired
    private ProductMapper mapper;

    @Test
    @DisplayName("toEntity handles null description")
    void toEntityHandlesNullDescription() {
        CreateProductRequest req = new CreateProductRequest(
                "Name",
                null,
                BigDecimal.valueOf(10.0),
                5,
                "SKU-NULL",
                ProductCategory.TOYS);

        Product p = mapper.toEntity(req);

        assertThat(p.getDescription()).isNull();
        assertThat(p.getName()).isEqualTo("Name");
    }

    @Test
    @DisplayName("toResponse handles null description and active")
    void toResponseHandlesNulls() {
        Product p = Product.builder()
                .id(3L)
                .name("Minimal")
                .description(null)
                .price(BigDecimal.ONE)
                .stockQuantity(1)
                .sku("MIN-SKU")
                .category(ProductCategory.OTHER)
                .active(null)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        ProductResponse dto = mapper.toResponse(p);

        assertThat(dto.description()).isNull();
        assertThat(dto.active()).isNull();
        assertThat(dto.name()).isEqualTo("Minimal");
    }

    @Test
    @DisplayName("toResponseList handles empty list")
    void toResponseListHandlesEmptyList() {
        List<Product> empty = new ArrayList<>();
        List<ProductResponse> result = mapper.toResponseList(empty);
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("toResponseList handles multiple products")
    void toResponseListHandlesMultiple() {
        Product p1 = Product.builder()
                .id(1L)
                .name("P1")
                .price(BigDecimal.TEN)
                .stockQuantity(10)
                .sku("S1")
                .category(ProductCategory.ELECTRONICS)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Product p2 = Product.builder()
                .id(2L)
                .name("P2")
                .price(BigDecimal.ONE)
                .stockQuantity(5)
                .sku("S2")
                .category(ProductCategory.BOOKS)
                .active(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        List<Product> products = new ArrayList<>();
        products.add(p1);
        products.add(p2);

        List<ProductResponse> result = mapper.toResponseList(products);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).name()).isEqualTo("P1");
        assertThat(result.get(1).name()).isEqualTo("P2");
    }
}
