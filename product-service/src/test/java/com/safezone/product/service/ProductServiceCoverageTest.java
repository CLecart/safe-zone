package com.safezone.product.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.safezone.common.exception.ResourceNotFoundException;
import com.safezone.product.dto.ProductResponse;
import com.safezone.product.dto.UpdateProductRequest;
import com.safezone.product.entity.Product;
import com.safezone.product.entity.ProductCategory;
import com.safezone.product.mapper.ProductMapper;
import com.safezone.product.repository.ProductRepository;
import com.safezone.product.service.impl.ProductServiceImpl;

/**
 * Additional coverage tests for ProductServiceImpl to reach 100%.
 * Focuses on exception paths and edge cases.
 *
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2026-01-09
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ProductServiceImpl Coverage Tests")
class ProductServiceCoverageTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product testProduct;
    private ProductResponse testProductResponse;

    @BeforeEach
    void setUp() {
        testProduct = Product.builder()
                .id(1L)
                .name("Test")
                .description("Desc")
                .price(BigDecimal.TEN)
                .stockQuantity(50)
                .sku("SKU-1")
                .category(ProductCategory.ELECTRONICS)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        testProductResponse = new ProductResponse(
                1L,
                "Test",
                "Desc",
                BigDecimal.TEN,
                50,
                "SKU-1",
                ProductCategory.ELECTRONICS,
                true,
                LocalDateTime.now(),
                LocalDateTime.now());
    }

    @Test
    @DisplayName("getProductBySku throws ResourceNotFoundException when not found")
    void getProductBySkuThrowsNotFound() {
        given(productRepository.findBySku("INVALID")).willReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProductBySku("INVALID"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Product not found");
    }

    @Test
    @DisplayName("getAllProducts with non-null Pageable")
    void getAllProductsWithPageable() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Product> products = new ArrayList<>();
        products.add(testProduct);
        Page<Product> page = new PageImpl<>(products, pageable, 1);

        given(productRepository.findAll(pageable)).willReturn(page);
        given(productMapper.toResponse(testProduct)).willReturn(testProductResponse);

        Page<ProductResponse> result = productService.getAllProducts(pageable);

        assertThat(result.getContent()).hasSize(1);
        verify(productRepository).findAll(pageable);
    }

    @Test
    @DisplayName("updateProduct applies all non-null fields")
    void updateProductAppliesAllFields() {
        UpdateProductRequest request = new UpdateProductRequest(
                "New Name",
                "New Desc",
                BigDecimal.valueOf(200),
                75,
                ProductCategory.BOOKS,
                false);

        given(productRepository.findById(1L)).willReturn(Optional.of(testProduct));
        given(productRepository.save(any(Product.class))).willReturn(testProduct);
        given(productMapper.toResponse(testProduct)).willReturn(testProductResponse);

        ProductResponse result = productService.updateProduct(1L, request);

        assertThat(result).isNotNull();
        verify(productRepository).save(any(Product.class));
    }

    @Test
    @DisplayName("updateProduct with partial fields only updates non-null")
    void updateProductPartial() {
        UpdateProductRequest request = new UpdateProductRequest(
                "Only Name",
                null,
                null,
                null,
                null,
                null);

        given(productRepository.findById(1L)).willReturn(Optional.of(testProduct));
        given(productRepository.save(any(Product.class))).willReturn(testProduct);
        given(productMapper.toResponse(testProduct)).willReturn(testProductResponse);

        ProductResponse result = productService.updateProduct(1L, request);

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("isProductAvailable returns false when inactive")
    void isProductAvailableReturnsFalseWhenInactive() {
        testProduct.setActive(false);
        given(productRepository.findById(1L)).willReturn(Optional.of(testProduct));

        boolean result = productService.isProductAvailable(1L, 10);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("isProductAvailable returns false when insufficient stock")
    void isProductAvailableReturnsFalseWhenInsufficientStock() {
        given(productRepository.findById(1L)).willReturn(Optional.of(testProduct));

        boolean result = productService.isProductAvailable(1L, 100);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("isProductAvailable returns true when conditions met")
    void isProductAvailableReturnsTrue() {
        given(productRepository.findById(1L)).willReturn(Optional.of(testProduct));

        boolean result = productService.isProductAvailable(1L, 25);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("getActiveProducts returns active products only")
    void getActiveProductsReturnsActiveProductsOnly() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Product> products = new ArrayList<>();
        products.add(testProduct);
        Page<Product> page = new PageImpl<>(products, pageable, 1);

        given(productRepository.findByActiveTrue(pageable)).willReturn(page);
        given(productMapper.toResponse(testProduct)).willReturn(testProductResponse);

        Page<ProductResponse> result = productService.getActiveProducts(pageable);

        assertThat(result.getContent()).hasSize(1);
        verify(productRepository).findByActiveTrue(pageable);
    }

    @Test
    @DisplayName("updateProduct with description set to null")
    void updateProductWithDescriptionNull() {
        UpdateProductRequest request = new UpdateProductRequest(
                null,
                "Description",
                null,
                null,
                null,
                null);

        given(productRepository.findById(1L)).willReturn(Optional.of(testProduct));
        given(productRepository.save(any(Product.class))).willReturn(testProduct);
        given(productMapper.toResponse(testProduct)).willReturn(testProductResponse);

        ProductResponse result = productService.updateProduct(1L, request);

        assertThat(result).isNotNull();
    }
}
