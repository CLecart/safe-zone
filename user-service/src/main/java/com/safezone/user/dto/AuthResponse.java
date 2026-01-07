package com.safezone.user.dto;

/**
 * Response DTO for authentication operations.
 * <p>
 * Contains the JWT token, token metadata, and authenticated user information.
 * </p>
 *
 * @param token the JWT access token
 * @param tokenType the token type (always "Bearer")
 * @param expiresIn the token expiration time in seconds
 * @param user the authenticated user's information
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2024-01-06
 */
public record AuthResponse(
        String token,
        String tokenType,
        Long expiresIn,
        UserResponse user
) {
    /**
     * Factory method to create an AuthResponse with Bearer token type.
     *
     * @param token the JWT access token
     * @param expiresIn the token expiration time in seconds
     * @param user the authenticated user's information
     * @return a new AuthResponse instance
     */
    public static AuthResponse of(String token, Long expiresIn, UserResponse user) {
        return new AuthResponse(token, "Bearer", expiresIn, user);
    }
}
