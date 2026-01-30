package com.safezone.common.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

class CommonSecurityConfigTest {

    @Test
    void corsConfigurationSource_shouldProvideExpectedDefaults() {
        CommonSecurityConfig cfg;
        try {
            var ctor = CommonSecurityConfig.class.getDeclaredConstructor();
            ctor.setAccessible(true);
            cfg = ctor.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        CorsConfigurationSource source = cfg.corsConfigurationSource();
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setRequestURI("/api/test");

        CorsConfiguration cors =
                java.util.Objects.requireNonNull(
                        source.getCorsConfiguration(req),
                        "CorsConfigurationSource returned null for request: "
                                + req.getRequestURI());
        assertThat(cors).isNotNull();
        assertThat(cors.getAllowedOrigins())
                .contains("http://localhost:3000", "http://127.0.0.1:3000");
        assertThat(cors.getAllowedMethods())
                .contains("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS");
        assertThat(cors.getAllowedHeaders()).contains("*");
        assertThat(cors.getExposedHeaders()).contains("Authorization", "Content-Type");
        assertThat(cors.getAllowCredentials()).isFalse();
    }
}
