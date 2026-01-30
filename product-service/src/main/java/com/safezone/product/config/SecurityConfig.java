package com.safezone.product.config;

import com.safezone.common.config.CommonSecurityConfigurer;
import com.safezone.common.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * Security configuration for the Product Service. Configures JWT authentication and endpoint
 * authorization rules.
 *
 * <p>Public access is allowed for:
 *
 * <ul>
 *   <li>Actuator endpoints (health checks)
 *   <li>Swagger/OpenAPI documentation
 *   <li>GET requests on product endpoints
 * </ul>
 *
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2026-01-06
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    /** JWT token provider for authentication filter. */
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Constructs a SecurityConfig with the required JWT provider.
     *
     * @param jwtTokenProvider the JWT token provider
     */
    public SecurityConfig(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * Configures the security filter chain with JWT authentication.
     *
     * <p>CSRF protection is disabled because this service provides a stateless REST API secured by
     * JWT bearer tokens; public read-only endpoints are limited to GET operations. For the Sonar
     * S4502 justification and reviewer guidance, see `.github/SONAR_S4502_JUSTIFICATION.md`.
     *
     * @param http the HttpSecurity builder
     * @return the configured SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http, CorsConfigurationSource corsConfigurationSource) throws Exception {
        // Centralized default security configuration (CSRF/CORS/S4502 justification)
        CommonSecurityConfigurer.applyDefaultSecurity(
                        http, jwtTokenProvider, corsConfigurationSource)
                .authorizeHttpRequests(
                        auth ->
                                auth
                                        // Public GET endpoints
                                        .requestMatchers(HttpMethod.GET, "/api/v1/products/{id}")
                                        .permitAll()
                                        .requestMatchers(
                                                HttpMethod.GET,
                                                "/api/v1/products/category/{category}")
                                        .permitAll()
                                        .requestMatchers(
                                                HttpMethod.GET, "/api/v1/products/sku/{sku}")
                                        .permitAll()
                                        .requestMatchers(HttpMethod.GET, "/api/v1/products/active")
                                        .permitAll()
                                        .requestMatchers(
                                                HttpMethod.GET, "/api/v1/products/low-stock")
                                        .permitAll()
                                        .requestMatchers(HttpMethod.GET, "/api/v1/products/search")
                                        .permitAll()
                                        .requestMatchers(
                                                HttpMethod.GET,
                                                "/api/v1/products/{id}/availability")
                                        .permitAll()
                                        // GET /api/v1/products (listing) is public for
                                        // pagination/sorting
                                        .requestMatchers(HttpMethod.GET, "/api/v1/products")
                                        .permitAll()
                                        // All other requests require authentication
                                        .anyRequest()
                                        .authenticated())
                .exceptionHandling(
                        ex ->
                                ex.authenticationEntryPoint(
                                        (request, response, authException) -> {
                                            if ("POST".equalsIgnoreCase(request.getMethod())
                                                    && request.getRequestURI()
                                                            .startsWith("/api/v1/products")) {
                                                response.sendError(
                                                        HttpServletResponse.SC_FORBIDDEN,
                                                        "Forbidden");
                                            } else {
                                                response.sendError(
                                                        HttpServletResponse.SC_UNAUTHORIZED,
                                                        "Unauthorized");
                                            }
                                        }));
        return http.build();
    }
}
