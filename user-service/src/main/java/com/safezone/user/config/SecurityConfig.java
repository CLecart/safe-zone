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
     * Creates a BCrypt password encoder bean.
     * <p>
     * Used for securely hashing user passwords during registration
     * and verifying passwords during authentication.
     * 
     * @Bean
     *       public SecurityFilterChain securityFilterChain(HttpSecurity http)
     *       throws Exception {
     *       // Example (do not disable CSRF globally):
     *       // http
     *       // .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))
     *       // .sessionManagement(session ->
     *       // session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
     *       // .authorizeHttpRequests(auth -> auth
     *       // .requestMatchers("/actuator/**").permitAll()
     *       // .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
     *       // .requestMatchers("/api/v1/auth/**").permitAll()
     *       // .anyRequest().authenticated());
     *       // http.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
     *       // UsernamePasswordAuthenticationFilter.class);
     *       // return http.build();
     *       // }
     *       </ul>
     *       </p>
     *
     * @param http the HttpSecurity builder to configure
     * @return the configured SecurityFilterChain
     * @throws Exception if security configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // SonarQube S4502 justification:
        // This service is a stateless REST API that uses JWT Bearer tokens
        // (Authorization: Bearer <token>) and SessionCreationPolicy.STATELESS.
        // There is no cookie- or session-based authentication and no login form,
        // therefore CSRF attacks (which rely on a browser sending authenticated
        // cookies) do not apply to API endpoints. We explicitly ignore CSRF for
        // `/api/**` endpoints to make this intent visible to Sonar and reviewers.
        // Important: Ensure CORS does NOT allow credentials (cookies). If cookies or
        // server-side sessions are introduced in the future, this exception must be
        // removed and CSRF protection re-enabled.
        http
                .csrf(csrf -> csrf
                        // CSRF remains enabled but is explicitly ignored for stateless REST API
                        // endpoints
                        // that use JWT authentication (no cookies/sessions). See Sonar S4502.
                        .ignoringRequestMatchers("/api/**"))
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
