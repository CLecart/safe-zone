package com.safezone.user.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Comprehensive test suite for {@link User} entity builder pattern.
 * 
 * <p>
 * <strong>Purpose:</strong>
 * Tests the Lombok-generated builder for User entity to achieve 100% code
 * coverage.
 * Builder pattern allows flexible object construction with optional fields.
 * 
 * <p>
 * <strong>Coverage Strategy:</strong>
 * <ul>
 * <li>Test builder with all fields populated</li>
 * <li>Test builder with minimal fields (ID, username, email, password)</li>
 * <li>Test builder with partial fields (combinations)</li>
 * <li>Test default values (@Builder.Default for roles)</li>
 * <li>Test lifecycle callbacks (@PrePersist, @PreUpdate)</li>
 * <li>Test business methods (addRole, removeRole, getFullName)</li>
 * </ul>
 * 
 * <p>
 * <strong>Target:</strong> Achieve 100% instruction and branch coverage for
 * User.UserBuilder.
 * 
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2026-01-12
 * @see User
 * @see User.UserBuilder
 */
@DisplayName("User Builder Pattern Tests")
class UserBuilderTest {

    /**
     * Tests User builder with all fields populated.
     * 
     * <p>
     * <strong>Scenario:</strong> Construct User entity using builder pattern with
     * all fields.
     * 
     * <p>
     * <strong>Given:</strong> All User fields have values.
     * 
     * <p>
     * <strong>When:</strong> Builder builds User instance.
     * 
     * <p>
     * <strong>Then:</strong> User instance contains all specified field values.
     * 
     * <p>
     * <strong>Coverage:</strong> Tests all builder setter methods and build()
     * method.
     */
    @Test
    @DisplayName("Should build user with all fields")
    void shouldBuildUserWithAllFields() {
        // Arrange: Prepare all field values
        LocalDateTime now = LocalDateTime.now();
        Set<UserRole> roles = new HashSet<>();
        roles.add(UserRole.USER);
        roles.add(UserRole.ADMIN);

        // Act: Build User with all fields
        User user = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .firstName("Test")
                .lastName("User")
                .phone("+33612345678")
                .roles(roles)
                .enabled(true)
                .locked(false)
                .createdAt(now)
                .updatedAt(now)
                .lastLoginAt(now)
                .build();

        // Assert: Verify all fields are set correctly
        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getUsername()).isEqualTo("testuser");
        assertThat(user.getEmail()).isEqualTo("test@example.com");
        assertThat(user.getPassword()).isEqualTo("encodedPassword");
        assertThat(user.getFirstName()).isEqualTo("Test");
        assertThat(user.getLastName()).isEqualTo("User");
        assertThat(user.getPhone()).isEqualTo("+33612345678");
        assertThat(user.getRoles()).containsExactlyInAnyOrder(UserRole.USER, UserRole.ADMIN);
        assertThat(user.getEnabled()).isTrue();
        assertThat(user.getLocked()).isFalse();
        assertThat(user.getCreatedAt()).isEqualTo(now);
        assertThat(user.getUpdatedAt()).isEqualTo(now);
        assertThat(user.getLastLoginAt()).isEqualTo(now);
    }

    /**
     * Tests User builder with minimal required fields only.
     * 
     * <p>
     * <strong>Scenario:</strong> Construct User with only mandatory fields.
     * 
     * <p>
     * <strong>Given:</strong> Only ID, username, email, password are provided.
     * 
     * <p>
     * <strong>When:</strong> Builder builds User instance.
     * 
     * <p>
     * <strong>Then:</strong> User instance has required fields set, optional fields
     * are null.
     * 
     * <p>
     * <strong>Coverage:</strong> Tests builder with partial field population.
     */
    @Test
    @DisplayName("Should build user with minimal fields")
    void shouldBuildUserWithMinimalFields() {
        // Act: Build User with minimal fields
        User user = User.builder()
                .id(2L)
                .username("minimal")
                .email("minimal@example.com")
                .password("hashedpass")
                .build();

        // Assert: Verify required fields and null optionals
        assertThat(user.getId()).isEqualTo(2L);
        assertThat(user.getUsername()).isEqualTo("minimal");
        assertThat(user.getEmail()).isEqualTo("minimal@example.com");
        assertThat(user.getPassword()).isEqualTo("hashedpass");
        assertThat(user.getFirstName()).isNull();
        assertThat(user.getLastName()).isNull();
        assertThat(user.getPhone()).isNull();
        assertThat(user.getEnabled()).isNull();
        assertThat(user.getLocked()).isNull();
    }

    /**
     * Tests User builder with roles collection.
     * 
     * <p>
     * <strong>Scenario:</strong> Builder with explicitly set roles.
     * 
     * <p>
     * <strong>Given:</strong> Roles set contains ADMIN and SUPPORT.
     * 
     * <p>
     * <strong>When:</strong> Builder builds User.
     * 
     * <p>
     * <strong>Then:</strong> User has specified roles.
     * 
     * <p>
     * <strong>Coverage:</strong> Tests roles collection builder method.
     */
    @Test
    @DisplayName("Should build user with custom roles")
    void shouldBuildUserWithCustomRoles() {
        // Arrange: Create custom role set
        Set<UserRole> customRoles = new HashSet<>();
        customRoles.add(UserRole.ADMIN);
        customRoles.add(UserRole.SUPPORT);

        // Act: Build User with custom roles
        User user = User.builder()
                .username("admin")
                .email("admin@example.com")
                .password("pass")
                .roles(customRoles)
                .build();

        // Assert: Verify custom roles are assigned
        assertThat(user.getRoles()).containsExactlyInAnyOrder(UserRole.ADMIN, UserRole.SUPPORT);
    }

    /**
     * Tests User builder with enabled/locked flags.
     * 
     * <p>
     * <strong>Scenario:</strong> Builder with account status flags.
     * 
     * <p>
     * <strong>Given:</strong> Enabled=false, Locked=true.
     * 
     * <p>
     * <strong>When:</strong> Builder builds User.
     * 
     * <p>
     * <strong>Then:</strong> User has specified status flags.
     * 
     * <p>
     * <strong>Coverage:</strong> Tests boolean flag builder methods.
     */
    @Test
    @DisplayName("Should build user with disabled and locked flags")
    void shouldBuildUserWithStatusFlags() {
        // Act: Build User with status flags
        User user = User.builder()
                .username("locked")
                .email("locked@example.com")
                .password("pass")
                .enabled(false)
                .locked(true)
                .build();

        // Assert: Verify status flags
        assertThat(user.getEnabled()).isFalse();
        assertThat(user.getLocked()).isTrue();
    }

    /**
     * Tests User builder with name fields.
     * 
     * <p>
     * <strong>Scenario:</strong> Builder with firstName and lastName.
     * 
     * <p>
     * <strong>Given:</strong> FirstName="John", LastName="Doe".
     * 
     * <p>
     * <strong>When:</strong> Builder builds User.
     * 
     * <p>
     * <strong>Then:</strong> User has specified names.
     * 
     * <p>
     * <strong>Coverage:</strong> Tests firstName and lastName builder methods.
     */
    @Test
    @DisplayName("Should build user with name fields")
    void shouldBuildUserWithNames() {
        // Act: Build User with names
        User user = User.builder()
                .username("johndoe")
                .email("john@example.com")
                .password("pass")
                .firstName("John")
                .lastName("Doe")
                .build();

        // Assert: Verify names
        assertThat(user.getFirstName()).isEqualTo("John");
        assertThat(user.getLastName()).isEqualTo("Doe");
    }

    /**
     * Tests User builder with phone field.
     * 
     * <p>
     * <strong>Scenario:</strong> Builder with phone number.
     * 
     * <p>
     * <strong>Given:</strong> Phone="+33123456789".
     * 
     * <p>
     * <strong>When:</strong> Builder builds User.
     * 
     * <p>
     * <strong>Then:</strong> User has specified phone.
     * 
     * <p>
     * <strong>Coverage:</strong> Tests phone builder method.
     */
    @Test
    @DisplayName("Should build user with phone number")
    void shouldBuildUserWithPhone() {
        // Act: Build User with phone
        User user = User.builder()
                .username("phoneuser")
                .email("phone@example.com")
                .password("pass")
                .phone("+33123456789")
                .build();

        // Assert: Verify phone
        assertThat(user.getPhone()).isEqualTo("+33123456789");
    }

    /**
     * Tests User builder with timestamp fields.
     * 
     * <p>
     * <strong>Scenario:</strong> Builder with createdAt, updatedAt, lastLoginAt.
     * 
     * <p>
     * <strong>Given:</strong> Timestamp values for all date fields.
     * 
     * <p>
     * <strong>When:</strong> Builder builds User.
     * 
     * <p>
     * <strong>Then:</strong> User has specified timestamps.
     * 
     * <p>
     * <strong>Coverage:</strong> Tests timestamp builder methods.
     */
    @Test
    @DisplayName("Should build user with timestamps")
    void shouldBuildUserWithTimestamps() {
        // Arrange: Create timestamps
        LocalDateTime created = LocalDateTime.of(2026, 1, 1, 10, 0);
        LocalDateTime updated = LocalDateTime.of(2026, 1, 10, 15, 30);
        LocalDateTime lastLogin = LocalDateTime.of(2026, 1, 12, 9, 0);

        // Act: Build User with timestamps
        User user = User.builder()
                .username("timeuser")
                .email("time@example.com")
                .password("pass")
                .createdAt(created)
                .updatedAt(updated)
                .lastLoginAt(lastLogin)
                .build();

        // Assert: Verify timestamps
        assertThat(user.getCreatedAt()).isEqualTo(created);
        assertThat(user.getUpdatedAt()).isEqualTo(updated);
        assertThat(user.getLastLoginAt()).isEqualTo(lastLogin);
    }

    /**
     * Tests @PrePersist lifecycle callback.
     * 
     * <p>
     * <strong>Scenario:</strong> JPA callback sets default values before insert.
     * 
     * <p>
     * <strong>Given:</strong> User built without enabled/locked/timestamps.
     * 
     * <p>
     * <strong>When:</strong> onCreate() is called manually (simulates JPA).
     * 
     * <p>
     * <strong>Then:</strong> Default values are set (enabled=true, locked=false,
     * timestamps=now).
     * 
     * <p>
     * <strong>Coverage:</strong> Tests onCreate() lifecycle method.
     */
    @Test
    @DisplayName("Should set default values on @PrePersist")
    void shouldSetDefaultValuesOnPrePersist() {
        // Arrange: Build User without defaults
        User user = User.builder()
                .username("newuser")
                .email("new@example.com")
                .password("pass")
                .build();

        // Act: Simulate @PrePersist
        user.onCreate();

        // Assert: Verify default values are set
        assertThat(user.getEnabled()).isTrue();
        assertThat(user.getLocked()).isFalse();
        assertThat(user.getCreatedAt()).isNotNull();
        assertThat(user.getUpdatedAt()).isNotNull();
        assertThat(user.getRoles()).contains(UserRole.USER);
    }

    /**
     * Tests @PreUpdate lifecycle callback.
     * 
     * <p>
     * <strong>Scenario:</strong> JPA callback updates timestamp before update.
     * 
     * <p>
     * <strong>Given:</strong> User with old updatedAt timestamp.
     * 
     * <p>
     * <strong>When:</strong> onUpdate() is called manually (simulates JPA).
     * 
     * <p>
     * <strong>Then:</strong> UpdatedAt timestamp is refreshed to current time.
     * 
     * <p>
     * <strong>Coverage:</strong> Tests onUpdate() lifecycle method.
     */
    @Test
    @DisplayName("Should refresh timestamp on @PreUpdate")
    void shouldRefreshTimestampOnPreUpdate() {
        // Arrange: Build User with old timestamp
        LocalDateTime oldTimestamp = LocalDateTime.of(2026, 1, 1, 10, 0);
        User user = User.builder()
                .username("updateuser")
                .email("update@example.com")
                .password("pass")
                .updatedAt(oldTimestamp)
                .build();

        // Act: Simulate @PreUpdate
        user.onUpdate();

        // Assert: Verify updatedAt is refreshed
        assertThat(user.getUpdatedAt()).isNotEqualTo(oldTimestamp);
        assertThat(user.getUpdatedAt()).isAfter(oldTimestamp);
    }

    /**
     * Tests addRole() business method.
     * 
     * <p>
     * <strong>Scenario:</strong> Add ADMIN role to user with USER role.
     * 
     * <p>
     * <strong>Given:</strong> User has USER role.
     * 
     * <p>
     * <strong>When:</strong> addRole(ADMIN) is called.
     * 
     * <p>
     * <strong>Then:</strong> User has both USER and ADMIN roles.
     * 
     * <p>
     * <strong>Coverage:</strong> Tests addRole() method.
     */
    @Test
    @DisplayName("Should add role to user")
    void shouldAddRoleToUser() {
        // Arrange: Build User with USER role
        Set<UserRole> roles = new HashSet<>();
        roles.add(UserRole.USER);
        User user = User.builder()
                .username("roleuser")
                .email("role@example.com")
                .password("pass")
                .roles(roles)
                .build();

        // Act: Add ADMIN role
        user.addRole(UserRole.ADMIN);

        // Assert: Verify both roles present
        assertThat(user.getRoles()).containsExactlyInAnyOrder(UserRole.USER, UserRole.ADMIN);
    }

    /**
     * Tests removeRole() business method.
     * 
     * <p>
     * <strong>Scenario:</strong> Remove ADMIN role from user with multiple roles.
     * 
     * <p>
     * <strong>Given:</strong> User has USER and ADMIN roles.
     * 
     * <p>
     * <strong>When:</strong> removeRole(ADMIN) is called.
     * 
     * <p>
     * <strong>Then:</strong> User has only USER role remaining.
     * 
     * <p>
     * <strong>Coverage:</strong> Tests removeRole() method.
     */
    @Test
    @DisplayName("Should remove role from user")
    void shouldRemoveRoleFromUser() {
        // Arrange: Build User with multiple roles
        Set<UserRole> roles = new HashSet<>();
        roles.add(UserRole.USER);
        roles.add(UserRole.ADMIN);
        User user = User.builder()
                .username("roleuser")
                .email("role@example.com")
                .password("pass")
                .roles(roles)
                .build();

        // Act: Remove ADMIN role
        user.removeRole(UserRole.ADMIN);

        // Assert: Verify only USER role remains
        assertThat(user.getRoles()).containsExactly(UserRole.USER);
    }

    /**
     * Tests getFullName() when both firstName and lastName are set.
     * 
     * <p>
     * <strong>Scenario:</strong> User has complete name.
     * 
     * <p>
     * <strong>Given:</strong> FirstName="John", LastName="Doe".
     * 
     * <p>
     * <strong>When:</strong> getFullName() is called.
     * 
     * <p>
     * <strong>Then:</strong> Returns "John Doe".
     * 
     * <p>
     * <strong>Coverage:</strong> Tests getFullName() happy path.
     */
    @Test
    @DisplayName("Should return full name when both names are set")
    void shouldReturnFullNameWhenBothNamesSet() {
        // Arrange: Build User with both names
        User user = User.builder()
                .username("johndoe")
                .email("john@example.com")
                .password("pass")
                .firstName("John")
                .lastName("Doe")
                .build();

        // Act & Assert: Verify full name
        assertThat(user.getFullName()).isEqualTo("John Doe");
    }

    /**
     * Tests getFullName() when only firstName is set.
     * 
     * <p>
     * <strong>Scenario:</strong> User has only first name.
     * 
     * <p>
     * <strong>Given:</strong> FirstName="John", LastName=null.
     * 
     * <p>
     * <strong>When:</strong> getFullName() is called.
     * 
     * <p>
     * <strong>Then:</strong> Returns "John".
     * 
     * <p>
     * <strong>Coverage:</strong> Tests getFullName() with partial name.
     */
    @Test
    @DisplayName("Should return first name only when last name is null")
    void shouldReturnFirstNameOnlyWhenLastNameNull() {
        // Arrange: Build User with only firstName
        User user = User.builder()
                .username("john")
                .email("john@example.com")
                .password("pass")
                .firstName("John")
                .build();

        // Act & Assert: Verify only first name returned
        assertThat(user.getFullName()).isEqualTo("John");
    }

    /**
     * Tests getFullName() when only lastName is set.
     * 
     * <p>
     * <strong>Scenario:</strong> User has only last name.
     * 
     * <p>
     * <strong>Given:</strong> FirstName=null, LastName="Doe".
     * 
     * <p>
     * <strong>When:</strong> getFullName() is called.
     * 
     * <p>
     * <strong>Then:</strong> Returns "Doe".
     * 
     * <p>
     * <strong>Coverage:</strong> Tests getFullName() with partial name.
     */
    @Test
    @DisplayName("Should return last name only when first name is null")
    void shouldReturnLastNameOnlyWhenFirstNameNull() {
        // Arrange: Build User with only lastName
        User user = User.builder()
                .username("doe")
                .email("doe@example.com")
                .password("pass")
                .lastName("Doe")
                .build();

        // Act & Assert: Verify only last name returned
        assertThat(user.getFullName()).isEqualTo("Doe");
    }

    /**
     * Tests getFullName() when neither name is set (fallback to username).
     * 
     * <p>
     * <strong>Scenario:</strong> User has no name fields.
     * 
     * <p>
     * <strong>Given:</strong> FirstName=null, LastName=null, Username="johndoe".
     * 
     * <p>
     * <strong>When:</strong> getFullName() is called.
     * 
     * <p>
     * <strong>Then:</strong> Returns username as fallback "johndoe".
     * 
     * <p>
     * <strong>Coverage:</strong> Tests getFullName() fallback logic.
     */
    @Test
    @DisplayName("Should return username when names are null")
    void shouldReturnUsernameWhenNamesAreNull() {
        // Arrange: Build User without names
        User user = User.builder()
                .username("johndoe")
                .email("john@example.com")
                .password("pass")
                .build();

        // Act & Assert: Verify username is returned as fallback
        assertThat(user.getFullName()).isEqualTo("johndoe");
    }
}
