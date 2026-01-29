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
                                // Sonar S4502 justification:
                                // CSRF protection is applied conditionally:
                                // - Only mutating HTTP methods (POST/PUT/PATCH/DELETE) are considered.
                                // - Protect requests that include cookies (possible CSRF).
                                // If cookies, session-based authentication, or allowCredentials=true are
                                // introduced, re-enable CSRF and reassess.
                                .csrf(csrf -> csrf.requireCsrfProtectionMatcher(req -> {
                                        String method = req.getMethod();
                                        boolean mutating = java.util.Set.of("POST", "PUT", "PATCH", "DELETE")
                                                        .contains(method);
                                        if (!mutating) {
                                                return false;
                                        }
                                        // Protect only when cookies are present (session-based or cookie auth).
                                        return req.getCookies() != null && req.getCookies().length > 0;
                                }))
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

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

        // Visible for tests: determine whether CSRF protection should apply to the
        // given request.
        // Rules:
        // - Only mutating HTTP methods (POST/PUT/PATCH/DELETE) are considered
        // - If cookies are present, protect
        static boolean shouldProtectCsrf(jakarta.servlet.http.HttpServletRequest req) {
                String method = req.getMethod();
                boolean mutating = java.util.Set.of("POST", "PUT", "PATCH", "DELETE").contains(method);
                if (!mutating) {
                        return false;
                }
                return req.getCookies() != null && req.getCookies().length > 0;
        }
}
