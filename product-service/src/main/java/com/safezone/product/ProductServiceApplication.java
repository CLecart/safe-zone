package com.safezone.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Product Service microservice.
 * Manages product catalog operations including CRUD and inventory management.
 *
 * <p>Scans both product-specific and common module packages for components.</p>
 *
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2026-01-06
 */
@SpringBootApplication(scanBasePackages = {"com.safezone.product", "com.safezone.common"})
public class ProductServiceApplication {

    /**
     * Application entry point.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);
    }
}
