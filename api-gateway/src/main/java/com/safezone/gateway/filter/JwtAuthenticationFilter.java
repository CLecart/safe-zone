package com.safezone.gateway.filter;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import reactor.core.publisher.Mono;

/**
 * Gateway filter for JWT token authentication.
 * <p>
 * Validates JWT tokens in incoming requests and extracts user information.
 * Public endpoints are allowed without authentication. Authenticated requests
 * have user ID and roles added as headers for downstream services.
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

    /** Secret key for JWT signature verification. */
    private final SecretKey secretKey;

    /**
     * Constructs the filter with the JWT secret key.
     *
     * @param secret the secret key for JWT signature verification
     */
    public JwtAuthenticationFilter(
            @Value("${jwt.secret:SafeZoneSecretKeyForJWTAuthenticationMustBeAtLeast256BitsLong}") String secret) {
        super(Config.class);
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Creates the gateway filter that performs JWT authentication.
     * <p>
     * The filter validates the JWT token and adds user headers to the request.
     * Public endpoints bypass authentication.
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
            Optional<Claims> claimsOpt = validateToken(token);

            if (claimsOpt.isEmpty()) {
                return onError(exchange, "Invalid JWT token", HttpStatus.UNAUTHORIZED);
            }

            Claims claims = claimsOpt.get();
            ServerHttpRequest modifiedRequest = request.mutate()
                    .header("X-User-Id", claims.getSubject())
                    .header("X-User-Roles", String.join(",", getRoles(claims)))
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
        return path.contains("/api/v1/auth/") ||
                path.contains("/actuator/") ||
                path.contains("/swagger-ui") ||
                path.contains("/v3/api-docs") ||
                (path.contains("/api/v1/products") && !path.contains("/stock") && !path.contains("/low-stock"));
    }

    /**
     * Validates a JWT token and extracts its claims.
     *
     * @param token the JWT token to validate
     * @return an Optional containing the claims if valid, empty otherwise
     */
    private Optional<Claims> validateToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return Optional.of(claims);
        } catch (SignatureException ex) {
            logger.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            logger.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            logger.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty");
        }
        return Optional.empty();
    }

    /**
     * Extracts user roles from JWT claims.
     *
     * @param claims the JWT claims
     * @return the list of role names, empty list if none
     */
    @SuppressWarnings("unchecked")
    private List<String> getRoles(Claims claims) {
        Object roles = claims.get("roles");
        if (roles instanceof List) {
            return (List<String>) roles;
        }
        return List.of();
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
