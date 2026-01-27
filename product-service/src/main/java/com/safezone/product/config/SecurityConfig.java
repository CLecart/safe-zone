package com.safezone.product.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.safezone.common.security.JwtAuthenticationFilter;
import com.safezone.common.security.JwtTokenProvider;

import jakarta.servlet.http.HttpServletResponse;

/**
 * Security configuration for the Product Service.
 * Configures JWT authentication and endpoint authorization rules.
 *
 * <p>
 * Public access is allowed for:
 * </p>
 * <ul>
 * <li>Actuator endpoints (health checks)</li>
 * <li>Swagger/OpenAPI documentation</li>
 * <li>GET requests on product endpoints</li>
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
         * @param http the HttpSecurity builder
         * @return the configured SecurityFilterChain
         * @throws Exception if configuration fails
         */
        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                // CSRF handling justification (Sonar S4502):
                // This service is a stateless REST API that exclusively uses JWT Bearer tokens
                // (Authorization: Bearer <token>) for authentication. There is no cookie-based
                // or session-based authentication and no login form, therefore CSRF attacks
                // are not applicable for typical API clients. To maintain server-side CSRF
                // protection for any non-API endpoints, CSRF remains enabled by default and
                // we explicitly ignore only API endpoints that accept JWTs. If future changes
                // introduce cookies/sessions, this configuration must be revisited and CSRF
                // protection re-enabled for affected endpoints.
                http
                                .csrf(csrf -> csrf
                                                // CSRF remains enabled but is explicitly ignored for stateless
                                                // REST API endpoints that use JWT authentication (no cookies/sessions).
                                                // See Sonar S4502 and the justification above.
                                                .ignoringRequestMatchers("/api/**"))
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/actuator/**").permitAll()
                                                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                                                // Public GET endpoints
                                                .requestMatchers(HttpMethod.GET, "/api/v1/products/{id}").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/v1/products/category/{category}")
                                                .permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/v1/products/sku/{sku}")
                                                .permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/v1/products/active").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/v1/products/low-stock")
                                                .permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/v1/products/search").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/v1/products/{id}/availability")
                                                .permitAll()
                                                // GET /api/v1/products (listing) is public for pagination/sorting
                                                .requestMatchers(HttpMethod.GET, "/api/v1/products").permitAll()
                                                // All other requests require authentication
                                                .anyRequest().authenticated())
                                .exceptionHandling(ex -> ex
                                                .authenticationEntryPoint((request, response, authException) -> {
                                                        if ("POST".equalsIgnoreCase(request.getMethod())
                                                                        && request.getRequestURI().startsWith(
                                                                                        "/api/v1/products")) {
                                                                response.sendError(HttpServletResponse.SC_FORBIDDEN,
                                                                                "Forbidden");
                                                        } else {
                                                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                                                                                "Unauthorized");
                                                        }
                                                }))
                                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                                                UsernamePasswordAuthenticationFilter.class);
                return http.build();
        }
}
