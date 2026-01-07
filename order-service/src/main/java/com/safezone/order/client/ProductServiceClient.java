package com.safezone.order.client;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.safezone.order.dto.ProductDto;

import reactor.core.publisher.Mono;

/**
 * HTTP client for communicating with the Product Service.
 * <p>
 * Provides methods to retrieve product information, check availability,
 * and update stock levels using reactive WebClient.
 * </p>
 *
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2024-01-06
 */
@Component
public class ProductServiceClient {

    /** Logger for this class. */
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceClient.class);

    /** Default timeout for HTTP requests. */
    private static final Duration TIMEOUT = Duration.ofSeconds(5);

    /** WebClient instance for making HTTP requests. */
    private final WebClient webClient;

    /**
     * Constructs the Product Service client with configured base URL.
     *
     * @param webClientBuilder  the WebClient builder for creating HTTP client
     * @param productServiceUrl the base URL of the Product Service
     */
    public ProductServiceClient(
            WebClient.Builder webClientBuilder,
            @Value("${services.product.url:http://localhost:8081}") String productServiceUrl) {
        this.webClient = webClientBuilder.baseUrl(Objects.requireNonNull(productServiceUrl)).build();
    }

    /**
     * Retrieves a product by its unique identifier.
     *
     * @param productId the product ID to retrieve
     * @return an Optional containing the product if found, empty otherwise
     */
    public Optional<ProductDto> getProductById(Long productId) {
        try {
            return webClient.get()
                    .uri("/api/v1/products/{id}", productId)
                    .retrieve()
                    .bodyToMono(ProductApiResponse.class)
                    .timeout(TIMEOUT)
                    .map(response -> response.data)
                    .blockOptional();
        } catch (Exception e) {
            logger.error("Error fetching product with ID: {}", productId, e);
            return Optional.empty();
        }
    }

    /**
     * Checks if a product has sufficient stock for the requested quantity.
     *
     * @param productId the product ID to check
     * @param quantity  the requested quantity
     * @return true if sufficient stock is available, false otherwise
     */
    public boolean checkProductAvailability(Long productId, Integer quantity) {
        try {
            Boolean available = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/v1/products/{id}/availability")
                            .queryParam("quantity", quantity)
                            .build(productId))
                    .retrieve()
                    .bodyToMono(AvailabilityResponse.class)
                    .timeout(TIMEOUT)
                    .map(response -> response.data)
                    .block();
            return Boolean.TRUE.equals(available);
        } catch (Exception e) {
            logger.error("Error checking availability for product: {}", productId, e);
            return false;
        }
    }

    /**
     * Updates the stock level for a product asynchronously.
     *
     * @param productId the product ID to update
     * @param quantity  the quantity change (negative to decrease)
     * @return a Mono completing when the update finishes
     */
    public Mono<Void> updateStock(Long productId, Integer quantity) {
        return webClient.patch()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/products/{id}/stock")
                        .queryParam("quantity", quantity)
                        .build(productId))
                .retrieve()
                .bodyToMono(Void.class)
                .timeout(TIMEOUT)
                .doOnError(e -> logger.error("Error updating stock for product: {}", productId, e));
    }

    /**
     * Internal record for deserializing product API responses.
     *
     * @param success whether the API call succeeded
     * @param data    the product data if successful
     */
    private record ProductApiResponse(boolean success, ProductDto data) {
    }

    /**
     * Internal record for deserializing availability check responses.
     *
     * @param success whether the API call succeeded
     * @param data    the availability status if successful
     */
    private record AvailabilityResponse(boolean success, Boolean data) {
    }
}
