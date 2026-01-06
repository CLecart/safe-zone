package com.safezone.product.service.impl;

import com.safezone.common.exception.BusinessException;
import com.safezone.common.exception.ResourceNotFoundException;
import com.safezone.product.dto.CreateProductRequest;
import com.safezone.product.dto.ProductResponse;
import com.safezone.product.dto.UpdateProductRequest;
import com.safezone.product.entity.Product;
import com.safezone.product.entity.ProductCategory;
import com.safezone.product.mapper.ProductMapper;
import com.safezone.product.repository.ProductRepository;
import com.safezone.product.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of the {@link ProductService} interface.
 * Provides product management business logic with transactional support.
 *
 * <p>Handles product CRUD operations, stock management, and product search.
 * All write operations are transactional.</p>
 *
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2026-01-06
 */
@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);
    private static final String PRODUCT_RESOURCE = "Product";

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    /**
     * Constructs a ProductServiceImpl with required dependencies.
     *
     * @param productRepository repository for product persistence
     * @param productMapper     mapper for DTO/entity conversion
     */
    public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Override
    public ProductResponse createProduct(CreateProductRequest request) {
        logger.info("Creating new product with SKU: {}", request.sku());

        if (productRepository.existsBySku(request.sku())) {
            throw new BusinessException("DUPLICATE_SKU", "Product with SKU " + request.sku() + " already exists");
        }

        Product product = productMapper.toEntity(request);
        product.setActive(true);
        Product savedProduct = productRepository.save(product);

        logger.info("Product created successfully with ID: {}", savedProduct.getId());
        return productMapper.toResponse(savedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        logger.debug("Fetching product by ID: {}", id);
        Product product = findProductById(id);
        return productMapper.toResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductBySku(String sku) {
        logger.debug("Fetching product by SKU: {}", sku);
        Product product = productRepository.findBySku(sku)
                .orElseThrow(() -> new ResourceNotFoundException(PRODUCT_RESOURCE, "sku", sku));
        return productMapper.toResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        logger.debug("Fetching all products with pagination");
        return productRepository.findAll(pageable)
                .map(productMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getActiveProducts(Pageable pageable) {
        logger.debug("Fetching active products with pagination");
        return productRepository.findByActiveTrue(pageable)
                .map(productMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getProductsByCategory(ProductCategory category, Pageable pageable) {
        logger.debug("Fetching products by category: {}", category);
        return productRepository.findByCategoryAndActiveTrue(category, pageable)
                .map(productMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> searchProducts(String search, Pageable pageable) {
        logger.debug("Searching products with term: {}", search);
        return productRepository.searchProducts(search, pageable)
                .map(productMapper::toResponse);
    }

    @Override
    public ProductResponse updateProduct(Long id, UpdateProductRequest request) {
        logger.info("Updating product with ID: {}", id);

        Product product = findProductById(id);

        updateProductFields(product, request);
        Product updatedProduct = productRepository.save(product);

        logger.info("Product updated successfully with ID: {}", id);
        return productMapper.toResponse(updatedProduct);
    }

    @Override
    public void deleteProduct(Long id) {
        logger.info("Deleting product with ID: {}", id);

        Product product = findProductById(id);
        product.setActive(false);
        productRepository.save(product);

        logger.info("Product soft-deleted successfully with ID: {}", id);
    }

    @Override
    public ProductResponse updateStock(Long id, Integer quantity) {
        logger.info("Updating stock for product ID: {} with quantity: {}", id, quantity);

        Product product = findProductById(id);

        int newStock = product.getStockQuantity() + quantity;
        if (newStock < 0) {
            throw new BusinessException("INSUFFICIENT_STOCK",
                    "Insufficient stock. Available: " + product.getStockQuantity() + ", Requested: " + Math.abs(quantity));
        }

        product.setStockQuantity(newStock);
        Product updatedProduct = productRepository.save(product);

        logger.info("Stock updated for product ID: {}. New stock: {}", id, newStock);
        return productMapper.toResponse(updatedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getLowStockProducts(Integer threshold) {
        logger.debug("Fetching low stock products with threshold: {}", threshold);
        return productMapper.toResponseList(productRepository.findLowStockActiveProducts(threshold));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isProductAvailable(Long id, Integer quantity) {
        logger.debug("Checking availability for product ID: {} with quantity: {}", id, quantity);
        Product product = findProductById(id);
        return product.getActive() && product.getStockQuantity() >= quantity;
    }

    private Product findProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(PRODUCT_RESOURCE, "id", id));
    }

    private void updateProductFields(Product product, UpdateProductRequest request) {
        if (request.name() != null) {
            product.setName(request.name());
        }
        if (request.description() != null) {
            product.setDescription(request.description());
        }
        if (request.price() != null) {
            product.setPrice(request.price());
        }
        if (request.stockQuantity() != null) {
            product.setStockQuantity(request.stockQuantity());
        }
        if (request.category() != null) {
            product.setCategory(request.category());
        }
        if (request.active() != null) {
            product.setActive(request.active());
        }
    }
}
