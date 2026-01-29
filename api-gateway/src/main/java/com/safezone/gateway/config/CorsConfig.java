
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

    private static final List<String> ALLOWED_METHODS = List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS");
    private static final List<String> ALLOWED_HEADERS = List.of("*");
    private static final List<String> EXPOSED_HEADERS = List.of("Authorization", "Content-Type");

    /**
     * Factorized logic for building a CorsConfiguration, used by both the bean and
     * tests.
     */
    public CorsConfiguration buildCorsConfiguration(String allowedOriginsProp, boolean allowWildcard) {
        CorsConfiguration corsConfig = new CorsConfiguration();
        List<String> allowedOrigins = java.util.Arrays.stream(allowedOriginsProp.split(","))
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
        corsConfig.setAllowCredentials(true);
        corsConfig.setMaxAge(3600L);

        return corsConfig;
    }

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
     * `cors.allow-wildcard=true` (opt-in). Without this opt-in l'application
     * will refuse to start to prevent insecure deployments. See
     * `.github/SONAR_S5122_JUSTIFICATION.md` for justification and reviewer
     * guidance.
     * </p>
     *
     * @return the configured CORS web filter
     */
    @Bean
    @SuppressWarnings("null")
    public CorsWebFilter corsWebFilter(
            @org.springframework.beans.factory.annotation.Value("${cors.allowed-origins:http://localhost,http://127.0.0.1}") String allowedOriginsProp,
            @org.springframework.beans.factory.annotation.Value("${cors.allow-wildcard:false}") boolean allowWildcard) {
        CorsConfiguration corsConfig = buildCorsConfiguration(allowedOriginsProp, allowWildcard);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);
        return new CorsWebFilter(source);
    }

}
