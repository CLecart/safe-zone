package com.safezone.product.service;

import com.safezone.product.dto.CreateProductRequest;
import com.safezone.product.dto.ProductResponse;
import com.safezone.product.dto.UpdateProductRequest;
import com.safezone.product.entity.ProductCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for product management operations.
 * Provides CRUD operations and business logic for products in the e-commerce platform.
 *
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2026-01-06
 */
public interface ProductService {

    /**
     * Creates a new product in the catalog.
     *
     * @param request the product creation request containing product details
     * @return the created product response with generated ID
     * @throws com.safezone.common.exception.BusinessException if SKU already exists
     */
    ProductResponse createProduct(CreateProductRequest request);

    /**
     * Retrieves a product by its unique identifier.
     *
     * @param id the product ID
     * @return the product response
     * @throws com.safezone.common.exception.ResourceNotFoundException if product not found
     */
    ProductResponse getProductById(Long id);

    /**
     * Retrieves a product by its Stock Keeping Unit (SKU).
     *
     * @param sku the unique SKU identifier
     * @return the product response
     * @throws com.safezone.common.exception.ResourceNotFoundException if product not found
     */
    ProductResponse getProductBySku(String sku);

    /**
     * Retrieves all products with pagination support.
     *
     * @param pageable pagination parameters
     * @return a page of product responses
     */
    Page<ProductResponse> getAllProducts(Pageable pageable);

    /**
     * Retrieves only active products with pagination support.
     *
     * @param pageable pagination parameters
     * @return a page of active product responses
     */
    Page<ProductResponse> getActiveProducts(Pageable pageable);

    /**
     * Retrieves products filtered by category.
     *
     * @param category the product category to filter by
     * @param pageable pagination parameters
     * @return a page of product responses in the specified category
     */
    Page<ProductResponse> getProductsByCategory(ProductCategory category, Pageable pageable);

    /**
     * Searches products by name or description.
     *
     * @param search the search query string
     * @param pageable pagination parameters
     * @return a page of matching product responses
     */
    Page<ProductResponse> searchProducts(String search, Pageable pageable);

    /**
     * Updates an existing product.
     *
     * @param id the product ID to update
     * @param request the update request containing fields to modify
     * @return the updated product response
     * @throws com.safezone.common.exception.ResourceNotFoundException if product not found
     */
    ProductResponse updateProduct(Long id, UpdateProductRequest request);

    /**
     * Deletes a product from the catalog.
     *
     * @param id the product ID to delete
     * @throws com.safezone.common.exception.ResourceNotFoundException if product not found
     */
    void deleteProduct(Long id);

    /**
     * Updates the stock quantity of a product.
     *
     * @param id the product ID
     * @param quantity the quantity to add (positive) or remove (negative)
     * @return the updated product response
     * @throws com.safezone.common.exception.BusinessException if resulting stock would be negative
     */
    ProductResponse updateStock(Long id, Integer quantity);

    /**
     * Retrieves products with stock below the specified threshold.
     *
     * @param threshold the minimum stock level threshold
     * @return list of products with low stock
     */
    List<ProductResponse> getLowStockProducts(Integer threshold);

    /**
     * Checks if a product has sufficient stock for the requested quantity.
     *
     * @param id the product ID
     * @param quantity the requested quantity
     * @return true if product is available in requested quantity, false otherwise
     */
    boolean isProductAvailable(Long id, Integer quantity);
}
