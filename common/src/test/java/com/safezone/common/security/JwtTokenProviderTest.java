package com.safezone.common.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
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

    private static final byte[] SECRET_BYTES = SECRET.getBytes(StandardCharsets.UTF_8);

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

        @Test
        @DisplayName("Should reject token with invalid signature")
        void shouldRejectTokenWithInvalidSignature() {
            JwtTokenProvider otherProvider = new JwtTokenProvider("another-secret-for-tests-should-differ", EXPIRATION);
            String forgedToken = otherProvider.generateToken("intruder", List.of("ROLE_USER"));

            boolean isValid = jwtTokenProvider.validateToken(forgedToken);

            assertThat(isValid).isFalse();
        }

        @Test
        @DisplayName("Should reject expired token")
        void shouldRejectExpiredToken() {
            JwtTokenProvider expiringProvider = new JwtTokenProvider(SECRET, -1000L);
            String expiredToken = expiringProvider.generateToken("olduser", List.of("ROLE_USER"));

            boolean isValid = jwtTokenProvider.validateToken(expiredToken);

            assertThat(isValid).isFalse();
            assertThat(jwtTokenProvider.extractUsername(expiredToken)).isEmpty();
        }

        @Test
        @DisplayName("Should reject unsupported unsigned token")
        void shouldRejectUnsupportedUnsignedToken() {
            String unsignedToken = "eyJhbGciOiJub25lIn0.e30.";

            boolean isValid = jwtTokenProvider.validateToken(unsignedToken);

            assertThat(isValid).isFalse();
            assertThat(jwtTokenProvider.extractUsername(unsignedToken)).isEmpty();
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

        @Test
        @DisplayName("Should return empty for username on malformed token")
        void shouldReturnEmptyUsernameOnMalformedToken() {
            assertThat(jwtTokenProvider.extractUsername("malformed")).isEmpty();
        }

        @Test
        @DisplayName("Should return empty roles on invalid token")
        void shouldReturnEmptyRolesOnInvalidToken() {
            assertThat(jwtTokenProvider.extractRoles("invalid.token.here")).isEmpty();
        }

        @Test
        @DisplayName("Should handle null token gracefully")
        void shouldHandleNullTokenGracefully() {
            assertThat(jwtTokenProvider.extractUsername(null)).isEmpty();
            assertThat(jwtTokenProvider.extractRoles(null)).isEmpty();
        }

        @Test
        @DisplayName("Should return empty roles when claim is not a list")
        void shouldReturnEmptyRolesWhenClaimIsNotList() {
            String token = io.jsonwebtoken.Jwts.builder()
                    .subject("testuser")
                    .claim("roles", "ROLE_USER")
                    .signWith(io.jsonwebtoken.security.Keys.hmacShaKeyFor(SECRET_BYTES))
                    .compact();

            assertThat(jwtTokenProvider.extractRoles(token)).isEmpty();
        }
    }
}
