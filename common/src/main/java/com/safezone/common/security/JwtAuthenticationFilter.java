package com.safezone.common.security;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * JWT authentication filter for Spring Security.
 * Intercepts HTTP requests to extract and validate JWT tokens from the
 * Authorization header.
 *
 * <p>
 * When a valid token is found, the user's authentication is set in the
 * {@link SecurityContextHolder} for downstream processing.
 * </p>
 *
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2026-01-06
 * @see JwtTokenProvider
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /** The Bearer token prefix in Authorization header. */
    private static final String BEARER_PREFIX = "Bearer ";

    /** The JWT token provider for validation and claim extraction. */
    private final JwtTokenProvider tokenProvider;

    /**
     * Constructs a new JwtAuthenticationFilter with the specified token provider.
     *
     * @param tokenProvider the JWT token provider for token operations
     */
    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    /**
     * Filters incoming requests to extract and validate JWT tokens.
     * Sets the authentication in SecurityContext if token is valid.
     *
     * @param request     the HTTP request
     * @param response    the HTTP response
     * @param filterChain the filter chain for subsequent filters
     * @throws ServletException if a servlet error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        String token = extractTokenFromRequest(request);

        if (StringUtils.hasText(token) && tokenProvider.validateToken(token)) {
            tokenProvider.extractUsername(token).ifPresent(username -> {
                List<String> roles = tokenProvider.extractRoles(token);
                List<SimpleGrantedAuthority> authorities = roles.stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                        .toList();

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username,
                        null, authorities);

                SecurityContextHolder.getContext().setAuthentication(authentication);
            });
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extracts the JWT token from the Authorization header.
     *
     * @param request the HTTP request containing the Authorization header
     * @return the token string without Bearer prefix, or null if not present
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}
