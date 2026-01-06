package com.safezone.order.client;

import com.safezone.order.dto.ProductDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Optional;

@Component
public class ProductServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceClient.class);
    private static final Duration TIMEOUT = Duration.ofSeconds(5);

    private final WebClient webClient;

    public ProductServiceClient(
            WebClient.Builder webClientBuilder,
            @Value("${services.product.url:http://localhost:8081}") String productServiceUrl) {
        this.webClient = webClientBuilder.baseUrl(productServiceUrl).build();
    }

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

    private record ProductApiResponse(boolean success, ProductDto data) {}
    private record AvailabilityResponse(boolean success, Boolean data) {}
}
