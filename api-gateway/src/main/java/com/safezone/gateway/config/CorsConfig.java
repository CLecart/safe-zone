package com.safezone.gateway.config;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/**
 * CORS configuration for the API Gateway.
 *
 * <p>Configures Cross-Origin Resource Sharing to allow requests from web applications hosted on
 * different domains.
 *
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2024-01-06
 */
@Configuration
public class CorsConfig {

    private static final List<String> ALLOWED_METHODS =
            List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS");
    private static final List<String> ALLOWED_HEADERS = List.of("*");
    private static final List<String> EXPOSED_HEADERS = List.of("Authorization", "Content-Type");

    /** Factorized logic for building a CorsConfiguration, used by both the bean and tests. */
    public @NonNull CorsConfiguration buildCorsConfiguration(
            String allowedOriginsProp, boolean allowWildcard) {
        CorsConfiguration corsConfig = new CorsConfiguration();
        List<String> allowedOrigins =
                java.util.Arrays.stream(allowedOriginsProp.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .toList();

        if (allowedOrigins.size() == 1 && "*".equals(allowedOrigins.get(0))) {
            if (!allowWildcard) {
                throw new IllegalStateException(
                        "CORS wildcard origin '*' is not allowed unless 'cors.allow-wildcard=true' is set. Aborting startup.");
            }
            // SonarQube S5122: Usage of wildcard is only possible via explicit opt-in (see
            // 'cors.allow-wildcard')
            List<String> safeWildcard = List.of("*");
            corsConfig.setAllowedOriginPatterns(safeWildcard);
        } else {
            corsConfig.setAllowedOrigins(allowedOrigins);
        }
        corsConfig.setAllowedMethods(ALLOWED_METHODS);
        corsConfig.setAllowedHeaders(ALLOWED_HEADERS);
        corsConfig.setExposedHeaders(EXPOSED_HEADERS);
        // For security, the gateway must not allow credentials (cookies) by default.
        // Tests and the security policy expect credentials to be disabled.
        corsConfig.setAllowCredentials(false);
        corsConfig.setMaxAge(3600L);

        return corsConfig;
    }

    /**
     * Creates a CORS filter with configurable settings.
     *
     * <p>The bean reads `cors.allowed-origins` and `cors.allow-wildcard` to determine the effective
     * configuration. When `cors.allowed-origins` is empty, the bean defaults to localhost origins
     * for development convenience.
     *
     * @return the configured CORS web filter
     */
    private static final Logger logger = LoggerFactory.getLogger(CorsConfig.class);

    @Bean
    public @NonNull CorsWebFilter corsWebFilter(
            @Value("${cors.allowed-origins:}") String allowedOriginsProp,
            @Value("${cors.allow-wildcard:false}") boolean allowWildcard) {
        if (allowedOriginsProp == null || allowedOriginsProp.isBlank()) {
            allowedOriginsProp = "http://localhost,http://127.0.0.1";
            logger.warn(
                    "'cors.allowed-origins' not set; defaulting to localhost origins for development. Set a strict list in production.");
        }
        CorsConfiguration corsConfig = buildCorsConfiguration(allowedOriginsProp, allowWildcard);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);
        return new CorsWebFilter(source);
    }
}
