package com.safezone.user.entity;

/**
 * Enumeration of user roles for access control.
 * Defines the permission levels available in the system.
 *
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2026-01-06
 */
public enum UserRole {
    /** Standard user with basic access. */
    USER,
    /** Administrator with full system access. */
    ADMIN,
    /** Inventory manager for stock operations. */
    INVENTORY,
    /** Customer support representative. */
    SUPPORT
}
