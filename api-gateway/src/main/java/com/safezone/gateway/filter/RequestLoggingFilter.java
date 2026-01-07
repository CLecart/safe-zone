package com.safezone.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;

/**
 * Global gateway filter for logging all incoming requests.
 * <p>
 * Logs request details including method, path, and source address.
 * Also tracks and logs response time for performance monitoring.
 * </p>
 *
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2024-01-06
 */
@Component
public class RequestLoggingFilter implements GlobalFilter, Ordered {

    /** Logger for this class. */
    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);

    /**
     * Filters the request and logs its details.
     * <p>
     * Generates a unique request ID for tracing and measures execution time.
     * </p>
     *
     * @param exchange the server web exchange
     * @param chain the gateway filter chain
     * @return a Mono completing when the request is processed
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String requestId = java.util.UUID.randomUUID().toString().substring(0, 8);
        Instant startTime = Instant.now();

        logger.info("[{}] Incoming request: {} {} from {}",
                requestId,
                request.getMethod(),
                request.getPath(),
                request.getRemoteAddress());

        return chain.filter(exchange)
                .doFinally(signalType -> {
                    Duration duration = Duration.between(startTime, Instant.now());
                    logger.info("[{}] Request completed: {} {} - Status: {} - Duration: {}ms",
                            requestId,
                            request.getMethod(),
                            request.getPath(),
                            exchange.getResponse().getStatusCode(),
                            duration.toMillis());
                });
    }

    /**
     * Returns the filter order.
     * <p>
     * Returns -1 to ensure this filter runs early in the filter chain.
     * </p>
     *
     * @return the filter order
     */
    @Override
    public int getOrder() {
        return -1;
    }
}
