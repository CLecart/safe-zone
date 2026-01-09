package com.safezone.user.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.safezone.user.entity.UserRole;

/**
 * Coverage tests for user DTOs (Data Transfer Objects).
 * Tests all user-related record types and their field access.
 *
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2026-01-09
 */
@DisplayName("User DTO Coverage Tests")
class UserDtoCoverageTest {

    @Test
    @DisplayName("UserResponse record constructor and accessors")
    void userResponseConstructorAndAccessors() {
        LocalDateTime now = LocalDateTime.now();
        Set<UserRole> roles = new HashSet<>(java.util.List.of(UserRole.USER));

        UserResponse response = new UserResponse(
                1L,
                "testuser",
                "test@example.com",
                "John",
                "Doe",
                "+1234567890",
                "John Doe",
                roles,
                true,
                now,
                now);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.username()).isEqualTo("testuser");
        assertThat(response.email()).isEqualTo("test@example.com");
        assertThat(response.firstName()).isEqualTo("John");
        assertThat(response.lastName()).isEqualTo("Doe");
        assertThat(response.phone()).isEqualTo("+1234567890");
        assertThat(response.fullName()).isEqualTo("John Doe");
        assertThat(response.roles()).contains(UserRole.USER);
        assertThat(response.enabled()).isTrue();
    }

    @Test
    @DisplayName("RegisterRequest record constructor and accessors")
    void registerRequestConstructorAndAccessors() {
        RegisterRequest request = new RegisterRequest(
                "newuser",
                "new@example.com",
                "Password123",
                "Jane",
                "Smith",
                "+1234567890");

        assertThat(request.username()).isEqualTo("newuser");
        assertThat(request.email()).isEqualTo("new@example.com");
        assertThat(request.password()).isEqualTo("Password123");
        assertThat(request.firstName()).isEqualTo("Jane");
        assertThat(request.lastName()).isEqualTo("Smith");
        assertThat(request.phone()).isEqualTo("+1234567890");
    }

    @Test
    @DisplayName("UpdateUserRequest record constructor and accessors")
    void updateUserRequestConstructorAndAccessors() {
        UpdateUserRequest request = new UpdateUserRequest(
                "Updated",
                "Name",
                "updated@example.com",
                "+9876543210");

        assertThat(request.firstName()).isEqualTo("Updated");
        assertThat(request.lastName()).isEqualTo("Name");
        assertThat(request.email()).isEqualTo("updated@example.com");
        assertThat(request.phone()).isEqualTo("+9876543210");
    }

    @Test
    @DisplayName("LoginRequest record constructor and accessors")
    void loginRequestConstructorAndAccessors() {
        LoginRequest request = new LoginRequest("testuser", "Password123");

        assertThat(request.username()).isEqualTo("testuser");
        assertThat(request.password()).isEqualTo("Password123");
    }

    @Test
    @DisplayName("AuthResponse record and factory method")
    void authResponseRecordAndFactoryMethod() {
        LocalDateTime now = LocalDateTime.now();
        UserResponse user = new UserResponse(
                1L,
                "testuser",
                "test@example.com",
                "John",
                "Doe",
                "+1234567890",
                "John Doe",
                new HashSet<>(java.util.List.of(UserRole.USER)),
                true,
                now,
                now);

        AuthResponse response = AuthResponse.of("token123", 3600L, user);

        assertThat(response.token()).isEqualTo("token123");
        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.expiresIn()).isEqualTo(3600L);
        assertThat(response.user()).isEqualTo(user);
    }
}
