package com.safezone.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * JWT token provider for authentication and authorization.
 * Handles token generation, validation, and claim extraction.
 *
 * <p>This component uses HMAC-SHA algorithm for signing tokens
 * and supports configurable expiration times.</p>
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
            @Value("${jwt.secret:defaultSecretKeyThatShouldBeChangedInProduction123456}") String secret,
            @Value("${jwt.expiration:86400000}") long expirationMs) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
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
    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        return extractClaims(token)
                .map(claims -> claims.get("roles", List.class))
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
}
