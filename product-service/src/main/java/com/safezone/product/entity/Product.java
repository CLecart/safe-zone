package com.safezone.product.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing a product in the e-commerce catalog.
 * Contains all product information including pricing, inventory, and categorization.
 *
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2026-01-06
 */
@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    /** Unique identifier for the product. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Product display name. */
    @Column(nullable = false, length = 100)
    private String name;

    /** Detailed product description. */
    @Column(length = 1000)
    private String description;

    /** Product price with precision for currency calculations. */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    /** Current stock quantity available for sale. */
    @Column(nullable = false)
    private Integer stockQuantity;

    /** Unique Stock Keeping Unit identifier. */
    @Column(nullable = false, unique = true, length = 50)
    private String sku;

    /** Product category classification. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductCategory category;

    /** Indicates if the product is available for purchase. */
    @Column(nullable = false)
    private Boolean active;

    /** Timestamp when the product was created. */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Timestamp of the last product update. */
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * JPA lifecycle callback executed before persisting a new entity.
     * Sets creation and update timestamps, and default active status.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (active == null) {
            active = true;
        }
    }

    /**
     * JPA lifecycle callback executed before updating an existing entity.
     * Updates the modification timestamp.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
