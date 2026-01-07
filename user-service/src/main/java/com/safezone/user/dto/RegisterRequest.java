package com.safezone.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for user registration.
 * <p>
 * Contains all required and optional fields for creating a new user account.
 * Validates username format, email, password strength, and phone format.
 * </p>
 *
 * @param username  the unique username (3-50 alphanumeric characters or
 *                  underscores)
 * @param email     the unique email address (max 100 characters)
 * @param password  the password (8-100 characters with mixed case and digit)
 * @param firstName the user's first name (max 50 characters)
 * @param lastName  the user's last name (max 50 characters)
 * @param phone     the phone number (10-15 digits, optional +)
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2024-01-06
 */
public record RegisterRequest(
                @NotBlank(message = "Username is required") @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters") @Pattern(regexp = "^\\w+$", message = "Username can only contain letters, numbers, and underscores") String username,

                @NotBlank(message = "Email is required") @Email(message = "Invalid email format") @Size(max = 100, message = "Email cannot exceed 100 characters") String email,

                @NotBlank(message = "Password is required") @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters") @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", message = "Password must contain at least one uppercase letter, one lowercase letter, and one digit") String password,

                @Size(max = 50, message = "First name cannot exceed 50 characters") String firstName,

                @Size(max = 50, message = "Last name cannot exceed 50 characters") String lastName,

                @Pattern(regexp = "^\\+?\\d{10,15}$", message = "Invalid phone number format") String phone) {
}
