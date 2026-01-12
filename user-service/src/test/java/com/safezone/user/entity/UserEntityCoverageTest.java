package com.safezone.user.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Coverage tests for User entity to reach 100%.
 *
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2026-01-12
 */
@DisplayName("User Entity Coverage Tests")
class UserEntityCoverageTest {

    @Test
    @DisplayName("User builder creates valid instance")
    void userBuilderCreatesValidInstance() {
        LocalDateTime now = LocalDateTime.now();
        Set<UserRole> roles = new HashSet<>();
        roles.add(UserRole.USER);
        roles.add(UserRole.ADMIN);

        User user = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("hashedpassword")
                .firstName("John")
                .lastName("Doe")
                .phone("+1234567890")
                .roles(roles)
                .enabled(true)
                .locked(false)
                .createdAt(now)
                .updatedAt(now)
                .lastLoginAt(now)
                .build();

        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getUsername()).isEqualTo("testuser");
        assertThat(user.getEmail()).isEqualTo("test@example.com");
        assertThat(user.getPassword()).isEqualTo("hashedpassword");
        assertThat(user.getFirstName()).isEqualTo("John");
        assertThat(user.getLastName()).isEqualTo("Doe");
        assertThat(user.getPhone()).isEqualTo("+1234567890");
        assertThat(user.getRoles()).containsExactlyInAnyOrder(UserRole.USER, UserRole.ADMIN);
        assertThat(user.getEnabled()).isTrue();
        assertThat(user.getLocked()).isFalse();
        assertThat(user.getCreatedAt()).isEqualTo(now);
        assertThat(user.getUpdatedAt()).isEqualTo(now);
        assertThat(user.getLastLoginAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("User setters modify fields correctly")
    void userSettersModifyFieldsCorrectly() {
        User user = new User();
        LocalDateTime now = LocalDateTime.now();
        Set<UserRole> roles = new HashSet<>();
        roles.add(UserRole.USER);

        user.setId(2L);
        user.setUsername("newuser");
        user.setEmail("new@example.com");
        user.setPassword("newpassword");
        user.setFirstName("Jane");
        user.setLastName("Smith");
        user.setPhone("+9876543210");
        user.setRoles(roles);
        user.setEnabled(false);
        user.setLocked(true);
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        user.setLastLoginAt(now);

        assertThat(user.getId()).isEqualTo(2L);
        assertThat(user.getUsername()).isEqualTo("newuser");
        assertThat(user.getEmail()).isEqualTo("new@example.com");
        assertThat(user.getPassword()).isEqualTo("newpassword");
        assertThat(user.getFirstName()).isEqualTo("Jane");
        assertThat(user.getLastName()).isEqualTo("Smith");
        assertThat(user.getPhone()).isEqualTo("+9876543210");
        assertThat(user.getRoles()).containsExactly(UserRole.USER);
        assertThat(user.getEnabled()).isFalse();
        assertThat(user.getLocked()).isTrue();
    }

    @Test
    @DisplayName("User AllArgsConstructor creates valid instance")
    void userAllArgsConstructorCreatesValidInstance() {
        LocalDateTime now = LocalDateTime.now();
        Set<UserRole> roles = new HashSet<>();
        roles.add(UserRole.INVENTORY);

        User user = new User(
                3L,
                "constructor",
                "constructor@example.com",
                "password",
                "First",
                "Last",
                "+1111111111",
                roles,
                true,
                false,
                now,
                now,
                now);

        assertThat(user.getId()).isEqualTo(3L);
        assertThat(user.getUsername()).isEqualTo("constructor");
        assertThat(user.getRoles()).containsExactly(UserRole.INVENTORY);
    }

    @Test
    @DisplayName("User NoArgsConstructor creates empty instance")
    void userNoArgsConstructorCreatesEmptyInstance() {
        User user = new User();

        assertThat(user).isNotNull();
        assertThat(user.getId()).isNull();
        assertThat(user.getUsername()).isNull();
    }

    @Test
    @DisplayName("PrePersist sets default values")
    void prePersistSetsDefaultValues() {
        User user = User.builder().build();
        user.onCreate();

        assertThat(user.getCreatedAt()).isNotNull();
        assertThat(user.getUpdatedAt()).isNotNull();
        assertThat(user.getEnabled()).isTrue();
        assertThat(user.getLocked()).isFalse();
        assertThat(user.getRoles()).containsExactly(UserRole.USER);
    }

    @Test
    @DisplayName("PrePersist preserves existing enabled status")
    void prePersistPreservesExistingEnabledStatus() {
        User user = User.builder().enabled(false).build();
        user.onCreate();

        assertThat(user.getEnabled()).isFalse();
    }

    @Test
    @DisplayName("PrePersist preserves existing locked status")
    void prePersistPreservesExistingLockedStatus() {
        User user = User.builder().locked(true).build();
        user.onCreate();

        assertThat(user.getLocked()).isTrue();
    }

    @Test
    @DisplayName("PrePersist preserves existing roles")
    void prePersistPreservesExistingRoles() {
        Set<UserRole> roles = new HashSet<>();
        roles.add(UserRole.ADMIN);
        User user = User.builder().roles(roles).build();
        user.onCreate();

        assertThat(user.getRoles()).containsExactly(UserRole.ADMIN);
    }

    @Test
    @DisplayName("PreUpdate updates timestamp")
    void preUpdateUpdatesTimestamp() {
        User user = User.builder().build();
        user.onCreate();

        // Set updatedAt to a past time to simulate time passing
        LocalDateTime pastTime = LocalDateTime.now().minusSeconds(1);
        user.setUpdatedAt(pastTime);

        user.onUpdate();

        assertThat(user.getUpdatedAt()).isAfter(pastTime);
    }

    @Test
    @DisplayName("addRole adds role to user")
    void addRoleAddsRoleToUser() {
        User user = User.builder().roles(new HashSet<>()).build();

        user.addRole(UserRole.ADMIN);

        assertThat(user.getRoles()).containsExactly(UserRole.ADMIN);
    }

    @Test
    @DisplayName("removeRole removes role from user")
    void removeRoleRemovesRoleFromUser() {
        Set<UserRole> roles = new HashSet<>();
        roles.add(UserRole.USER);
        roles.add(UserRole.ADMIN);
        User user = User.builder().roles(roles).build();

        user.removeRole(UserRole.ADMIN);

        assertThat(user.getRoles()).containsExactly(UserRole.USER);
    }

    @Test
    @DisplayName("getFullName returns full name when both names set")
    void getFullNameReturnsFullNameWhenBothNamesSet() {
        User user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .username("johndoe")
                .build();

        assertThat(user.getFullName()).isEqualTo("John Doe");
    }

    @Test
    @DisplayName("getFullName returns first name only when last name null")
    void getFullNameReturnsFirstNameOnlyWhenLastNameNull() {
        User user = User.builder()
                .firstName("John")
                .username("johndoe")
                .build();

        assertThat(user.getFullName()).isEqualTo("John");
    }

    @Test
    @DisplayName("getFullName returns last name only when first name null")
    void getFullNameReturnsLastNameOnlyWhenFirstNameNull() {
        User user = User.builder()
                .lastName("Doe")
                .username("johndoe")
                .build();

        assertThat(user.getFullName()).isEqualTo("Doe");
    }

    @Test
    @DisplayName("getFullName returns username when both names null")
    void getFullNameReturnsUsernameWhenBothNamesNull() {
        User user = User.builder()
                .username("johndoe")
                .build();

        assertThat(user.getFullName()).isEqualTo("johndoe");
    }

    @Test
    @DisplayName("Builder default creates empty roles set")
    void builderDefaultCreatesEmptyRolesSet() {
        User user = User.builder().build();

        assertThat(user.getRoles()).isNotNull().isEmpty();
    }
}
