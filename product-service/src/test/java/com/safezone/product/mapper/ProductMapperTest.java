package com.safezone.product.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.safezone.product.dto.CreateProductRequest;
import com.safezone.product.dto.ProductResponse;
import com.safezone.product.entity.Product;
import com.safezone.product.entity.ProductCategory;

@SpringBootTest
@DisplayName("ProductMapper Tests")
class ProductMapperTest {

    @Autowired
    private ProductMapper mapper;

    @Test
    @DisplayName("toEntity maps request to product with ignored fields")
    void toEntityMapsRequest() {
        CreateProductRequest req = new CreateProductRequest(
                "Name",
                "Desc",
                BigDecimal.valueOf(10.50),
                5,
                "SKU-1",
                ProductCategory.ELECTRONICS);

        Product p = mapper.toEntity(req);

        assertThat(p.getId()).isNull();
        assertThat(p.getActive()).isNull();
        assertThat(p.getCreatedAt()).isNull();
        assertThat(p.getUpdatedAt()).isNull();
        assertThat(p.getName()).isEqualTo("Name");
        assertThat(p.getSku()).isEqualTo("SKU-1");
        assertThat(p.getCategory()).isEqualTo(ProductCategory.ELECTRONICS);
    }

    @Test
    @DisplayName("toResponse and toResponseList map entity to DTOs")
    void toResponseMapsEntity() {
        Product p = Product.builder()
                .id(2L)
                .name("P")
                .description("D")
                .price(BigDecimal.ONE)
                .stockQuantity(7)
                .sku("SKU-2")
                .category(ProductCategory.BOOKS)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        ProductResponse dto = mapper.toResponse(p);
        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(2L);
        assertThat(dto.sku()).isEqualTo("SKU-2");

        List<ProductResponse> list = mapper.toResponseList(new java.util.ArrayList<>(java.util.List.of(p)));
        assertThat(list).hasSize(1);
        assertThat(list.get(0).category()).isEqualTo(ProductCategory.BOOKS);
    }

    @Test
    @DisplayName("toEntity handles null request")
    void toEntityHandlesNullRequest() {
        Product p = mapper.toEntity(null);
        assertThat(p).isNull();
    }

    @Test
    @DisplayName("toResponse handles null product")
    void toResponseHandlesNullProduct() {
        ProductResponse dto = mapper.toResponse(null);
        assertThat(dto).isNull();
    }

    @Test
    @DisplayName("toResponseList handles null list")
    void toResponseListHandlesNullList() {
        List<ProductResponse> list = mapper.toResponseList(null);
        assertThat(list).isNull();
    }

    @Test
    @DisplayName("toResponseList handles empty list")
    void toResponseListHandlesEmptyList() {
        List<ProductResponse> list = mapper.toResponseList(new java.util.ArrayList<>());
        assertThat(list).isEmpty();
    }
}
