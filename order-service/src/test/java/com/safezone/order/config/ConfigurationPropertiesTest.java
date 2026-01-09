package com.safezone.order.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests for configuration properties classes.
 *
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2026-01-09
 */
@DisplayName("Configuration Properties Tests")
class ConfigurationPropertiesTest {

    @Test
    @DisplayName("ServiceProperties sets and gets product URL")
    void servicePropertiesProductUrl() {
        ServiceProperties props = new ServiceProperties();
        props.getProduct().setUrl("http://localhost:8081");

        assertThat(props.getProduct().getUrl()).isEqualTo("http://localhost:8081");
    }

    @Test
    @DisplayName("ServiceProperties sets and gets user URL")
    void servicePropertiesUserUrl() {
        ServiceProperties props = new ServiceProperties();
        props.getUser().setUrl("http://localhost:8083");

        assertThat(props.getUser().getUrl()).isEqualTo("http://localhost:8083");
    }

    @Test
    @DisplayName("JwtProperties sets and gets secret")
    void jwtPropertiesSecret() {
        JwtProperties props = new JwtProperties();
        props.setSecret("test-secret");

        assertThat(props.getSecret()).isEqualTo("test-secret");
    }

    @Test
    @DisplayName("JwtProperties sets and gets expiration")
    void jwtPropertiesExpiration() {
        JwtProperties props = new JwtProperties();
        props.setExpiration(86400000L);

        assertThat(props.getExpiration()).isEqualTo(86400000L);
    }

    @Test
    @DisplayName("ApplicationProperties sets and gets JWT secret")
    void applicationPropertiesJwtSecret() {
        ApplicationProperties props = new ApplicationProperties();
        props.getJwt().setSecret("app-secret");

        assertThat(props.getJwt().getSecret()).isEqualTo("app-secret");
    }
}
