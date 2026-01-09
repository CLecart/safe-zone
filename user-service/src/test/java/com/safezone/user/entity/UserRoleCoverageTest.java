package com.safezone.user.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Coverage tests for {@link UserRole} enum.
 * Tests all enum values are accessible.
 *
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2026-01-09
 */
@DisplayName("UserRole Enum Coverage Tests")
class UserRoleCoverageTest {

    @Test
    @DisplayName("All UserRole enum values exist")
    void allEnumValuesExist() {
        UserRole[] roles = UserRole.values();

        assertThat(roles)
                .contains(UserRole.USER, UserRole.ADMIN, UserRole.INVENTORY, UserRole.SUPPORT)
                .hasSize(4);
    }

    @Test
    @DisplayName("UserRole valueOf works correctly")
    void valueOfReturnsCorrectEnum() {
        assertThat(UserRole.valueOf("USER")).isEqualTo(UserRole.USER);
        assertThat(UserRole.valueOf("ADMIN")).isEqualTo(UserRole.ADMIN);
        assertThat(UserRole.valueOf("INVENTORY")).isEqualTo(UserRole.INVENTORY);
        assertThat(UserRole.valueOf("SUPPORT")).isEqualTo(UserRole.SUPPORT);
    }

    @Test
    @DisplayName("UserRole name returns correct string")
    void nameReturnsCorrectString() {
        assertThat(UserRole.USER.name()).isEqualTo("USER");
        assertThat(UserRole.ADMIN.name()).isEqualTo("ADMIN");
        assertThat(UserRole.INVENTORY.name()).isEqualTo("INVENTORY");
        assertThat(UserRole.SUPPORT.name()).isEqualTo("SUPPORT");
    }
}
