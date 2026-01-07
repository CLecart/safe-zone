package com.safezone.common.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Unit tests for {@link JwtTokenProvider}.
 * Verifies token generation, validation, and claim extraction functionality.
 *
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2026-01-06
 */
@DisplayName("JWT Token Provider Tests")
class JwtTokenProviderTest {

    /** The JWT token provider under test. */
    private JwtTokenProvider jwtTokenProvider;

    /** Test secret key for JWT signing (Base64 encoded, 256+ bits). */
    private static final String SECRET = "dGVzdC1zZWNyZXQta2V5LWZvci1zYWZlem9uZS1hcHBsaWNhdGlvbi10ZXN0aW5nLW9ubHktdGhpcy1pcy1sb25nLWVub3VnaA==";

    /** Test token expiration time in milliseconds (1 hour). */
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

            assertThat(token)
                    .isNotNull()
                    .isNotEmpty()
                    .satisfies(t -> assertThat(t.split("\\.")).hasSize(3));
        }

        @Test
        @DisplayName("Should generate token with multiple roles")
        void shouldGenerateTokenWithMultipleRoles() {
            String token = jwtTokenProvider.generateToken("admin", List.of("ROLE_USER", "ROLE_ADMIN"));

            assertThat(token).isNotNull();
            List<String> roles = jwtTokenProvider.extractRoles(token);
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

        @ParameterizedTest(name = "Should reject invalid token: {0}")
        @NullAndEmptySource
        @ValueSource(strings = { "invalid.token.here" })
        @DisplayName("Should reject invalid, null or empty token")
        void shouldRejectInvalidNullOrEmptyToken(String invalidToken) {
            boolean isValid = jwtTokenProvider.validateToken(invalidToken);

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

            String username = jwtTokenProvider.extractUsername(token).orElse(null);

            assertThat(username).isEqualTo("testuser");
        }

        @Test
        @DisplayName("Should extract roles from token")
        void shouldExtractRolesFromToken() {
            String token = jwtTokenProvider.generateToken("testuser", List.of("ROLE_USER", "ROLE_ADMIN"));

            List<String> roles = jwtTokenProvider.extractRoles(token);

            assertThat(roles)
                    .hasSize(2)
                    .contains("ROLE_USER", "ROLE_ADMIN");
        }
    }
}
