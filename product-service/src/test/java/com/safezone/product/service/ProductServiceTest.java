package com.safezone.product.service;

import com.safezone.common.exception.BusinessException;
import com.safezone.common.exception.ResourceNotFoundException;
import com.safezone.product.dto.CreateProductRequest;
import com.safezone.product.dto.ProductResponse;
import com.safezone.product.dto.UpdateProductRequest;
import com.safezone.product.entity.Product;
import com.safezone.product.entity.ProductCategory;
import com.safezone.product.mapper.ProductMapper;
import com.safezone.product.repository.ProductRepository;
import com.safezone.product.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product testProduct;
    private ProductResponse testProductResponse;
    private CreateProductRequest createRequest;

    @BeforeEach
    void setUp() {
        testProduct = Product.builder()
                .id(1L)
                .name("Test Product")
                .description("Test Description")
                .price(BigDecimal.valueOf(99.99))
                .stockQuantity(100)
                .sku("TEST-001")
                .category(ProductCategory.ELECTRONICS)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        testProductResponse = new ProductResponse(
                1L,
                "Test Product",
                "Test Description",
                BigDecimal.valueOf(99.99),
                100,
                "TEST-001",
                ProductCategory.ELECTRONICS,
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        createRequest = new CreateProductRequest(
                "Test Product",
                "Test Description",
                BigDecimal.valueOf(99.99),
                100,
                "TEST-001",
                ProductCategory.ELECTRONICS
        );
    }

    @Nested
    @DisplayName("Create Product Tests")
    class CreateProductTests {

        @Test
        @DisplayName("Should create product successfully")
        void shouldCreateProductSuccessfully() {
            given(productRepository.existsBySku(createRequest.sku())).willReturn(false);
            given(productMapper.toEntity(createRequest)).willReturn(testProduct);
            given(productRepository.save(any(Product.class))).willReturn(testProduct);
            given(productMapper.toResponse(testProduct)).willReturn(testProductResponse);

            ProductResponse result = productService.createProduct(createRequest);

            assertThat(result).isNotNull();
            assertThat(result.name()).isEqualTo(testProductResponse.name());
            assertThat(result.sku()).isEqualTo(testProductResponse.sku());
            verify(productRepository).save(any(Product.class));
        }

        @Test
        @DisplayName("Should throw exception for duplicate SKU")
        void shouldThrowExceptionForDuplicateSku() {
            given(productRepository.existsBySku(createRequest.sku())).willReturn(true);

            assertThatThrownBy(() -> productService.createProduct(createRequest))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("already exists");

            verify(productRepository, never()).save(any(Product.class));
        }
    }

    @Nested
    @DisplayName("Get Product Tests")
    class GetProductTests {

        @Test
        @DisplayName("Should get product by ID")
        void shouldGetProductById() {
            given(productRepository.findById(1L)).willReturn(Optional.of(testProduct));
            given(productMapper.toResponse(testProduct)).willReturn(testProductResponse);

            ProductResponse result = productService.getProductById(1L);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Should throw exception when product not found by ID")
        void shouldThrowExceptionWhenProductNotFoundById() {
            given(productRepository.findById(999L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> productService.getProductById(999L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Product not found");
        }

        @Test
        @DisplayName("Should get product by SKU")
        void shouldGetProductBySku() {
            given(productRepository.findBySku("TEST-001")).willReturn(Optional.of(testProduct));
            given(productMapper.toResponse(testProduct)).willReturn(testProductResponse);

            ProductResponse result = productService.getProductBySku("TEST-001");

            assertThat(result).isNotNull();
            assertThat(result.sku()).isEqualTo("TEST-001");
        }

        @Test
        @DisplayName("Should return paginated products")
        void shouldReturnPaginatedProducts() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Product> productPage = new PageImpl<>(List.of(testProduct), pageable, 1);

            given(productRepository.findAll(pageable)).willReturn(productPage);
            given(productMapper.toResponse(testProduct)).willReturn(testProductResponse);

            Page<ProductResponse> result = productService.getAllProducts(pageable);

            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getTotalElements()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("Update Product Tests")
    class UpdateProductTests {

        @Test
        @DisplayName("Should update product successfully")
        void shouldUpdateProductSuccessfully() {
            UpdateProductRequest updateRequest = new UpdateProductRequest(
                    "Updated Name",
                    null,
                    BigDecimal.valueOf(149.99),
                    null,
                    null,
                    null
            );

            given(productRepository.findById(1L)).willReturn(Optional.of(testProduct));
            given(productRepository.save(any(Product.class))).willReturn(testProduct);
            given(productMapper.toResponse(testProduct)).willReturn(testProductResponse);

            ProductResponse result = productService.updateProduct(1L, updateRequest);

            assertThat(result).isNotNull();
            verify(productRepository).save(any(Product.class));
        }

        @Test
        @DisplayName("Should soft delete product")
        void shouldSoftDeleteProduct() {
            given(productRepository.findById(1L)).willReturn(Optional.of(testProduct));
            given(productRepository.save(any(Product.class))).willReturn(testProduct);

            productService.deleteProduct(1L);

            assertThat(testProduct.getActive()).isFalse();
            verify(productRepository).save(testProduct);
        }
    }

    @Nested
    @DisplayName("Stock Management Tests")
    class StockManagementTests {

        @Test
        @DisplayName("Should update stock successfully")
        void shouldUpdateStockSuccessfully() {
            given(productRepository.findById(1L)).willReturn(Optional.of(testProduct));
            given(productRepository.save(any(Product.class))).willReturn(testProduct);
            given(productMapper.toResponse(testProduct)).willReturn(testProductResponse);

            ProductResponse result = productService.updateStock(1L, 50);

            assertThat(result).isNotNull();
            assertThat(testProduct.getStockQuantity()).isEqualTo(150);
        }

        @Test
        @DisplayName("Should throw exception for insufficient stock")
        void shouldThrowExceptionForInsufficientStock() {
            given(productRepository.findById(1L)).willReturn(Optional.of(testProduct));

            assertThatThrownBy(() -> productService.updateStock(1L, -150))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Insufficient stock");
        }

        @Test
        @DisplayName("Should check product availability correctly")
        void shouldCheckProductAvailability() {
            given(productRepository.findById(1L)).willReturn(Optional.of(testProduct));

            boolean available = productService.isProductAvailable(1L, 50);
            boolean notAvailable = productService.isProductAvailable(1L, 150);

            assertThat(available).isTrue();
            assertThat(notAvailable).isFalse();
        }

        @Test
        @DisplayName("Should return low stock products")
        void shouldReturnLowStockProducts() {
            Product lowStockProduct = Product.builder()
                    .id(2L)
                    .stockQuantity(5)
                    .active(true)
                    .build();

            given(productRepository.findLowStockActiveProducts(10))
                    .willReturn(List.of(lowStockProduct));
            given(productMapper.toResponseList(any())).willReturn(List.of(testProductResponse));

            List<ProductResponse> result = productService.getLowStockProducts(10);

            assertThat(result).hasSize(1);
        }
    }

    @Nested
    @DisplayName("Search and Filter Tests")
    class SearchAndFilterTests {

        @Test
        @DisplayName("Should search products by term")
        void shouldSearchProductsByTerm() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Product> productPage = new PageImpl<>(List.of(testProduct), pageable, 1);

            given(productRepository.searchProducts("test", pageable)).willReturn(productPage);
            given(productMapper.toResponse(testProduct)).willReturn(testProductResponse);

            Page<ProductResponse> result = productService.searchProducts("test", pageable);

            assertThat(result.getContent()).hasSize(1);
        }

        @Test
        @DisplayName("Should filter products by category")
        void shouldFilterProductsByCategory() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Product> productPage = new PageImpl<>(List.of(testProduct), pageable, 1);

            given(productRepository.findByCategoryAndActiveTrue(ProductCategory.ELECTRONICS, pageable))
                    .willReturn(productPage);
            given(productMapper.toResponse(testProduct)).willReturn(testProductResponse);

            Page<ProductResponse> result = productService.getProductsByCategory(ProductCategory.ELECTRONICS, pageable);

            assertThat(result.getContent()).hasSize(1);
        }
    }
}
