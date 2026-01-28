package com.safezone.user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.safezone.common.config.CommonSecurityConfigurer;
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
     * Creates a BCrypt password encoder bean.
     * <p>
     * Used for securely hashing user passwords during registration
     * and verifying passwords during authentication.
     * 
     * @Bean
     *       public SecurityFilterChain securityFilterChain(HttpSecurity http)
     *       throws Exception {
     *       </ul>
     *       </p>
     *
     * @param http the HttpSecurity builder to configure
     * @return the configured SecurityFilterChain
     * @throws Exception if security configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
            org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource) throws Exception {
        // See CommonSecurityConfigurer for CSRF/CORS policy and S4502 justification
        CommonSecurityConfigurer.applyDefaultSecurity(http, jwtTokenProvider, corsConfigurationSource)
                .authorizeHttpRequests(auth -> auth
                        // Common public endpoints (actuator & swagger) are configured in
                        // CommonSecurityConfigurer
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
