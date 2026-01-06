package com.safezone.user.repository;

import com.safezone.user.entity.User;
import com.safezone.user.entity.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for User entities.
 * Provides CRUD operations and custom queries for user management.
 *
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2026-01-06
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by username.
     *
     * @param username the username
     * @return an Optional containing the user if found
     */
    Optional<User> findByUsername(String username);

    /**
     * Finds a user by email address.
     *
     * @param email the email address
     * @return an Optional containing the user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks if a user with the given username exists.
     *
     * @param username the username
     * @return true if a user with this username exists
     */
    boolean existsByUsername(String username);

    /**
     * Checks if a user with the given email exists.
     *
     * @param email the email address
     * @return true if a user with this email exists
     */
    boolean existsByEmail(String email);

    /**
     * Finds all enabled users with pagination.
     *
     * @param pageable pagination parameters
     * @return page of enabled users
     */
    Page<User> findByEnabledTrue(Pageable pageable);

    /**
     * Finds users with a specific role.
     *
     * @param role     the user role
     * @param pageable pagination parameters
     * @return page of users with the given role
     */
    @Query("SELECT u FROM User u WHERE :role MEMBER OF u.roles")
    Page<User> findByRole(@Param("role") UserRole role, Pageable pageable);

    /**
     * Searches users by username, email, or name.
     *
     * @param search   the search term
     * @param pageable pagination parameters
     * @return page of matching users
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<User> searchUsers(@Param("search") String search, Pageable pageable);
}
