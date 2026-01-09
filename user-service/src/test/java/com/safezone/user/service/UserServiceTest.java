package com.safezone.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.safezone.common.exception.BusinessException;
import com.safezone.common.exception.ResourceNotFoundException;
import com.safezone.common.security.JwtTokenProvider;
import com.safezone.user.dto.AuthResponse;
import com.safezone.user.dto.LoginRequest;
import com.safezone.user.dto.RegisterRequest;
import com.safezone.user.dto.UpdateUserRequest;
import com.safezone.user.dto.UserResponse;
import com.safezone.user.entity.User;
import com.safezone.user.entity.UserRole;
import com.safezone.user.mapper.UserMapper;
import com.safezone.user.repository.UserRepository;
import com.safezone.user.service.impl.UserServiceImpl;

/**
 * Unit tests for {@link UserServiceImpl}.
 * <p>
 * Tests cover user registration, authentication, profile management,
 * and role operations including both success and error scenarios.
 * </p>
 *
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2024-01-06
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    private UserServiceImpl userService;

    private User testUser;
    private UserResponse testUserResponse;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(
                userRepository,
                userMapper,
                passwordEncoder,
                jwtTokenProvider,
                86400000L);

        Set<UserRole> roles = new HashSet<>();
        roles.add(UserRole.USER);

        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .firstName("Test")
                .lastName("User")
                .roles(roles)
                .enabled(true)
                .locked(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        testUserResponse = new UserResponse(
                1L,
                "testuser",
                "test@example.com",
                "Test",
                "User",
                null,
                "Test User",
                Set.of(UserRole.USER),
                true,
                LocalDateTime.now(),
                null);
    }

    @Nested
    @DisplayName("Registration Tests")
    class RegistrationTests {

        @Test
        @DisplayName("Should register user successfully")
        void shouldRegisterUserSuccessfully() {
            RegisterRequest request = new RegisterRequest(
                    "newuser",
                    "new@example.com",
                    "Password123",
                    "New",
                    "User",
                    null);

            given(userRepository.existsByUsername("newuser")).willReturn(false);
            given(userRepository.existsByEmail("new@example.com")).willReturn(false);
            given(userMapper.toEntity(request)).willReturn(testUser);
            given(passwordEncoder.encode("Password123")).willReturn("encodedPassword");
            given(userRepository.save(Objects.requireNonNull(testUser))).willReturn(testUser);
            given(userMapper.toResponse(testUser)).willReturn(testUserResponse);
            given(jwtTokenProvider.generateToken(anyString(), anyList())).willReturn("jwt-token");

            AuthResponse result = userService.register(request);

            assertThat(result).isNotNull();
            assertThat(result.token()).isEqualTo("jwt-token");
            assertThat(result.expiresIn()).isEqualTo(86400L);
            verify(userRepository).save(Objects.requireNonNull(testUser));
            assertThat(testUser).isNotNull();
        }

        @Test
        @DisplayName("Should throw exception for duplicate username")
        void shouldThrowExceptionForDuplicateUsername() {
            RegisterRequest request = new RegisterRequest(
                    "existinguser",
                    "new@example.com",
                    "Password123",
                    null,
                    null,
                    null);

            given(userRepository.existsByUsername("existinguser")).willReturn(true);

            assertThatThrownBy(() -> userService.register(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Username is already taken");
        }

        @Test
        @DisplayName("Should throw exception for duplicate email")
        void shouldThrowExceptionForDuplicateEmail() {
            RegisterRequest request = new RegisterRequest(
                    "newuser",
                    "existing@example.com",
                    "Password123",
                    null,
                    null,
                    null);

            given(userRepository.existsByUsername("newuser")).willReturn(false);
            given(userRepository.existsByEmail("existing@example.com")).willReturn(true);

            assertThatThrownBy(() -> userService.register(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Email is already in use");
        }
    }

    @Nested
    @DisplayName("Login Tests")
    class LoginTests {

        @Test
        @DisplayName("Should login successfully")
        void shouldLoginSuccessfully() {
            LoginRequest request = new LoginRequest("testuser", "password123");

            given(userRepository.findByUsername("testuser")).willReturn(Optional.of(testUser));
            given(passwordEncoder.matches("password123", "encodedPassword")).willReturn(true);
            given(userRepository.save(Objects.requireNonNull(testUser))).willReturn(testUser);
            given(userMapper.toResponse(testUser)).willReturn(testUserResponse);
            given(jwtTokenProvider.generateToken(anyString(), anyList())).willReturn("jwt-token");

            AuthResponse result = userService.login(request);

            assertThat(result).isNotNull();
            assertThat(result.token()).isEqualTo("jwt-token");
            assertThat(result.expiresIn()).isEqualTo(86400L);
        }

        @Test
        @DisplayName("Should throw exception for invalid credentials")
        void shouldThrowExceptionForInvalidCredentials() {
            LoginRequest request = new LoginRequest("testuser", "wrongpassword");

            given(userRepository.findByUsername("testuser")).willReturn(Optional.of(testUser));
            given(passwordEncoder.matches("wrongpassword", "encodedPassword")).willReturn(false);

            assertThatThrownBy(() -> userService.login(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Invalid username or password");
        }

        @Test
        @DisplayName("Should throw exception for disabled account")
        void shouldThrowExceptionForDisabledAccount() {
            testUser.setEnabled(false);
            LoginRequest request = new LoginRequest("testuser", "password123");

            given(userRepository.findByUsername("testuser")).willReturn(Optional.of(testUser));
            given(passwordEncoder.matches("password123", "encodedPassword")).willReturn(true);

            assertThatThrownBy(() -> userService.login(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Account is disabled");
        }

        @Test
        @DisplayName("Should throw exception for locked account")
        void shouldThrowExceptionForLockedAccount() {
            testUser.setLocked(true);
            LoginRequest request = new LoginRequest("testuser", "password123");

            given(userRepository.findByUsername("testuser")).willReturn(Optional.of(testUser));
            given(passwordEncoder.matches("password123", "encodedPassword")).willReturn(true);

            assertThatThrownBy(() -> userService.login(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Account is locked");
        }
    }

    @Nested
    @DisplayName("Get User Tests")
    class GetUserTests {

        @Test
        @DisplayName("Should get user by ID")
        void shouldGetUserById() {
            given(userRepository.findById(1L)).willReturn(Optional.of(testUser));
            given(userMapper.toResponse(testUser)).willReturn(testUserResponse);

            UserResponse result = userService.getUserById(1L);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Should throw exception when user not found")
        void shouldThrowExceptionWhenUserNotFound() {
            given(userRepository.findById(999L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> userService.getUserById(999L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("User not found");
        }

        @Test
        @DisplayName("Should get user by username")
        void shouldGetUserByUsername() {
            given(userRepository.findByUsername("testuser")).willReturn(Optional.of(testUser));
            given(userMapper.toResponse(testUser)).willReturn(testUserResponse);

            UserResponse result = userService.getUserByUsername("testuser");

            assertThat(result).isNotNull();
            assertThat(result.username()).isEqualTo("testuser");
        }

        @Test
        @DisplayName("Should throw exception when username not found")
        void shouldThrowExceptionWhenUsernameNotFound() {
            given(userRepository.findByUsername("missing")).willReturn(Optional.empty());

            assertThatThrownBy(() -> userService.getUserByUsername("missing"))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("Should get all users with pagination")
        void shouldGetAllUsersWithPagination() {
            Pageable pageable = PageRequest.of(0, 10);
            List<User> userList = new java.util.ArrayList<>(List.of(testUser));
            Page<User> userPage = new PageImpl<>(userList, pageable, 1);

            given(userRepository.findAll(pageable)).willReturn(userPage);
            given(userMapper.toResponse(testUser)).willReturn(testUserResponse);

            Page<UserResponse> result = userService.getAllUsers(pageable);

            assertThat(result.getContent()).hasSize(1);
        }

        @Test
        @DisplayName("Should throw NPE when pageable is null")
        void shouldThrowNpeWhenPageableNull() {
            assertThatThrownBy(() -> userService.getAllUsers(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("Pageable must not be null");
        }
    }

    @Nested
    @DisplayName("Search Users Tests")
    class SearchUsersTests {
        @Test
        @DisplayName("Should search users and map to responses")
        void shouldSearchUsersAndMap() {
            Pageable pageable = PageRequest.of(0, 5);
            List<User> userList = new java.util.ArrayList<>(List.of(testUser));
            Page<User> userPage = new PageImpl<>(userList, pageable, 1);

            given(userRepository.searchUsers("te", pageable)).willReturn(userPage);
            given(userMapper.toResponse(testUser)).willReturn(testUserResponse);

            Page<UserResponse> result = userService.searchUsers("te", pageable);

            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).username()).isEqualTo("testuser");
        }
    }

    @Nested
    @DisplayName("Update User Tests")
    class UpdateUserTests {

        @Test
        @DisplayName("Should update user successfully")
        void shouldUpdateUserSuccessfully() {
            UpdateUserRequest request = new UpdateUserRequest(
                    "UpdatedFirst",
                    "UpdatedLast",
                    null,
                    null);

            given(userRepository.findById(1L)).willReturn(Optional.of(testUser));
            given(userRepository.save(Objects.requireNonNull(testUser))).willReturn(testUser);
            given(userMapper.toResponse(testUser)).willReturn(testUserResponse);

            UserResponse result = userService.updateUser(1L, request);

            assertThat(result).isNotNull();
            verify(userRepository).save(Objects.requireNonNull(testUser));
            assertThat(testUser).isNotNull();
        }

        @Test
        @DisplayName("Should throw exception when email already in use")
        void shouldThrowExceptionWhenEmailInUse() {
            UpdateUserRequest request = new UpdateUserRequest(
                    null,
                    null,
                    "existing@example.com",
                    null);

            given(userRepository.findById(1L)).willReturn(Optional.of(testUser));
            given(userRepository.existsByEmail("existing@example.com")).willReturn(true);

            assertThatThrownBy(() -> userService.updateUser(1L, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Email is already in use");
        }
    }

    @Nested
    @DisplayName("Role Management Tests")
    class RoleManagementTests {

        @Test
        @DisplayName("Should add role to user")
        void shouldAddRoleToUser() {
            given(userRepository.findById(1L)).willReturn(Optional.of(testUser));
            given(userRepository.save(Objects.requireNonNull(testUser))).willReturn(testUser);
            given(userMapper.toResponse(testUser)).willReturn(testUserResponse);

            UserResponse result = userService.addRole(1L, UserRole.ADMIN);

            assertThat(result).isNotNull();
            verify(userRepository).save(Objects.requireNonNull(testUser));
            assertThat(testUser).isNotNull();
        }

        @Test
        @DisplayName("Should throw exception when removing last role")
        void shouldThrowExceptionWhenRemovingLastRole() {
            testUser.setRoles(new HashSet<>(Set.of(UserRole.USER)));
            given(userRepository.findById(1L)).willReturn(Optional.of(testUser));

            assertThatThrownBy(() -> userService.removeRole(1L, UserRole.USER))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Cannot remove the last role");
        }

        @Test
        @DisplayName("Should remove role when multiple roles exist")
        void shouldRemoveRoleWhenMultipleRolesExist() {
            testUser.setRoles(new HashSet<>(Set.of(UserRole.USER, UserRole.ADMIN)));
            given(userRepository.findById(1L)).willReturn(Optional.of(testUser));
            given(userRepository.save(Objects.requireNonNull(testUser))).willReturn(testUser);
            given(userMapper.toResponse(testUser)).willReturn(testUserResponse);

            UserResponse result = userService.removeRole(1L, UserRole.ADMIN);

            assertThat(result).isNotNull();
            assertThat(testUser.getRoles()).containsExactly(UserRole.USER);
        }
    }

    @Nested
    @DisplayName("Status Management Tests")
    class StatusManagementTests {
        @Test
        @DisplayName("Should soft delete user by disabling")
        void shouldSoftDeleteUser() {
            given(userRepository.findById(1L)).willReturn(Optional.of(testUser));

            userService.deleteUser(1L);

            verify(userRepository).save(Objects.requireNonNull(testUser));
            assertThat(testUser.getEnabled()).isFalse();
        }

        @Test
        @DisplayName("Should enable user")
        void shouldEnableUser() {
            testUser.setEnabled(false);
            given(userRepository.findById(1L)).willReturn(Optional.of(testUser));

            userService.enableUser(1L);

            verify(userRepository).save(Objects.requireNonNull(testUser));
            assertThat(testUser.getEnabled()).isTrue();
        }

        @Test
        @DisplayName("Should disable user")
        void shouldDisableUser() {
            testUser.setEnabled(true);
            given(userRepository.findById(1L)).willReturn(Optional.of(testUser));

            userService.disableUser(1L);

            verify(userRepository).save(Objects.requireNonNull(testUser));
            assertThat(testUser.getEnabled()).isFalse();
        }
    }
}
