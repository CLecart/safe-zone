package com.safezone.product.repository;

import com.safezone.product.entity.Product;
import com.safezone.product.entity.ProductCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for Product entities.
 * Provides CRUD operations and custom queries for product management.
 *
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2026-01-06
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Finds a product by its SKU.
     *
     * @param sku the Stock Keeping Unit
     * @return an Optional containing the product if found
     */
    Optional<Product> findBySku(String sku);

    /**
     * Checks if a product with the given SKU exists.
     *
     * @param sku the Stock Keeping Unit
     * @return true if a product with this SKU exists
     */
    boolean existsBySku(String sku);

    /**
     * Finds all active products with pagination.
     *
     * @param pageable pagination parameters
     * @return page of active products
     */
    Page<Product> findByActiveTrue(Pageable pageable);

    /**
     * Finds products by category with pagination.
     *
     * @param category the product category
     * @param pageable pagination parameters
     * @return page of products in the category
     */
    Page<Product> findByCategory(ProductCategory category, Pageable pageable);

    /**
     * Finds active products by category with pagination.
     *
     * @param category the product category
     * @param pageable pagination parameters
     * @return page of active products in the category
     */
    Page<Product> findByCategoryAndActiveTrue(ProductCategory category, Pageable pageable);

    /**
     * Searches products by name or description.
     *
     * @param search   the search term
     * @param pageable pagination parameters
     * @return page of matching products
     */
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Product> searchProducts(@Param("search") String search, Pageable pageable);

    /**
     * Finds products with stock below a threshold.
     *
     * @param threshold the stock threshold
     * @return list of low-stock products
     */
    List<Product> findByStockQuantityLessThan(Integer threshold);

    /**
     * Finds active products with stock below a threshold.
     *
     * @param threshold the stock threshold
     * @return list of active low-stock products
     */
    @Query("SELECT p FROM Product p WHERE p.stockQuantity < :threshold AND p.active = true")
    List<Product> findLowStockActiveProducts(@Param("threshold") Integer threshold);
}
