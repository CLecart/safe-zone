package com.safezone.user.dto;

import com.safezone.user.entity.UserRole;

import java.time.LocalDateTime;
import java.util.Set;

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
