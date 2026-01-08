package com.safezone.common.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Unit tests for {@link JwtAuthenticationFilter}.
 *
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2026-01-08
 */
@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        filter = new JwtAuthenticationFilter(tokenProvider);
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should authenticate request with valid token")
    void shouldAuthenticateRequestWithValidToken() throws ServletException, IOException {
        String token = "valid.jwt.token";
        String bearerToken = "Bearer " + token;

        given(request.getHeader(HttpHeaders.AUTHORIZATION)).willReturn(bearerToken);
        given(tokenProvider.validateToken(token)).willReturn(true);
        given(tokenProvider.extractUsername(token)).willReturn(Optional.of("testuser"));
        given(tokenProvider.extractRoles(token)).willReturn(List.of("USER"));

        filter.doFilterInternal(request, response, filterChain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNotNull();
        assertThat(auth.getName()).isEqualTo("testuser");
        assertThat(auth.getAuthorities()).hasSize(1);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Should not authenticate when token is missing")
    void shouldNotAuthenticateWhenTokenMissing() throws ServletException, IOException {
        given(request.getHeader(HttpHeaders.AUTHORIZATION)).willReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Should not authenticate when token is invalid")
    void shouldNotAuthenticateWhenTokenInvalid() throws ServletException, IOException {
        String bearerToken = "Bearer invalid.token";

        given(request.getHeader(HttpHeaders.AUTHORIZATION)).willReturn(bearerToken);
        given(tokenProvider.validateToken(anyString())).willReturn(false);

        filter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Should not authenticate when bearer prefix is missing")
    void shouldNotAuthenticateWhenBearerPrefixMissing() throws ServletException, IOException {
        given(request.getHeader(HttpHeaders.AUTHORIZATION)).willReturn("InvalidPrefix token");

        filter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Should handle multiple roles")
    void shouldHandleMultipleRoles() throws ServletException, IOException {
        String token = "valid.jwt.token";
        String bearerToken = "Bearer " + token;

        given(request.getHeader(HttpHeaders.AUTHORIZATION)).willReturn(bearerToken);
        given(tokenProvider.validateToken(token)).willReturn(true);
        given(tokenProvider.extractUsername(token)).willReturn(Optional.of("admin"));
        given(tokenProvider.extractRoles(token)).willReturn(List.of("USER", "ADMIN"));

        filter.doFilterInternal(request, response, filterChain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNotNull();
        assertThat(auth.getAuthorities()).hasSize(2);
    }
}
