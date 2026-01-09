package com.safezone.user.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.safezone.user.dto.RegisterRequest;
import com.safezone.user.dto.UserResponse;
import com.safezone.user.entity.User;
import com.safezone.user.entity.UserRole;

/**
 * Coverage tests for {@link UserMapper} MapStruct generated code.
 * Focuses on null handling, empty lists, and multiple entity conversions.
 *
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2026-01-09
 */
@SpringBootTest
@DisplayName("UserMapper Coverage Tests")
class UserMapperCoverageTest {

    @Autowired
    private UserMapper userMapper;

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
    @DisplayName("toResponse maps User entity to response DTO")
    void toResponseMapsUserCorrectly() {
        UserResponse response = userMapper.toResponse(testUser);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.username()).isEqualTo("testuser");
        assertThat(response.email()).isEqualTo("test@example.com");
        assertThat(response.enabled()).isTrue();
    }

    @Test
    @DisplayName("toResponse with single role")
    void toResponseWithSingleRole() {
        testUser.setRoles(new HashSet<>(List.of(UserRole.USER)));
        UserResponse response = userMapper.toResponse(testUser);

        assertThat(response.roles()).hasSize(1).contains(UserRole.USER);
    }

    @Test
    @DisplayName("toResponse with multiple roles")
    void toResponseWithMultipleRoles() {
        testUser.setRoles(new HashSet<>(List.of(UserRole.USER, UserRole.ADMIN, UserRole.INVENTORY)));
        UserResponse response = userMapper.toResponse(testUser);

        assertThat(response.roles()).hasSize(3).contains(UserRole.USER, UserRole.ADMIN, UserRole.INVENTORY);
    }

    @Test
    @DisplayName("toResponseList converts multiple users")
    void toResponseListConvertsMultiple() {
        List<User> users = new ArrayList<>();
        users.add(testUser);
        users.add(User.builder()
                .id(2L)
                .username("user2")
                .email("user2@example.com")
                .firstName("Jane")
                .lastName("Smith")
                .phone("+9876543210")
                .password("password")
                .roles(new HashSet<>())
                .enabled(true)
                .locked(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());

        List<UserResponse> responses = userMapper.toResponseList(users);

        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).username()).isEqualTo("testuser");
        assertThat(responses.get(1).username()).isEqualTo("user2");
    }

    @Test
    @DisplayName("toResponseList handles empty list")
    void toResponseListHandlesEmpty() {
        List<UserResponse> responses = userMapper.toResponseList(new ArrayList<>());

        assertThat(responses).isEmpty();
    }

    @Test
    @DisplayName("toResponse handles null user input")
    void toResponseHandlesNullUser() {
        UserResponse response = userMapper.toResponse(null);

        assertThat(response).isNull();
    }

    @Test
    @DisplayName("toResponse maps fullName across null combinations")
    void toResponseMapsFullNameNullCombinations() {
        // null firstName, lastName present
        testUser.setFirstName(null);
        testUser.setLastName("Solo");
        assertThat(userMapper.toResponse(testUser).fullName()).isEqualTo("Solo");

        // firstName present, null lastName
        testUser.setFirstName("Mono");
        testUser.setLastName(null);
        assertThat(userMapper.toResponse(testUser).fullName()).isEqualTo("Mono");

        // both names null -> fallback to username
        testUser.setFirstName(null);
        testUser.setLastName(null);
        assertThat(userMapper.toResponse(testUser).fullName()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("toEntity maps RegisterRequest fields and ignores generated ones")
    void toEntityMapsRegisterRequest() {
        RegisterRequest req = new RegisterRequest(
                "newuser",
                "new@example.com",
                "Password123",
                "Jane",
                "Smith",
                "+330123456789");

        User mapped = userMapper.toEntity(req);

        assertThat(mapped.getId()).isNull();
        assertThat(mapped.getUsername()).isEqualTo("newuser");
        assertThat(mapped.getEmail()).isEqualTo("new@example.com");
        assertThat(mapped.getFirstName()).isEqualTo("Jane");
        assertThat(mapped.getLastName()).isEqualTo("Smith");
        assertThat(mapped.getPhone()).isEqualTo("+330123456789");
        assertThat(mapped.getRoles()).isEmpty();
        assertThat(mapped.getEnabled()).isNull();
        assertThat(mapped.getLocked()).isNull();
        assertThat(mapped.getCreatedAt()).isNull();
        assertThat(mapped.getUpdatedAt()).isNull();
        assertThat(mapped.getLastLoginAt()).isNull();
    }
}
