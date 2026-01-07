package com.safezone.user.dto;

import com.safezone.user.entity.UserRole;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Response DTO for user information.
 * <p>
 * Contains user profile data excluding sensitive information like passwords.
 * </p>
 *
 * @param id the unique identifier of the user
 * @param username the username
 * @param email the email address
 * @param firstName the first name
 * @param lastName the last name
 * @param phone the phone number
 * @param fullName the computed full name
 * @param roles the set of assigned roles
 * @param enabled whether the account is enabled
 * @param createdAt the account creation timestamp
 * @param lastLoginAt the last login timestamp
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2024-01-06
 */
public record UserResponse(
        Long id,
        String username,
        String email,
        String firstName,
        String lastName,
        String phone,
        String fullName,
        Set<UserRole> roles,
        Boolean enabled,
        LocalDateTime createdAt,
        LocalDateTime lastLoginAt
) {}
