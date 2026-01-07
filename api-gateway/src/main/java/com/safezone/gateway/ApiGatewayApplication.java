package com.safezone.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the API Gateway.
 * <p>
 * This gateway serves as the single entry point for all client requests.
 * It routes requests to appropriate microservices, handles authentication,
 * and provides cross-cutting concerns like logging and CORS.
 * </p>
 *
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2024-01-06
 */
@SpringBootApplication
public class ApiGatewayApplication {

    /**
     * Application entry point.
     * <p>
     * Bootstraps the Spring Cloud Gateway application.
     * </p>
     *
     * @param args command line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
