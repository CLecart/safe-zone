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
     * web clients.
     * </p>
     * <p>
     * Note: the wildcard origin `*` is only permitted when the operator sets
     * `cors.allow-wildcard=true` (opt-in). Without this opt-in the application
     * will refuse to start to prevent insecure deployments. See
     * `.github/SONAR_S5122_JUSTIFICATION.md` for justification and reviewer
     * guidance.
     * </p>
     *
     * @return the configured CORS web filter
     */
    @Bean
    public CorsWebFilter corsWebFilter(
            @org.springframework.beans.factory.annotation.Value("${cors.allowed-origins:http://localhost,http://127.0.0.1}") String allowedOriginsProp,
            @org.springframework.beans.factory.annotation.Value("${cors.allow-wildcard:false}") boolean allowWildcard) {
        CorsConfiguration corsConfig = new CorsConfiguration();
        java.util.List<String> allowedOrigins = java.util.Arrays.stream(allowedOriginsProp.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        if (allowedOrigins.size() == 1 && "*".equals(allowedOrigins.get(0))) {
            if (!allowWildcard) {
                throw new IllegalStateException(
                        "CORS wildcard origin '*' is not allowed unless 'cors.allow-wildcard=true' is set. Aborting startup.");
            }
            org.slf4j.LoggerFactory.getLogger(CorsConfig.class)
                    .warn("CORS configured with wildcard origin '*' and 'cors.allow-wildcard=true' - ensure this is intentional and acceptable for your environment (see Sonar S5122)");
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

    /**
     * Helper method for tests: builds the {@link CorsConfiguration} that would be
     * registered for the given comma-separated allowed origins property.
     *
     * This method exists to make it straightforward to assert that wildcard
     * origins are only used when explicitly configured and to document the
     * Sonar S5122 justification (only trusted origins should be allowed).
     */
    CorsConfiguration buildCorsConfiguration(String allowedOriginsProp, boolean allowWildcard) {
        CorsConfiguration corsConfig = new CorsConfiguration();
        java.util.List<String> allowedOrigins = java.util.Arrays.stream(allowedOriginsProp.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
        if (allowedOrigins.size() == 1 && "*".equals(allowedOrigins.get(0))) {
            if (!allowWildcard) {
                throw new IllegalStateException(
                        "CORS wildcard origin '*' is not allowed unless 'cors.allow-wildcard=true' is set.");
            }
            corsConfig.setAllowedOriginPatterns(List.of("*"));
        } else {
            corsConfig.setAllowedOrigins(allowedOrigins);
        }
        corsConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        corsConfig.setAllowedHeaders(List.of("*"));
        corsConfig.setExposedHeaders(List.of("Authorization", "Content-Type"));
        corsConfig.setAllowCredentials(true);
        corsConfig.setMaxAge(3600L);
        return corsConfig;
    }
}
