package com.safezone.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Route configuration for the API Gateway.
 * <p>
 * Defines routing rules to forward requests to appropriate microservices.
 * Uses Spring Cloud Gateway's load balancing capabilities.
 * </p>
 *
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2024-01-06
 */
@Configuration
public class GatewayConfig {

    /**
     * Configures the route locator with microservice routes.
     * <p>
     * Routes configured:
     * <ul>
     *   <li>/api/v1/products/** → product-service</li>
     *   <li>/api/v1/orders/** → order-service</li>
     *   <li>/api/v1/auth/** → user-service</li>
     *   <li>/api/v1/users/** → user-service</li>
     * </ul>
     * </p>
     *
     * @param builder the route locator builder
     * @return the configured route locator
     */
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("product-service", r -> r
                        .path("/api/v1/products/**")
                        .uri("lb://product-service"))
                .route("order-service", r -> r
                        .path("/api/v1/orders/**")
                        .uri("lb://order-service"))
                .route("user-service-auth", r -> r
                        .path("/api/v1/auth/**")
                        .uri("lb://user-service"))
                .route("user-service", r -> r
                        .path("/api/v1/users/**")
                        .uri("lb://user-service"))
                .build();
    }
}
