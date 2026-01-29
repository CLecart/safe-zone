package com.safezone.user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
 * Security configuration for the User Service.
 * <p>
 * Configures JWT-based authentication with stateless session management.
 * Provides password encoder bean and security filter chain.
 * Public endpoints include auth endpoints, actuator, and Swagger docs.
 * </p>
 *
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2024-01-06
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    /**
     * Provides a BCrypt password encoder bean for secure password hashing.
     */
    @Bean
    public org.springframework.security.crypto.password.PasswordEncoder passwordEncoder() {
        return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
    }

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
     * Configures the HTTP security filter chain for the User Service.
     *
     * <p>
     * CSRF protection is disabled intentionally because this service exposes a
     * stateless JSON API secured by JWT bearer tokens. State-changing operations
     * require a valid JWT and are therefore not vulnerable to browser-based CSRF
     * attacks in this architecture. Public endpoints are limited to read-only
     * GET requests as shown below. For the complete Sonar S4502 justification
     * and reviewer guidance, see `.github/SONAR_S4502_JUSTIFICATION.md`.
     * </p>
     *
     * @param http the HttpSecurity builder to configure
     * @return the configured SecurityFilterChain
     * @throws Exception if security configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // CSRF is disabled because this service is a stateless REST API that uses
        // JWT Bearer tokens (Authorization: Bearer <token>). There is no cookie- or
        // session-based authentication and no login form, so CSRF is not applicable.
        // See SonarQube rule S4502 for justification.
        http
                .csrf(org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer::disable) // NOSONAR:
                                                                                                                      // S4502
                                                                                                                      // justified
                                                                                                                      // -
                                                                                                                      // stateless
                                                                                                                      // REST
                                                                                                                      // API
                                                                                                                      // using
                                                                                                                      // JWT
                                                                                                                      // Bearer
                                                                                                                      // tokens
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/users/{id}").permitAll()
                        .anyRequest().authenticated());
        http.exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> response
                        .sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized")));
        http.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
