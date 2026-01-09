package com.safezone.user.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Coverage tests for {@link User} entity.
 * Tests builder patterns, field accessors, and role management.
 *
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2026-01-09
 */
@DisplayName("User Entity Coverage Tests")
class UserCoverageTest {

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .phone("+1234567890")
                .password("encoded_password")
                .roles(new HashSet<>(List.of(UserRole.USER)))
                .enabled(true)
                .locked(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("builder creates user with all fields")
    void builderCreatesUser() {
        assertThat(testUser).isNotNull();
        assertThat(testUser.getUsername()).isEqualTo("testuser");
        assertThat(testUser.getEmail()).isEqualTo("test@example.com");
        assertThat(testUser.getEnabled()).isTrue();
    }

    @Test
    @DisplayName("builder with minimal fields creates user")
    void builderWithMinimalFields() {
        User minimal = User.builder()
                .username("minimal")
                .email("minimal@example.com")
                .password("password")
                .build();

        assertThat(minimal).isNotNull();
        assertThat(minimal.getUsername()).isEqualTo("minimal");
    }

    @Test
    @DisplayName("getters return correct values")
    void gettersReturnCorrectValues() {
        assertThat(testUser.getId()).isEqualTo(1L);
        assertThat(testUser.getUsername()).isEqualTo("testuser");
        assertThat(testUser.getEmail()).isEqualTo("test@example.com");
        assertThat(testUser.getFirstName()).isEqualTo("John");
        assertThat(testUser.getLastName()).isEqualTo("Doe");
        assertThat(testUser.getPassword()).isEqualTo("encoded_password");
        assertThat(testUser.getEnabled()).isTrue();
        assertThat(testUser.getLocked()).isFalse();
    }

    @Test
    @DisplayName("setters modify user fields")
    void settersModifyFields() {
        testUser.setFirstName("Jane");
        testUser.setLastName("Smith");
        testUser.setEmail("jane@example.com");
        testUser.setEnabled(false);
        testUser.setLocked(true);

        assertThat(testUser.getFirstName()).isEqualTo("Jane");
        assertThat(testUser.getLastName()).isEqualTo("Smith");
        assertThat(testUser.getEmail()).isEqualTo("jane@example.com");
        assertThat(testUser.getEnabled()).isFalse();
        assertThat(testUser.getLocked()).isTrue();
    }

    @Test
    @DisplayName("role management with addRole and removeRole")
    void roleManagement() {
        testUser.addRole(UserRole.ADMIN);
        assertThat(testUser.getRoles()).hasSize(2).contains(UserRole.USER, UserRole.ADMIN);

        testUser.removeRole(UserRole.USER);
        assertThat(testUser.getRoles()).hasSize(1).contains(UserRole.ADMIN);
    }

    @Test
    @DisplayName("getFullName returns combined name")
    void getFullNameReturnsFullName() {
        String fullName = testUser.getFullName();

        assertThat(fullName).isEqualTo("John Doe");
    }

    @Test
    @DisplayName("getFullName returns username when names null")
    void getFullNameReturnsUsernameWhenNamesNull() {
        User noNames = User.builder()
                .username("nonames")
                .email("nonames@example.com")
                .password("password")
                .build();

        assertThat(noNames.getFullName()).isEqualTo("nonames");
    }

    @Test
    @DisplayName("getFullName returns firstName only when lastName null")
    void getFullNameReturnsFirstNameOnly() {
        User firstOnly = User.builder()
                .username("firstonly")
                .email("firstonly@example.com")
                .password("password")
                .firstName("OnlyFirst")
                .build();

        assertThat(firstOnly.getFullName()).isEqualTo("OnlyFirst");
    }

    @Test
    @DisplayName("getFullName returns lastName only when firstName null")
    void getFullNameReturnsLastNameOnly() {
        User lastOnly = User.builder()
                .username("lastonly")
                .email("lastonly@example.com")
                .password("password")
                .lastName("OnlyLast")
                .build();

        assertThat(lastOnly.getFullName()).isEqualTo("OnlyLast");
    }

    @Test
    @DisplayName("onCreate sets defaults for timestamps, status and roles")
    void onCreateSetsDefaults() {
        User fresh = User.builder()
                .username("fresh")
                .email("fresh@example.com")
                .password("password")
                .build();

        fresh.onCreate();

        assertThat(fresh.getCreatedAt()).isNotNull();
        assertThat(fresh.getUpdatedAt()).isNotNull();
        assertThat(fresh.getEnabled()).isTrue();
        assertThat(fresh.getLocked()).isFalse();
        assertThat(fresh.getRoles()).contains(UserRole.USER);
    }

    @Test
    @DisplayName("onUpdate refreshes updatedAt timestamp")
    void onUpdateRefreshesUpdatedAt() {
        LocalDateTime before = testUser.getUpdatedAt();
        testUser.onUpdate();
        assertThat(testUser.getUpdatedAt()).isNotNull();
        assertThat(testUser.getUpdatedAt()).isAfterOrEqualTo(before);
    }
}
