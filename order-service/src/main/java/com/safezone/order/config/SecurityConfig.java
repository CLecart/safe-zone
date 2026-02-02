package com.safezone.order.config;

import com.safezone.common.config.CommonSecurityConfigurer;
import com.safezone.common.security.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * Security configuration for the Order Service.
 *
 * <p>Configures JWT-based authentication with stateless session management. Public endpoints
 * include actuator health checks and Swagger documentation. All other endpoints require
 * authentication.
 *
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2024-01-06
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    /** JWT token provider for authentication processing. */
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Constructs the security configuration with required dependencies.
     *
     * @param jwtTokenProvider the JWT token provider for token validation
     */
    public SecurityConfig(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * Configures the security filter chain for HTTP requests.
     *
     * <p>Security configuration includes:
     *
     * <ul>
     *   <li>CSRF protection disabled for stateless API
     *   <li>Stateless session management
     *   <li>Public access to actuator and Swagger endpoints
     *   <li>JWT authentication filter for protected endpoints
     * </ul>
     *
     * <p>Rationale: CSRF is disabled because the API is stateless and protected by JWT bearer
     * tokens; state-changing endpoints require authentication. See
     * `.github/SONAR_S4502_JUSTIFICATION.md` for the Sonar S4502 justification and reviewer
     * checklist.
     *
     * @param http the HttpSecurity builder to configure
     * @return the configured SecurityFilterChain
     * @throws Exception if security configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http, CorsConfigurationSource corsConfigurationSource) throws Exception {
        CommonSecurityConfigurer.applyDefaultSecurity(
                        http, jwtTokenProvider, corsConfigurationSource)
                .authorizeHttpRequests(
                        auth ->
                                auth.requestMatchers(
                                                HttpMethod.GET,
                                                "/api/v1/orders/{id}",
                                                "/api/v1/orders/number/{orderNumber}")
                                        .permitAll()
                                        .anyRequest()
                                        .authenticated());
        return http.build();
    }
}
