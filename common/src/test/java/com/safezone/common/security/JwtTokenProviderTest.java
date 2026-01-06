package com.safezone.common.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("JWT Token Provider Tests")
class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    private static final String SECRET = "dGVzdC1zZWNyZXQta2V5LWZvci1zYWZlem9uZS1hcHBsaWNhdGlvbi10ZXN0aW5nLW9ubHktdGhpcy1pcy1sb25nLWVub3VnaA==";
    private static final long EXPIRATION = 3600000L;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider(SECRET, EXPIRATION);
    }

    @Nested
    @DisplayName("Token Generation Tests")
    class TokenGenerationTests {

        @Test
        @DisplayName("Should generate valid token")
        void shouldGenerateValidToken() {
            String token = jwtTokenProvider.generateToken("testuser", List.of("ROLE_USER"));

            assertThat(token).isNotNull();
            assertThat(token).isNotEmpty();
            assertThat(token.split("\\.")).hasSize(3);
        }

        @Test
        @DisplayName("Should generate token with multiple roles")
        void shouldGenerateTokenWithMultipleRoles() {
            String token = jwtTokenProvider.generateToken("admin", List.of("ROLE_USER", "ROLE_ADMIN"));

            assertThat(token).isNotNull();
            List<String> roles = jwtTokenProvider.getRoles(token);
            assertThat(roles).containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN");
        }
    }

    @Nested
    @DisplayName("Token Validation Tests")
    class TokenValidationTests {

        @Test
        @DisplayName("Should validate correct token")
        void shouldValidateCorrectToken() {
            String token = jwtTokenProvider.generateToken("testuser", List.of("ROLE_USER"));

            boolean isValid = jwtTokenProvider.validateToken(token);

            assertThat(isValid).isTrue();
        }

        @Test
        @DisplayName("Should reject invalid token")
        void shouldRejectInvalidToken() {
            boolean isValid = jwtTokenProvider.validateToken("invalid.token.here");

            assertThat(isValid).isFalse();
        }

        @Test
        @DisplayName("Should reject null token")
        void shouldRejectNullToken() {
            boolean isValid = jwtTokenProvider.validateToken(null);

            assertThat(isValid).isFalse();
        }

        @Test
        @DisplayName("Should reject empty token")
        void shouldRejectEmptyToken() {
            boolean isValid = jwtTokenProvider.validateToken("");

            assertThat(isValid).isFalse();
        }
    }

    @Nested
    @DisplayName("Token Extraction Tests")
    class TokenExtractionTests {

        @Test
        @DisplayName("Should extract username from token")
        void shouldExtractUsernameFromToken() {
            String token = jwtTokenProvider.generateToken("testuser", List.of("ROLE_USER"));

            String username = jwtTokenProvider.getUsername(token);

            assertThat(username).isEqualTo("testuser");
        }

        @Test
        @DisplayName("Should extract roles from token")
        void shouldExtractRolesFromToken() {
            String token = jwtTokenProvider.generateToken("testuser", List.of("ROLE_USER", "ROLE_ADMIN"));

            List<String> roles = jwtTokenProvider.getRoles(token);

            assertThat(roles).hasSize(2);
            assertThat(roles).contains("ROLE_USER", "ROLE_ADMIN");
        }
    }
}
