package com.safezone.user.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.safezone.user.dto.RegisterRequest;
import com.safezone.user.dto.UserResponse;
import com.safezone.user.entity.User;
import com.safezone.user.entity.UserRole;

/**
 * Tests for UserMapper to achieve 100% coverage.
 *
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2026-01-12
 */
@SpringBootTest
@DisplayName("UserMapper Tests")
class UserMapperTest {

    @Autowired
    private UserMapper mapper;

    @Test
    @DisplayName("toEntity maps RegisterRequest to User with ignored fields")
    void toEntityMapsRegisterRequestToUser() {
        RegisterRequest request = new RegisterRequest(
                "testuser",
                "test@example.com",
                "password123",
                "John",
                "Doe",
                "+1234567890");

        User user = mapper.toEntity(request);

        assertThat(user).isNotNull();
        assertThat(user.getId()).isNull();
        assertThat(user.getUsername()).isEqualTo("testuser");
        assertThat(user.getEmail()).isEqualTo("test@example.com");
        assertThat(user.getPassword()).isEqualTo("password123");
        assertThat(user.getFirstName()).isEqualTo("John");
        assertThat(user.getLastName()).isEqualTo("Doe");
        assertThat(user.getPhone()).isEqualTo("+1234567890");
        // MapStruct may initialize collections to empty instead of null
        assertThat(user.getRoles()).satisfiesAnyOf(
                roles -> assertThat(roles).isNull(),
                roles -> assertThat(roles).isEmpty());
        assertThat(user.getEnabled()).isNull();
        assertThat(user.getLocked()).isNull();
        assertThat(user.getCreatedAt()).isNull();
        assertThat(user.getUpdatedAt()).isNull();
        assertThat(user.getLastLoginAt()).isNull();
    }

    @Test
    @DisplayName("toEntity handles null RegisterRequest")
    void toEntityHandlesNullRegisterRequest() {
        User user = mapper.toEntity(null);
        assertThat(user).isNull();
    }

    @Test
    @DisplayName("toResponse maps User to UserResponse")
    void toResponseMapsUserToUserResponse() {
        Set<UserRole> roles = new HashSet<>();
        roles.add(UserRole.USER);
        roles.add(UserRole.ADMIN);

        User user = User.builder()
                .id(1L)
                .username("johndoe")
                .email("john@example.com")
                .firstName("John")
                .lastName("Doe")
                .phone("+9876543210")
                .roles(roles)
                .enabled(true)
                .locked(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        UserResponse response = mapper.toResponse(user);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.username()).isEqualTo("johndoe");
        assertThat(response.email()).isEqualTo("john@example.com");
        assertThat(response.firstName()).isEqualTo("John");
        assertThat(response.lastName()).isEqualTo("Doe");
        assertThat(response.phone()).isEqualTo("+9876543210");
        assertThat(response.roles()).containsExactlyInAnyOrder(UserRole.USER, UserRole.ADMIN);
        assertThat(response.enabled()).isTrue();
    }

    @Test
    @DisplayName("toResponse handles null User")
    void toResponseHandlesNullUser() {
        UserResponse response = mapper.toResponse(null);
        assertThat(response).isNull();
    }

    @Test
    @DisplayName("toResponseList maps User list to UserResponse list")
    void toResponseListMapsUserListToUserResponseList() {
        User user1 = User.builder()
                .id(1L)
                .username("user1")
                .email("user1@example.com")
                .firstName("First1")
                .lastName("Last1")
                .roles(new HashSet<>())
                .enabled(true)
                .locked(false)
                .build();

        User user2 = User.builder()
                .id(2L)
                .username("user2")
                .email("user2@example.com")
                .firstName("First2")
                .lastName("Last2")
                .roles(new HashSet<>())
                .enabled(false)
                .locked(true)
                .build();

        List<UserResponse> responseList = mapper.toResponseList(List.of(user1, user2));

        assertThat(responseList).hasSize(2);
        assertThat(responseList.get(0).id()).isEqualTo(1L);
        assertThat(responseList.get(0).username()).isEqualTo("user1");
        assertThat(responseList.get(0).enabled()).isTrue();
        assertThat(responseList.get(1).id()).isEqualTo(2L);
        assertThat(responseList.get(1).username()).isEqualTo("user2");
        assertThat(responseList.get(1).enabled()).isFalse();
    }

    @Test
    @DisplayName("toResponseList handles null list")
    void toResponseListHandlesNullList() {
        List<UserResponse> responseList = mapper.toResponseList(null);
        assertThat(responseList).isNull();
    }

    @Test
    @DisplayName("toResponseList handles empty list")
    void toResponseListHandlesEmptyList() {
        List<UserResponse> responseList = mapper.toResponseList(new ArrayList<>());
        assertThat(responseList).isEmpty();
    }
}
