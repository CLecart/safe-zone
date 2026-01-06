package com.safezone.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safezone.common.security.JwtTokenProvider;
import com.safezone.user.dto.AuthResponse;
import com.safezone.user.dto.LoginRequest;
import com.safezone.user.dto.RegisterRequest;
import com.safezone.user.dto.UserResponse;
import com.safezone.user.entity.UserRole;
import com.safezone.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@DisplayName("Auth Controller Tests")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    private UserResponse userResponse;
    private AuthResponse authResponse;

    @BeforeEach
    void setUp() {
        userResponse = new UserResponse(
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
                null
        );

        authResponse = new AuthResponse(
                "jwt-token",
                "Bearer",
                3600000L,
                userResponse
        );
    }

    @Nested
    @DisplayName("Registration Endpoint Tests")
    class RegistrationEndpointTests {

        @Test
        @DisplayName("Should register user successfully")
        void shouldRegisterUserSuccessfully() throws Exception {
            RegisterRequest request = new RegisterRequest(
                    "newuser",
                    "new@example.com",
                    "Password123",
                    "New",
                    "User",
                    null
            );

            given(userService.register(any(RegisterRequest.class))).willReturn(authResponse);

            mockMvc.perform(post("/api/auth/register")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.token").value("jwt-token"));
        }

        @Test
        @DisplayName("Should reject invalid registration request")
        void shouldRejectInvalidRegistrationRequest() throws Exception {
            RegisterRequest request = new RegisterRequest(
                    "",
                    "invalid-email",
                    "short",
                    null,
                    null,
                    null
            );

            mockMvc.perform(post("/api/auth/register")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Login Endpoint Tests")
    class LoginEndpointTests {

        @Test
        @DisplayName("Should login successfully")
        void shouldLoginSuccessfully() throws Exception {
            LoginRequest request = new LoginRequest("testuser", "password123");

            given(userService.login(any(LoginRequest.class))).willReturn(authResponse);

            mockMvc.perform(post("/api/auth/login")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.token").value("jwt-token"));
        }

        @Test
        @DisplayName("Should reject empty credentials")
        void shouldRejectEmptyCredentials() throws Exception {
            LoginRequest request = new LoginRequest("", "");

            mockMvc.perform(post("/api/auth/login")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Token Refresh Tests")
    @WithMockUser
    class TokenRefreshTests {

        @Test
        @DisplayName("Should refresh token successfully")
        void shouldRefreshTokenSuccessfully() throws Exception {
            given(userService.refreshToken("testuser")).willReturn(authResponse);

            mockMvc.perform(post("/api/auth/refresh")
                            .with(csrf())
                            .header("Authorization", "Bearer valid-token"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }
    }
}
