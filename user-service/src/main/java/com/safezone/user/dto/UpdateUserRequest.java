package com.safezone.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for updating user profile information.
 * <p>
 * All fields are optional; only provided fields will be updated.
 * </p>
 *
 * @param firstName the new first name (max 50 characters)
 * @param lastName  the new last name (max 50 characters)
 * @param email     the new email address (max 100 characters)
 * @param phone     the new phone number (10-15 digits)
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2024-01-06
 */
public record UpdateUserRequest(
                @Size(max = 50, message = "First name cannot exceed 50 characters") String firstName,

                @Size(max = 50, message = "Last name cannot exceed 50 characters") String lastName,

                @Email(message = "Invalid email format") @Size(max = 100, message = "Email cannot exceed 100 characters") String email,

                @Pattern(regexp = "^\\+?\\d{10,15}$", message = "Invalid phone number format") String phone) {
}
