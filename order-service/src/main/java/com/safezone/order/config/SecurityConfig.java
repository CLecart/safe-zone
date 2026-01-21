package com.safezone.order.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.safezone.common.security.JwtAuthenticationFilter;
import com.safezone.common.security.JwtTokenProvider;

import jakarta.servlet.http.HttpServletResponse;

/**
 * Security configuration for the Order Service.
 * <p>
 * Configures JWT-based authentication with stateless session management.
 * Public endpoints include actuator health checks and Swagger documentation.
 * All other endpoints require authentication.
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
         * <p>
         * Security configuration includes:
         * <ul>
         * <li>CSRF protection disabled for stateless API</li>
         * <li>Stateless session management</li>
         * <li>Public access to actuator and Swagger endpoints</li>
         * <li>JWT authentication filter for protected endpoints</li>
         * </ul>
         * </p>
         *
         * @param http the HttpSecurity builder to configure
         * @return the configured SecurityFilterChain
         * @throws Exception if security configuration fails
         */
        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(AbstractHttpConfigurer::disable)
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/actuator/**").permitAll()
                                                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/v1/orders/{id}",
                                                                "/api/v1/orders/number/{orderNumber}")
                                                .permitAll()
                                                .anyRequest().authenticated());
                http.exceptionHandling(ex -> ex
                                .authenticationEntryPoint((request, response, authException) -> response
                                                .sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized")));
                http.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                                UsernamePasswordAuthenticationFilter.class);
                return http.build();
        }
}
