package com.safezone.user.dto;

public record AuthResponse(
        String token,
        String tokenType,
        Long expiresIn,
        UserResponse user
) {
    public static AuthResponse of(String token, Long expiresIn, UserResponse user) {
        return new AuthResponse(token, "Bearer", expiresIn, user);
    }
}
