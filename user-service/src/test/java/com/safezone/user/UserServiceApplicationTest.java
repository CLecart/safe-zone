package com.safezone.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for the User Service Application.
 * <p>
 * Verifies that the Spring application context loads correctly
 * and all required beans are properly configured.
 * </p>
 *
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2024-01-06
 */
@SpringBootTest
@DisplayName("User Service Application Tests")
class UserServiceApplicationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    @DisplayName("Should load application context successfully")
    void shouldLoadContext() {
        assertThat(applicationContext).isNotNull();
    }

    @Test
    @DisplayName("Should have user service bean")
    void shouldHaveUserServiceBean() {
        assertThat(applicationContext.containsBean("userServiceImpl")).isTrue();
    }
}
