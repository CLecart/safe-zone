package com.safezone.user.service;

import com.safezone.user.dto.AuthResponse;
import com.safezone.user.dto.LoginRequest;
import com.safezone.user.dto.RegisterRequest;
import com.safezone.user.dto.UpdateUserRequest;
import com.safezone.user.dto.UserResponse;
import com.safezone.user.entity.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for user management and authentication operations.
 * Provides user registration, authentication, and role management functionality.
 *
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2026-01-06
 */
public interface UserService {

    /**
     * Registers a new user account.
     *
     * @param request the registration request containing user details
     * @return authentication response with JWT token and user info
     * @throws com.safezone.common.exception.BusinessException if username or email already exists
     */
    AuthResponse register(RegisterRequest request);

    /**
     * Authenticates a user with credentials.
     *
     * @param request the login request with username and password
     * @return authentication response with JWT token and user info
     * @throws com.safezone.common.exception.BusinessException if credentials invalid or account disabled
     */
    AuthResponse login(LoginRequest request);

    /**
     * Retrieves a user by their unique identifier.
     *
     * @param id the user ID
     * @return the user response
     * @throws com.safezone.common.exception.ResourceNotFoundException if user not found
     */
    UserResponse getUserById(Long id);

    /**
     * Retrieves a user by their username.
     *
     * @param username the username
     * @return the user response
     * @throws com.safezone.common.exception.ResourceNotFoundException if user not found
     */
    UserResponse getUserByUsername(String username);

    /**
     * Retrieves all users with pagination support.
     *
     * @param pageable pagination parameters
     * @return a page of user responses
     */
    Page<UserResponse> getAllUsers(Pageable pageable);

    /**
     * Searches users by username, email, or name.
     *
     * @param search the search query string
     * @param pageable pagination parameters
     * @return a page of matching user responses
     */
    Page<UserResponse> searchUsers(String search, Pageable pageable);

    /**
     * Updates user profile information.
     *
     * @param id the user ID to update
     * @param request the update request with fields to modify
     * @return the updated user response
     * @throws com.safezone.common.exception.ResourceNotFoundException if user not found
     */
    UserResponse updateUser(Long id, UpdateUserRequest request);

    /**
     * Deletes a user account.
     *
     * @param id the user ID to delete
     * @throws com.safezone.common.exception.ResourceNotFoundException if user not found
     */
    void deleteUser(Long id);

    /**
     * Adds a role to a user.
     *
     * @param userId the user ID
     * @param role the role to add
     * @return the updated user response
     */
    UserResponse addRole(Long userId, UserRole role);

    /**
     * Removes a role from a user.
     *
     * @param userId the user ID
     * @param role the role to remove
     * @return the updated user response
     * @throws com.safezone.common.exception.BusinessException if it would remove the last role
     */
    UserResponse removeRole(Long userId, UserRole role);

    /**
     * Enables a user account.
     *
     * @param id the user ID to enable
     */
    void enableUser(Long id);

    /**
     * Disables a user account.
     *
     * @param id the user ID to disable
     */
    void disableUser(Long id);
}
