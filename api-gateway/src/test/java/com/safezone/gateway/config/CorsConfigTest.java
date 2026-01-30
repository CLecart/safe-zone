package com.safezone.gateway.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.web.cors.CorsConfiguration;

class CorsConfigTest {

    @Test
    void defaultAllowedOrigins_shouldContainLocalhost() {
        CorsConfig cfg = new CorsConfig();
        CorsConfiguration cors =
                cfg.buildCorsConfiguration("http://localhost,http://127.0.0.1", false);

        assertThat(cors.getAllowedOrigins())
                .containsExactly("http://localhost", "http://127.0.0.1");
    }

    @Test
    void explicitWildcard_shouldUseAllowedOriginPatterns() {
        CorsConfig cfg = new CorsConfig();
        CorsConfiguration cors = cfg.buildCorsConfiguration("*", true);

        assertThat(cors.getAllowedOriginPatterns()).containsExactly("*");
    }

    @Test
    void wildcard_without_allowWildcard_shouldFail() {
        CorsConfig cfg = new CorsConfig();
        org.junit.jupiter.api.Assertions.assertThrows(
                IllegalStateException.class, () -> cfg.buildCorsConfiguration("*", false));
    }
}
