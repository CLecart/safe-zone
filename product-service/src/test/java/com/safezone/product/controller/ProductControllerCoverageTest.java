package com.safezone.product.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.safezone.product.dto.ProductResponse;
import com.safezone.product.entity.ProductCategory;
import com.safezone.product.service.ProductService;

/**
 * Additional tests for ProductController to reach 100% coverage.
 * Focuses on sort direction branches and edge cases.
 *
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2026-01-09
 */
@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerCoverageTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    private ProductResponse testProductResponse;

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
    }

    @Test
    @DisplayName("Should get all products with descending sort")
    void shouldGetAllProductsWithDescSort() throws Exception {
        List<ProductResponse> content = new ArrayList<>();
        content.add(testProductResponse);
        Page<ProductResponse> productPage = new PageImpl<>(content, PageRequest.of(0, 20), 1);
        given(productService.getAllProducts(any())).willReturn(productPage);

        mockMvc.perform(get("/api/v1/products")
                .param("page", "0")
                .param("size", "20")
                .param("sortBy", "name")
                .param("sortDir", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should get active products with descending sort")
    void shouldGetActiveProductsWithDescSort() throws Exception {
        List<ProductResponse> content = new ArrayList<>();
        content.add(testProductResponse);
        Page<ProductResponse> productPage = new PageImpl<>(content, PageRequest.of(0, 20), 1);
        given(productService.getActiveProducts(any())).willReturn(productPage);

        mockMvc.perform(get("/api/v1/products/active")
                .param("page", "0")
                .param("size", "20")
                .param("sortBy", "price")
                .param("sortDir", "DESC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
