package com.safezone.product.service;

import com.safezone.product.dto.CreateProductRequest;
import com.safezone.product.dto.ProductResponse;
import com.safezone.product.dto.UpdateProductRequest;
import com.safezone.product.entity.ProductCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {

    ProductResponse createProduct(CreateProductRequest request);

    ProductResponse getProductById(Long id);

    ProductResponse getProductBySku(String sku);

    Page<ProductResponse> getAllProducts(Pageable pageable);

    Page<ProductResponse> getActiveProducts(Pageable pageable);

    Page<ProductResponse> getProductsByCategory(ProductCategory category, Pageable pageable);

    Page<ProductResponse> searchProducts(String search, Pageable pageable);

    ProductResponse updateProduct(Long id, UpdateProductRequest request);

    void deleteProduct(Long id);

    ProductResponse updateStock(Long id, Integer quantity);

    List<ProductResponse> getLowStockProducts(Integer threshold);

    boolean isProductAvailable(Long id, Integer quantity);
}
