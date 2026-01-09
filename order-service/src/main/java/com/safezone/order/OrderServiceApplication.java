package com.safezone.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

import com.safezone.order.config.ApplicationProperties;
import com.safezone.order.config.JwtProperties;
import com.safezone.order.config.ServiceProperties;

/**
 * Main application class for the Order microservice.
 * <p>
 * This service handles all order-related operations including order creation,
 * status management, and order history retrieval. It communicates with the
 * Product Service to validate product availability and update stock levels.
 * </p>
 * <p>
 * The service scans both order and common packages for components.
 * </p>
 *
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2024-01-06
 */
@SpringBootApplication(scanBasePackages = { "com.safezone.order", "com.safezone.common" })
@ConfigurationPropertiesScan(basePackageClasses = { ServiceProperties.class, JwtProperties.class,
        ApplicationProperties.class })
public class OrderServiceApplication {

    /**
     * Application entry point.
     * <p>
     * Bootstraps the Spring Boot application and starts the embedded server.
     * </p>
     *
     * @param args command line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
