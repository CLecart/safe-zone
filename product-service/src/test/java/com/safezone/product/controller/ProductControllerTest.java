package com.safezone.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safezone.common.dto.ApiResponse;
import com.safezone.product.dto.CreateProductRequest;
import com.safezone.product.dto.ProductResponse;
import com.safezone.product.dto.UpdateProductRequest;
import com.safezone.product.entity.ProductCategory;
import com.safezone.product.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    private ProductResponse testProductResponse;
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
    @DisplayName("Should get products with pagination")
    void shouldGetProductsWithPagination() throws Exception {
        Page<ProductResponse> productPage = new PageImpl<>(
                List.of(testProductResponse),
                PageRequest.of(0, 20),
                1
        );
        given(productService.getAllProducts(any())).willReturn(productPage);

        mockMvc.perform(get("/api/v1/products")
                        .param("page", "0")
                        .param("size", "20"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should create product with admin role")
    void shouldCreateProductWithAdminRole() throws Exception {
        given(productService.createProduct(any(CreateProductRequest.class)))
                .willReturn(testProductResponse);

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Test Product"));
    }

    @Test
    @DisplayName("Should reject create product without authentication")
    void shouldRejectCreateProductWithoutAuth() throws Exception {
        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should reject create product with user role")
    void shouldRejectCreateProductWithUserRole() throws Exception {
        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
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
                null
        );

        given(productService.updateProduct(eq(1L), any(UpdateProductRequest.class)))
                .willReturn(testProductResponse);

        mockMvc.perform(put("/api/v1/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
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
        Page<ProductResponse> productPage = new PageImpl<>(
                List.of(testProductResponse),
                PageRequest.of(0, 20),
                1
        );
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
    @DisplayName("Should check product availability")
    void shouldCheckProductAvailability() throws Exception {
        given(productService.isProductAvailable(1L, 10)).willReturn(true);

        mockMvc.perform(get("/api/v1/products/1/availability")
                        .param("quantity", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(true));
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
                null
        );

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}
