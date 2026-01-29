package com.safezone.gateway.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/**
 * CORS configuration for the API Gateway.
 * <p>
 * Configures Cross-Origin Resource Sharing to allow requests from
 * web applications hosted on different domains.
 * </p>
 *
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2024-01-06
 */
@Configuration
public class CorsConfig {

    /**
     * Creates a CORS filter with configurable settings.
     * <p>
     * For security, allowed origins should be explicitly configured in production
     * using the `cors.allowed-origins` property (comma-separated). In development
     * the default is `http://localhost` and `http://127.0.0.1` to support local
     * web clients. Avoid using wildcard origins (`*`) in production.
     * </p>
     *
     * @return the configured CORS web filter
     */
    @Bean
    public CorsWebFilter corsWebFilter(
            @org.springframework.beans.factory.annotation.Value("${cors.allowed-origins:http://localhost,http://127.0.0.1}") String allowedOriginsProp) {
        CorsConfiguration corsConfig = new CorsConfiguration();
        java.util.List<String> allowedOrigins = java.util.Arrays.stream(allowedOriginsProp.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
        // If a single wildcard is provided explicitly, allow origin patterns; otherwise
        // use explicit origins
        if (allowedOrigins.size() == 1 && "*".equals(allowedOrigins.get(0))) {
            corsConfig.setAllowedOriginPatterns(List.of("*"));
        } else {
            corsConfig.setAllowedOrigins(allowedOrigins);
        }
        corsConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        corsConfig.setAllowedHeaders(List.of("*"));
        corsConfig.setExposedHeaders(List.of("Authorization", "Content-Type"));
        corsConfig.setAllowCredentials(true);
        corsConfig.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }
}
