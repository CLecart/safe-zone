package com.safezone.user.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.safezone.user.service.UserService;

/**
 * Implementation of the {@link UserService} interface.
 * Provides user management and authentication business logic.
 *
 * <p>
 * Handles user registration, authentication, profile management,
 * and role-based access control. Passwords are securely hashed.
 * </p>
 *
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2026-01-06
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private static final String USER_RESOURCE = "User";

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final long jwtExpiration;

    /**
     * Constructs a UserServiceImpl with required dependencies.
     *
     * @param userRepository   repository for user persistence
     * @param userMapper       mapper for DTO/entity conversion
     * @param passwordEncoder  encoder for secure password hashing
     * @param jwtTokenProvider provider for JWT token operations
     * @param jwtExpiration    JWT token expiration time in milliseconds
     */
    public UserServiceImpl(
            UserRepository userRepository,
            UserMapper userMapper,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider,
            @Value("${jwt.expiration:86400000}") long jwtExpiration) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.jwtExpiration = jwtExpiration;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        logger.info("Registering new user: {}", request.username());

        validateRegistration(request);

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.addRole(UserRole.USER);
        user.setEnabled(true);
        user.setLocked(false);

        User savedUser = userRepository.save(user);
        logger.info("User registered successfully: {}", savedUser.getUsername());

        return createAuthResponse(savedUser);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        logger.info("User login attempt: {}", request.username());

        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new BusinessException("INVALID_CREDENTIALS", "Invalid username or password"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            logger.warn("Invalid password for user: {}", request.username());
            throw new BusinessException("INVALID_CREDENTIALS", "Invalid username or password");
        }

        if (Boolean.FALSE.equals(user.getEnabled())) {
            throw new BusinessException("ACCOUNT_DISABLED", "Account is disabled");
        }

        if (Boolean.TRUE.equals(user.getLocked())) {
            throw new BusinessException("ACCOUNT_LOCKED", "Account is locked");
        }

        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        logger.info("User logged in successfully: {}", user.getUsername());
        return createAuthResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        logger.debug("Fetching user by ID: {}", id);
        User user = findUserById(id);
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByUsername(String username) {
        logger.debug("Fetching user by username: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(USER_RESOURCE, "username", username));
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        logger.debug("Fetching all users with pagination");
        return userRepository.findAll(Objects.requireNonNull(pageable, "Pageable must not be null"))
                .map(userMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> searchUsers(String search, Pageable pageable) {
        logger.debug("Searching users with term: {}", search);
        return userRepository.searchUsers(search, pageable)
                .map(userMapper::toResponse);
    }

    @Override
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        logger.info("Updating user: {}", id);

        User user = findUserById(id);

        if (request.email() != null && !request.email().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.email())) {
                throw new BusinessException("EMAIL_EXISTS", "Email is already in use");
            }
            user.setEmail(request.email());
        }

        if (request.firstName() != null) {
            user.setFirstName(request.firstName());
        }
        if (request.lastName() != null) {
            user.setLastName(request.lastName());
        }
        if (request.phone() != null) {
            user.setPhone(request.phone());
        }

        User updatedUser = userRepository.save(Objects.requireNonNull(user, "User must not be null"));
        logger.info("User updated successfully: {}", id);
        return userMapper.toResponse(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        logger.info("Deleting user: {}", id);
        User user = findUserById(id);
        user.setEnabled(false);
        userRepository.save(user);
        logger.info("User soft-deleted: {}", id);
    }

    @Override
    public UserResponse addRole(Long userId, UserRole role) {
        logger.info("Adding role {} to user: {}", role, userId);
        User user = findUserById(userId);
        user.addRole(role);
        User updatedUser = userRepository.save(user);
        return userMapper.toResponse(updatedUser);
    }

    @Override
    public UserResponse removeRole(Long userId, UserRole role) {
        logger.info("Removing role {} from user: {}", role, userId);
        User user = findUserById(userId);

        if (role == UserRole.USER && user.getRoles().size() == 1) {
            throw new BusinessException("INVALID_OPERATION", "Cannot remove the last role from user");
        }

        user.removeRole(role);
        User updatedUser = userRepository.save(user);
        return userMapper.toResponse(updatedUser);
    }

    @Override
    public void enableUser(Long id) {
        logger.info("Enabling user: {}", id);
        User user = findUserById(id);
        user.setEnabled(true);
        userRepository.save(user);
    }

    @Override
    public void disableUser(Long id) {
        logger.info("Disabling user: {}", id);
        User user = findUserById(id);
        user.setEnabled(false);
        userRepository.save(user);
    }

    private User findUserById(Long id) {
        return userRepository.findById(Objects.requireNonNull(id, "User ID must not be null"))
                .orElseThrow(() -> new ResourceNotFoundException(USER_RESOURCE, "id", id));
    }

    private void validateRegistration(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new BusinessException("USERNAME_EXISTS", "Username is already taken");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException("EMAIL_EXISTS", "Email is already in use");
        }
    }

    private AuthResponse createAuthResponse(User user) {
        List<String> roles = user.getRoles().stream()
                .map(UserRole::name)
                .toList();

        String token = jwtTokenProvider.generateToken(user.getUsername(), roles);
        UserResponse userResponse = userMapper.toResponse(user);

        return AuthResponse.of(token, jwtExpiration / 1000, userResponse);
    }
}
