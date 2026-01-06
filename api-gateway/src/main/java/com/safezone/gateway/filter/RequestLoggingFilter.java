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

@Component
public class RequestLoggingFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);

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

    @Override
    public int getOrder() {
        return -1;
    }
}
