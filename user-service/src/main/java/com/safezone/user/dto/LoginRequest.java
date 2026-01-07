package com.safezone.user.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Request DTO for user authentication.
 * <p>
 * Contains credentials for logging into the system.
 * </p>
 *
 * @param username the username for authentication
 * @param password the password for authentication
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2024-01-06
 */
public record LoginRequest(
        @NotBlank(message = "Username is required")
        String username,

        @NotBlank(message = "Password is required")
        String password
) {}
