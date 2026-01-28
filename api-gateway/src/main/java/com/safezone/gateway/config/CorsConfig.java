package com.safezone.gateway.config;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
    private static final Logger logger = LoggerFactory.getLogger(CorsConfig.class);

    @Bean
    public CorsWebFilter corsWebFilter(@Value("${cors.allowed-origins:}") String allowedOrigins) {
        CorsConfiguration corsConfig = new CorsConfiguration();

        // Determine allowed origins from configuration. Default to localhost for dev.
        List<String> origins;
        if (allowedOrigins == null || allowedOrigins.isBlank()) {
            origins = List.of("http://localhost:3000", "http://127.0.0.1:3000");
            logger.warn(
                    "'cors.allowed-origins' not set; defaulting to localhost origins for development. Set a strict list in production.");
        } else if ("*".equals(allowedOrigins.trim())) {
            // Sonar S5122 justification:
            // This gateway allows all origins only when the deployment explicitly
            // sets '*' for `cors.allowed-origins`. This is acceptable here because
            // credentials (cookies) are explicitly disabled (see
            // setAllowCredentials(false))
            // and the platform uses stateless JWT via Authorization header. In
            // production, prefer specifying concrete origins via 'cors.allowed-origins'.
            origins = List.of("*");
        } else {
            // Avoid using complex regexes in split to mitigate ReDoS vulnerabilities (Sonar
            // S5852).
            // Use a simple split on ',' and trim each token instead.
            origins = java.util.Arrays.stream(allowedOrigins.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList();
        }

        if (origins.size() == 1 && "*".equals(origins.get(0))) {
            corsConfig.setAllowedOriginPatterns(origins);
        } else {
            corsConfig.setAllowedOrigins(origins);
        }

        corsConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        corsConfig.setAllowedHeaders(List.of("*"));
        corsConfig.setExposedHeaders(List.of("Authorization", "Content-Type"));
        // Do NOT allow credentials (cookies) for the gateway: this service uses
        // stateless JWT bearer authentication (Authorization header). Allowing
        // credentials
        // makes CSRF protections necessary and would contradict the S4502 rationale.
        corsConfig.setAllowCredentials(false);
        corsConfig.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }
}
