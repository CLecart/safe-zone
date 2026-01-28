package com.safezone.common.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import com.safezone.common.security.JwtAuthenticationFilter;
import com.safezone.common.security.JwtTokenProvider;

import jakarta.servlet.http.HttpServletResponse;

/**
 * Shared security configuration used across microservices to avoid duplication.
 *
 * SonarQube S4502 justification:
 * CSRF is ignored for `/api/**` endpoints because:
 * - Stateless JWT authentication (Authorization header, no cookies/sessions)
 * - SessionCreationPolicy.STATELESS
 * - No login forms or browser-based authentication
 * - Gateway layer disables credentials (no cross-origin cookies)
 *
 * If cookies/sessions or `setAllowCredentials(true)` are introduced, remove
 * this exception and re-enable CSRF protection immediately.
 */
public final class CommonSecurityConfigurer {

    private CommonSecurityConfigurer() {
    }

    public static HttpSecurity applyDefaultSecurity(HttpSecurity http, JwtTokenProvider jwtTokenProvider,
            CorsConfigurationSource corsConfigurationSource) throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Common public endpoints (actuator & swagger) — extracted to reduce
        // duplication
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll());

        http.exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> response
                        .sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized")));

        http.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                UsernamePasswordAuthenticationFilter.class);

        return http;
    }
}
