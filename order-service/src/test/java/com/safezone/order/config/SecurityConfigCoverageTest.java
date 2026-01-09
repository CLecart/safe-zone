package com.safezone.order.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Tests for {@link SecurityConfig}.
 * Verifies security configuration and bean creation.
 *
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2026-01-09
 */
@SpringBootTest
@DisplayName("SecurityConfig Tests")
class SecurityConfigCoverageTest {

    @Autowired
    private SecurityConfig securityConfig;

    @Test
    @DisplayName("SecurityConfig bean is created")
    void securityConfigBeanExists() {
        assertThat(securityConfig).isNotNull();
    }
}
