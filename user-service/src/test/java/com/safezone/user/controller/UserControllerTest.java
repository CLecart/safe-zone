package com.safezone.user.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import com.safezone.common.dto.ApiResponse;
import com.safezone.common.dto.PageResponse;
import com.safezone.user.dto.UpdateUserRequest;
import com.safezone.user.dto.UserResponse;
import com.safezone.user.entity.UserRole;
import com.safezone.user.service.UserService;

/**
 * Unit tests for {@link UserController} without web context.
 * Avoids deprecated annotations and security stack for clean compilation.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserController Unit Tests")
class UserControllerTest {

    @Mock
    private UserService userService;

    private UserController userController;

    private UserResponse testUserResponse;

    @BeforeEach
    void setUp() {
        userController = new UserController(userService);

        LocalDateTime now = LocalDateTime.now();
        testUserResponse = new UserResponse(
                1L,
                "testuser",
                "test@example.com",
                "John",
                "Doe",
                "+1234567890",
                "John Doe",
                new HashSet<>(List.of(UserRole.USER)),
                true,
                now,
                now);
    }

    @Nested
    @DisplayName("GET operations")
    class GetOperations {
        @Test
        void getUserById_returnsOk() {
            given(userService.getUserById(1L)).willReturn(testUserResponse);

            ResponseEntity<ApiResponse<UserResponse>> response = userController.getUserById(1L);

            assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
            ApiResponse<UserResponse> body = response.getBody();
            org.junit.jupiter.api.Assertions.assertNotNull(body);
            assertThat(body.data().username()).isEqualTo("testuser");
            verify(userService).getUserById(1L);
        }

        @Test
        void getUserByUsername_returnsOk() {
            given(userService.getUserByUsername("testuser")).willReturn(testUserResponse);

            ResponseEntity<ApiResponse<UserResponse>> response = userController.getUserByUsername("testuser");

            assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
            ApiResponse<UserResponse> body = response.getBody();
            org.junit.jupiter.api.Assertions.assertNotNull(body);
            assertThat(body.data().username()).isEqualTo("testuser");
            verify(userService).getUserByUsername("testuser");
        }

        @Test
        void getAllUsers_returnsPagedResponse() {
            @SuppressWarnings("unchecked")
            Page<UserResponse> page = org.mockito.Mockito.mock(Page.class);
            java.util.ArrayList<UserResponse> content = new java.util.ArrayList<>();
            content.add(testUserResponse);
            given(page.getContent()).willReturn(content);
            given(page.getNumber()).willReturn(0);
            given(page.getSize()).willReturn(20);
            given(page.getTotalElements()).willReturn(1L);
            given(userService.getAllUsers(any())).willReturn(page);

            ResponseEntity<ApiResponse<PageResponse<UserResponse>>> response = userController.getAllUsers(0, 20, "id",
                    "asc");

            assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
            ApiResponse<PageResponse<UserResponse>> body = response.getBody();
            org.junit.jupiter.api.Assertions.assertNotNull(body);
            assertThat(body.data().content().get(0).username()).isEqualTo("testuser");
            verify(userService).getAllUsers(any());
        }

        @Test
        void searchUsers_returnsPagedResponse() {
            @SuppressWarnings("unchecked")
            Page<UserResponse> page = org.mockito.Mockito.mock(Page.class);
            java.util.ArrayList<UserResponse> content = new java.util.ArrayList<>();
            content.add(testUserResponse);
            given(page.getContent()).willReturn(content);
            given(page.getNumber()).willReturn(0);
            given(page.getSize()).willReturn(20);
            given(page.getTotalElements()).willReturn(1L);
            given(userService.searchUsers(anyString(), any())).willReturn(page);

            ResponseEntity<ApiResponse<PageResponse<UserResponse>>> response = userController.searchUsers("john", 0,
                    20);

            assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
            ApiResponse<PageResponse<UserResponse>> body = response.getBody();
            org.junit.jupiter.api.Assertions.assertNotNull(body);
            assertThat(body.data().content().get(0).username()).isEqualTo("testuser");
        }
    }

    @Nested
    @DisplayName("Update operations")
    class UpdateOperations {
        @Test
        void updateUser_returnsUpdated() {
            UpdateUserRequest request = new UpdateUserRequest("Jane", "Smith", "jane@example.com", "+9876543210");
            given(userService.updateUser(eq(1L), any())).willReturn(testUserResponse);

            ResponseEntity<ApiResponse<UserResponse>> response = userController.updateUser(1L, request);

            assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
            ApiResponse<UserResponse> body = response.getBody();
            org.junit.jupiter.api.Assertions.assertNotNull(body);
            assertThat(body.data().username()).isEqualTo("testuser");
            verify(userService).updateUser(eq(1L), any());
        }
    }

    @Nested
    @DisplayName("Role operations")
    class RoleOperations {
        @Test
        void addRole_returnsUpdated() {
            given(userService.addRole(1L, UserRole.ADMIN)).willReturn(testUserResponse);

            ResponseEntity<ApiResponse<UserResponse>> response = userController.addRole(1L, UserRole.ADMIN);

            assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
            ApiResponse<UserResponse> body = response.getBody();
            org.junit.jupiter.api.Assertions.assertNotNull(body);
            assertThat(body.data().username()).isEqualTo("testuser");
            verify(userService).addRole(1L, UserRole.ADMIN);
        }

        @Test
        void removeRole_returnsUpdated() {
            given(userService.removeRole(1L, UserRole.USER)).willReturn(testUserResponse);

            ResponseEntity<ApiResponse<UserResponse>> response = userController.removeRole(1L, UserRole.USER);

            assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
            ApiResponse<UserResponse> body = response.getBody();
            org.junit.jupiter.api.Assertions.assertNotNull(body);
            assertThat(body.data().username()).isEqualTo("testuser");
            verify(userService).removeRole(1L, UserRole.USER);
        }
    }

    @Nested
    @DisplayName("Enable/Disable/Delete operations")
    class AdminOperations {
        @Test
        void enableUser_returnsOk() {
            ResponseEntity<ApiResponse<Void>> response = userController.enableUser(1L);
            assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        }

        @Test
        void disableUser_returnsOk() {
            ResponseEntity<ApiResponse<Void>> response = userController.disableUser(1L);
            assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        }

        @Test
        void deleteUser_returnsOk() {
            ResponseEntity<ApiResponse<Void>> response = userController.deleteUser(1L);
            assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        }
    }
}
