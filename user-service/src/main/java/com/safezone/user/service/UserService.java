package com.safezone.user.service;

import com.safezone.user.dto.AuthResponse;
import com.safezone.user.dto.LoginRequest;
import com.safezone.user.dto.RegisterRequest;
import com.safezone.user.dto.UpdateUserRequest;
import com.safezone.user.dto.UserResponse;
import com.safezone.user.entity.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    UserResponse getUserById(Long id);

    UserResponse getUserByUsername(String username);

    Page<UserResponse> getAllUsers(Pageable pageable);

    Page<UserResponse> searchUsers(String search, Pageable pageable);

    UserResponse updateUser(Long id, UpdateUserRequest request);

    void deleteUser(Long id);

    UserResponse addRole(Long userId, UserRole role);

    UserResponse removeRole(Long userId, UserRole role);

    void enableUser(Long id);

    void disableUser(Long id);
}
