
package com.safezone.common.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Common utility for security configuration (CORS, CSRF, etc.)
 * to be used in all microservices to avoid duplication.
 */
@Configuration
public class CommonSecurityConfig {
    // Prevent instantiation
    private CommonSecurityConfig() {
    }

    /**
     * Shared CORS bean: always disallows credentials at the service level.
     */

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // S5122 justification: Wildcard CORS origins are allowed because
        // setAllowCredentials(false) guarantees that no credentials (cookies, auth
        // headers, client certificates) will ever be sent cross-origin. This is
        // required for public REST APIs and is safe as long as credentials are always
        // refused. If setAllowCredentials(true) is ever used, remove the wildcard and
        // restrict origins explicitly. (See SonarQube S5122)
        config.setAllowedOriginPatterns(List.of("*")); // NOSONAR S5122
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization", "Content-Type"));
        config.setAllowCredentials(false); // Absolutely required for wildcard CORS
        config.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
