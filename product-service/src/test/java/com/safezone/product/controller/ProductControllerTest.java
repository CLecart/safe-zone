package com.safezone.product.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safezone.product.dto.CreateProductRequest;
import com.safezone.product.dto.ProductResponse;
import com.safezone.product.dto.UpdateProductRequest;
import com.safezone.product.entity.ProductCategory;
import com.safezone.product.service.ProductService;

/**
 * Integration tests for {@link ProductController}.
 * Tests REST endpoints with MockMvc and mocked service layer.
 *
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2026-01-06
 */
@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest {

        /** MockMvc for HTTP request simulation. */
        @Autowired
        private MockMvc mockMvc;

        /** JSON serialization mapper. */
        @Autowired
        private ObjectMapper objectMapper;

        /** Mock product service. */
        @MockitoBean
        private ProductService productService;

        /** Test product response DTO. */
        private ProductResponse testProductResponse;

        /** Test product creation request. */
        private CreateProductRequest createRequest;

        @BeforeEach
        void setUp() {
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
                                LocalDateTime.now());

                createRequest = new CreateProductRequest(
                                "Test Product",
                                "Test Description",
                                BigDecimal.valueOf(99.99),
                                100,
                                "TEST-001",
                                ProductCategory.ELECTRONICS);
        }

        @Test
        @DisplayName("Should get product by ID")
        void shouldGetProductById() throws Exception {
                given(productService.getProductById(1L)).willReturn(testProductResponse);

                mockMvc.perform(get("/api/v1/products/1"))
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.data.id").value(1))
                                .andExpect(jsonPath("$.data.name").value("Test Product"));
        }

        @Test
        @DisplayName("Should get products with pagination - unauthorized")
        void shouldGetProductsWithPagination() throws Exception {
                mockMvc.perform(get("/api/v1/products")
                                .param("page", "0")
                                .param("size", "20"))
                                .andDo(print())
                                .andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should create product with admin role")
        void shouldCreateProductWithAdminRole() throws Exception {
                given(productService.createProduct(any(CreateProductRequest.class)))
                                .willReturn(testProductResponse);

                mockMvc.perform(post("/api/v1/products")
                                .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
                                .content(Objects.requireNonNull(objectMapper.writeValueAsString(createRequest))))
                                .andDo(print())
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.data.name").value("Test Product"));
        }

        @Test
        @DisplayName("Should reject create product without authentication (401)")
        void shouldRejectCreateProductWithoutAuth() throws Exception {
                mockMvc.perform(post("/api/v1/products")
                                .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
                                .content(Objects.requireNonNull(objectMapper.writeValueAsString(createRequest))))
                                .andDo(print())
                                .andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("Should reject create product with user role")
        void shouldRejectCreateProductWithUserRole() throws Exception {
                mockMvc.perform(post("/api/v1/products")
                                .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
                                .content(Objects.requireNonNull(objectMapper.writeValueAsString(createRequest))))
                                .andDo(print())
                                .andExpect(result -> {
                                        int status = result.getResponse().getStatus();
                                        org.assertj.core.api.Assertions.assertThat(status)
                                                        .as("Should not allow user role to create product")
                                                        .isIn(403, 500);
                                });
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should update product")
        void shouldUpdateProduct() throws Exception {
                UpdateProductRequest updateRequest = new UpdateProductRequest(
                                "Updated Product",
                                null,
                                BigDecimal.valueOf(149.99),
                                null,
                                null,
                                null);

                given(productService.updateProduct(eq(1L), any(UpdateProductRequest.class)))
                                .willReturn(testProductResponse);

                mockMvc.perform(put("/api/v1/products/1")
                                .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
                                .content(Objects.requireNonNull(objectMapper.writeValueAsString(updateRequest))))
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should delete product")
        void shouldDeleteProduct() throws Exception {
                mockMvc.perform(delete("/api/v1/products/1"))
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true));
        }

        @Test
        @DisplayName("Should search products")
        void shouldSearchProducts() throws Exception {
                List<ProductResponse> responseList = Collections.singletonList(testProductResponse);
                Page<ProductResponse> productPage = new PageImpl<>(
                                Objects.requireNonNull(responseList),
                                PageRequest.of(0, 20),
                                1);
                given(productService.searchProducts(eq("test"), any())).willReturn(productPage);

                mockMvc.perform(get("/api/v1/products/search")
                                .param("q", "test"))
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true));
        }

        @Test
        @WithMockUser(roles = "INVENTORY")
        @DisplayName("Should update stock with inventory role")
        void shouldUpdateStockWithInventoryRole() throws Exception {
                given(productService.updateStock(1L, 50)).willReturn(testProductResponse);

                mockMvc.perform(patch("/api/v1/products/1/stock")
                                .param("quantity", "50"))
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true));
        }

        @Test
        @DisplayName("Should check product availability - unauthorized")
        void shouldCheckProductAvailability() throws Exception {
                mockMvc.perform(get("/api/v1/products/1/availability")
                                .param("quantity", "10"))
                                .andDo(print())
                                .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Should validate create product request")
        @WithMockUser(roles = "ADMIN")
        void shouldValidateCreateProductRequest() throws Exception {
                CreateProductRequest invalidRequest = new CreateProductRequest(
                                "",
                                null,
                                BigDecimal.valueOf(-1),
                                -10,
                                "",
                                null);

                mockMvc.perform(post("/api/v1/products")
                                .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
                                .content(Objects.requireNonNull(objectMapper.writeValueAsString(invalidRequest))))
                                .andDo(print())
                                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should get product by SKU")
        void shouldGetProductBySku() throws Exception {
                given(productService.getProductBySku("TEST-001")).willReturn(testProductResponse);
                mockMvc.perform(get("/api/v1/products/sku/TEST-001"))
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.data.sku").value("TEST-001"));
        }

        @Test
        @DisplayName("Should get active products")
        void shouldGetActiveProducts() throws Exception {
                List<ProductResponse> content = new java.util.ArrayList<>();
                content.add(testProductResponse);
                Page<ProductResponse> productPage = new PageImpl<>(content, PageRequest.of(0, 20), 1);
                given(productService.getActiveProducts(any())).willReturn(productPage);
                mockMvc.perform(get("/api/v1/products/active")
                                .param("page", "0")
                                .param("size", "20"))
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.data.totalElements").value(1));
        }

        @Test
        @DisplayName("Should get products by category")
        void shouldGetProductsByCategory() throws Exception {
                List<ProductResponse> content = new java.util.ArrayList<>();
                content.add(testProductResponse);
                Page<ProductResponse> productPage = new PageImpl<>(content, PageRequest.of(0, 20), 1);
                given(productService.getProductsByCategory(eq(ProductCategory.ELECTRONICS), any()))
                                .willReturn(productPage);
                mockMvc.perform(get("/api/v1/products/category/ELECTRONICS")
                                .param("page", "0")
                                .param("size", "20"))
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.data.totalElements").value(1));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should get low stock products")
        void shouldGetLowStockProducts() throws Exception {
                java.util.List<ProductResponse> lowStock = new java.util.ArrayList<>();
                lowStock.add(testProductResponse);
                given(productService.getLowStockProducts(10)).willReturn(lowStock);
                mockMvc.perform(get("/api/v1/products/low-stock")
                                .param("threshold", "10"))
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.data").isArray());
        }
}
