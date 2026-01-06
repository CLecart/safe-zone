package com.safezone.user.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity representing a user account in the e-commerce system.
 * Contains authentication credentials, profile information, and role assignments.
 *
 * <p>Users can have multiple roles and track their login activity.
 * Account status is managed through enabled/locked flags.</p>
 *
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2026-01-06
 * @see UserRole
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    /** Unique identifier for the user. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Unique username for authentication. */
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    /** Unique email address for communication and recovery. */
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    /** Hashed password for authentication. */
    @Column(nullable = false)
    private String password;

    /** User's first name. */
    @Column(length = 50)
    private String firstName;

    /** User's last name. */
    @Column(length = 50)
    private String lastName;

    /** Contact phone number. */
    @Column(length = 20)
    private String phone;

    /** Set of roles assigned to this user. */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    @Builder.Default
    private Set<UserRole> roles = new HashSet<>();

    /** Whether the account is enabled for login. */
    @Column(nullable = false)
    private Boolean enabled;

    /** Whether the account is locked due to security concerns. */
    @Column(nullable = false)
    private Boolean locked;

    /** Timestamp when the account was created. */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Timestamp of the last profile update. */
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /** Timestamp of the user's last successful login. */
    private LocalDateTime lastLoginAt;

    /**
     * JPA lifecycle callback executed before persisting a new user.
     * Sets default values for timestamps and account status.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (enabled == null) {
            enabled = true;
        }
        if (locked == null) {
            locked = false;
        }
        if (roles.isEmpty()) {
            roles.add(UserRole.USER);
        }
    }

    /**
     * JPA lifecycle callback executed before updating an existing user.
     * Updates the modification timestamp.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Adds a role to this user's role set.
     *
     * @param role the role to add
     */
    public void addRole(UserRole role) {
        roles.add(role);
    }

    /**
     * Removes a role from this user's role set.
     *
     * @param role the role to remove
     */
    public void removeRole(UserRole role) {
        roles.remove(role);
    }

    /**
     * Returns the user's full name or username if names are not set.
     *
     * @return the full name combining first and last name, or username as fallback
     */
    public String getFullName() {
        if (firstName == null && lastName == null) {
            return username;
        }
        StringBuilder name = new StringBuilder();
        if (firstName != null) {
            name.append(firstName);
        }
        if (lastName != null) {
            if (!name.isEmpty()) {
                name.append(" ");
            }
            name.append(lastName);
        }
        return name.toString();
    }
}
