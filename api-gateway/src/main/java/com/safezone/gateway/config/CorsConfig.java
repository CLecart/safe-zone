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
     * Creates a CORS filter with permissive settings.
     * <p>
     * Configuration allows:
     * <ul>
     * <li>All origin patterns</li>
     * <li>Common HTTP methods (GET, POST, PUT, PATCH, DELETE, OPTIONS)</li>
     * <li>All headers</li>
     * <li>Credentials (cookies, authorization headers)</li>
     * <li>1 hour preflight cache</li>
     * </ul>
     * </p>
     *
     * @return the configured CORS web filter
     */
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOriginPatterns(List.of("*"));
        corsConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        corsConfig.setAllowedHeaders(List.of("*"));
        corsConfig.setExposedHeaders(List.of("Authorization", "Content-Type"));
        // Do NOT allow credentials (cookies) for the gateway: this service uses
        // stateless
        // JWT bearer authentication (Authorization header). Allowing credentials makes
        // CSRF protections necessary and would contradict the S4502 rationale below.
        // Keep CORS permissive for origins/headers/methods, but do not permit cookies.
        corsConfig.setAllowCredentials(false);
        corsConfig.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }
}
