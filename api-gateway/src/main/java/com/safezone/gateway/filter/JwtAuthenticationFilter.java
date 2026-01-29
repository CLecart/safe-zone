package com.safezone.gateway.filter;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.safezone.common.security.JwtTokenProvider;

import reactor.core.publisher.Mono;

/**
 * Gateway filter for JWT token authentication.
 * <p>
 * Delegates token validation and claim extraction to `JwtTokenProvider` to
 * avoid duplication with other modules. Public endpoints are allowed without
 * authentication. Authenticated requests have user ID and roles added as
 * headers for downstream services.
 * </p>
 *
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2024-01-06
 */
@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    /** Logger for this class. */
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    /** Bearer token prefix. */
    private static final String BEARER_PREFIX = "Bearer ";

    /** JWT helper that centralizes token parsing and validation. */
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Constructs the filter with the shared `JwtTokenProvider` from `common`.
     *
     * @param jwtTokenProvider the shared JWT helper
     */
    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        super(Config.class);
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * Creates the gateway filter that performs JWT authentication.
     * <p>
     * The filter validates the JWT token via `JwtTokenProvider` and adds user
     * headers to the request. Public endpoints bypass authentication.
     * </p>
     *
     * @param config the filter configuration
     * @return the configured gateway filter
     */
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            if (isPublicEndpoint(request.getPath().toString())) {
                return chain.filter(exchange);
            }

            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
                return onError(exchange, "Missing or invalid Authorization header", HttpStatus.UNAUTHORIZED);
            }

            String token = authHeader.substring(BEARER_PREFIX.length());

            if (!jwtTokenProvider.validateToken(token)) {
                return onError(exchange, "Invalid JWT token", HttpStatus.UNAUTHORIZED);
            }

            String username = jwtTokenProvider.extractUsername(token).orElse("");
            List<String> roles = jwtTokenProvider.extractRoles(token);

            ServerHttpRequest modifiedRequest = request.mutate()
                    .header("X-User-Id", username)
                    .header("X-User-Roles", String.join(",", roles))
                    .build();

            return chain.filter(exchange.mutate().request(modifiedRequest).build());
        };
    }

    /**
     * Checks if the given path is a public endpoint that doesn't require
     * authentication.
     *
     * @param path the request path
     * @return true if the endpoint is public
     */
    private boolean isPublicEndpoint(String path) {
        return path.contains("/api/v1/auth/") || path.contains("/actuator/") || path.contains("/swagger-ui")
                || path.contains("/v3/api-docs")
                || (path.contains("/api/v1/products") && !path.contains("/stock") && !path.contains("/low-stock"));
    }

    /**
     * Handles authentication errors by returning an error response.
     *
     * @param exchange the server web exchange
     * @param message  the error message to log
     * @param status   the HTTP status to return
     * @return a Mono completing the error response
     */
    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
        logger.warn("Authentication error: {}", message);
        exchange.getResponse().setStatusCode(status);
        return exchange.getResponse().setComplete();
    }

    /**
     * Configuration class for the JWT authentication filter.
     * <p>
     * Can be extended for configurable options such as excluded paths.
     * </p>
     */
    public static class Config {
        /** Indicates if the filter is enabled. */
        private boolean enabled = true;

        /**
         * Checks if the filter is enabled.
         *
         * @return true if the filter is enabled, false otherwise
         */
        public boolean isEnabled() {
            return enabled;
        }

        /**
         * Sets the enabled state of the filter.
         *
         * @param enabled the enabled state to set
         */
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
}
