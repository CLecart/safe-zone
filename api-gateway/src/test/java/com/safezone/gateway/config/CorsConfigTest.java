package com.safezone.gateway.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;

import org.junit.jupiter.api.Test;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

class CorsConfigTest {

    @Test
    void defaultAllowedOrigins_shouldContainLocalhost() {
        CorsConfig cfg = new CorsConfig();
        CorsConfiguration cors = cfg.buildCorsConfiguration("http://localhost,http://127.0.0.1");

        assertThat(cors.getAllowedOrigins()).containsExactly("http://localhost", "http://127.0.0.1");
    }

    @Test
    void explicitWildcard_shouldUseAllowedOriginPatterns() {
        CorsConfig cfg = new CorsConfig();
        CorsConfiguration cors = cfg.buildCorsConfiguration("*");

        assertThat(cors.getAllowedOriginPatterns()).containsExactly("*");
    }

    private UrlBasedCorsConfigurationSource extractSource(CorsWebFilter filter) {
        // CorsWebFilter stores the UrlBasedCorsConfigurationSource in a private field -
        // use reflection. Any reflection-related checked exceptions are wrapped into
        // IllegalStateException to keep the test signature simple (no checked
        // exceptions declared).
        Field[] fields = CorsWebFilter.class.getDeclaredFields();
        for (Field f : fields) {
            if (UrlBasedCorsConfigurationSource.class.isAssignableFrom(f.getType())) {
                try {
                    f.setAccessible(true);
                    return (UrlBasedCorsConfigurationSource) f.get(filter);
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException("Failed to access UrlBasedCorsConfigurationSource on CorsWebFilter",
                            e);
                }
            }
        }
        throw new IllegalStateException("UrlBasedCorsConfigurationSource field not found on CorsWebFilter");
    }
}
