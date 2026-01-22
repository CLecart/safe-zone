package com.safezone.user.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerSecurityIntegrationTest {
    @Autowired
    private com.safezone.user.repository.UserRepository userRepository;

    private Long testUserId;

    @SuppressWarnings("null")
    @org.junit.jupiter.api.BeforeEach
    void setupTestUser() {
        // Remove any existing test user with the same email to avoid unique constraint
        // violation
        userRepository.findByEmail("testuser@example.com").ifPresent(userRepository::delete);
        com.safezone.user.entity.User user = com.safezone.user.entity.User.builder()
                .username("testuser")
                .email("testuser@example.com")
                .password("$2a$10$testhash") // dummy bcrypt hash
                .firstName("Test")
                .lastName("User")
                .enabled(true)
                .locked(false)
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .build();
        testUserId = userRepository.save(user).getId();
    }

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /api/v1/users/{id} should be public")
    void getUserById_publicAccess() throws Exception {
        mockMvc.perform(get("/api/v1/users/" + testUserId))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/v1/users should require authentication")
    void getAllUsers_requiresAuth() throws Exception {
        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/v1/users/{id}/roles/ADMIN should require authentication")
    void addRole_requiresAuth() throws Exception {
        mockMvc.perform(post("/api/v1/users/1/roles/ADMIN"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("DELETE /api/v1/users/{id} should require authentication")
    void deleteUser_requiresAuth() throws Exception {
        mockMvc.perform(delete("/api/v1/users/1"))
                .andExpect(status().isUnauthorized());
    }
}
