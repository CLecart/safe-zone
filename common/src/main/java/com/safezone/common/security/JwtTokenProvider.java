package com.safezone.common.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

/**
 * JWT token provider for authentication and authorization.
 * Handles token generation, validation, and claim extraction.
 *
 * <p>
 * This component uses HMAC-SHA algorithm for signing tokens
 * and supports configurable expiration times.
 * </p>
 *
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2026-01-06
 */
@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    private final SecretKey secretKey;
    private final long expirationMs;

    /**
     * Constructs a new JwtTokenProvider with the specified configuration.
     *
     * @param secret       the secret key for signing tokens (minimum 256 bits)
     * @param expirationMs token expiration time in milliseconds
     */
    public JwtTokenProvider(
            @Value("${jwt.secret:}") String secret,
            @Value("${jwt.expiration:86400000}") long expirationMs) {
        SecretKey tmpSecretKey = null;
        if (secret == null || secret.isBlank()) {
            // No jwt.secret provided; generate a secure random key to allow tests and
            // local runs to proceed without embedding a secret in the repo. In
            // CI/production
            // a real secret should be provided via environment or configuration.
            logger.warn("'jwt.secret' not provided; generating a secure random key for runtime. "
                    + "Provide a proper 'jwt.secret' in production environments.");
            byte[] generated = new byte[32];
            new java.security.SecureRandom().nextBytes(generated);
            tmpSecretKey = Keys.hmacShaKeyFor(generated);
        } else {
            byte[] keyBytes;
            try {
                keyBytes = java.util.Base64.getDecoder().decode(secret);
            } catch (IllegalArgumentException ex) {
                keyBytes = secret.getBytes(StandardCharsets.UTF_8);
            }
            try {
                tmpSecretKey = Keys.hmacShaKeyFor(keyBytes);
            } catch (io.jsonwebtoken.security.WeakKeyException ex) {
                if (secret.contains("placeholder") || isTestProfileActive()) {
                    logger.warn(
                            "Provided JWT secret is too short for HMAC-SHA algorithms; generating a secure random key for runtime use in test/profile.");
                    byte[] generated = new byte[32]; // 256 bits
                    new java.security.SecureRandom().nextBytes(generated);
                    tmpSecretKey = Keys.hmacShaKeyFor(generated);
                } else {
                    throw new IllegalStateException(
                            "Provided 'jwt.secret' is not secure enough. Use a 256-bit (or larger) secret. "
                                    + ex.getMessage(),
                            ex);
                }
            }
        }
        this.secretKey = tmpSecretKey;
        this.expirationMs = expirationMs;
    }

    /**
     * Generates a JWT token for the specified user.
     *
     * @param username the username to include as the subject
     * @param roles    the list of roles to include in the token claims
     * @return the generated JWT token string
     */
    public String generateToken(String username, List<String> roles) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(username)
                .claim("roles", roles)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    /**
     * Extracts the username (subject) from a JWT token.
     *
     * @param token the JWT token to parse
     * @return an Optional containing the username, or empty if token is invalid
     */
    public Optional<String> extractUsername(String token) {
        return extractClaims(token).map(Claims::getSubject);
    }

    /**
     * Extracts the roles claim from a JWT token.
     *
     * @param token the JWT token to parse
     * @return the list of roles, or an empty list if token is invalid
     */
    public List<String> extractRoles(String token) {
        return extractClaims(token)
                .map(claims -> {
                    Object rolesObj = claims.get("roles");
                    if (rolesObj instanceof List<?> rawList) {
                        return rawList.stream()
                                .filter(String.class::isInstance)
                                .map(String.class::cast)
                                .toList();
                    }
                    return List.<String>of();
                })
                .orElse(List.of());
    }

    /**
     * Validates a JWT token for authenticity and expiration.
     *
     * @param token the JWT token to validate
     * @return true if the token is valid, false otherwise
     */
    public boolean validateToken(String token) {
        return extractClaims(token).isPresent();
    }

    /**
     * Extracts all claims from a JWT token.
     *
     * @param token the JWT token to parse
     * @return an Optional containing the claims, or empty if token is invalid
     */
    private Optional<Claims> extractClaims(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return Optional.of(claims);
        } catch (SignatureException ex) {
            logger.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            logger.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            logger.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty");
        }
        return Optional.empty();
    }

    private static boolean isTestProfileActive() {
        String prop = System.getProperty("spring.profiles.active");
        String env = System.getenv("SPRING_PROFILES_ACTIVE");
        return (prop != null && prop.contains("test")) || (env != null && env.contains("test"));
    }
}
