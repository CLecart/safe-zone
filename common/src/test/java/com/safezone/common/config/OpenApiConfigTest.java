package com.safezone.common.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import io.swagger.v3.oas.models.OpenAPI;

/**
 * Unit tests for {@link OpenApiConfig}.
 *
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2026-01-08
 */
@SpringBootTest(classes = OpenApiConfig.class)
@TestPropertySource(properties = {
        "spring.application.name=Test Service"
})
class OpenApiConfigTest {

    @Autowired
    private OpenAPI openAPI;

    @Test
    @DisplayName("Should create OpenAPI bean with JWT security")
    void shouldCreateOpenApiBeanWithJwtSecurity() {
        assertThat(openAPI).isNotNull();
        assertThat(openAPI.getComponents()).isNotNull();
        assertThat(openAPI.getComponents().getSecuritySchemes()).containsKey("bearerAuth");
        assertThat(openAPI.getSecurity()).isNotEmpty();
    }

    @Test
    @DisplayName("Should configure API metadata correctly")
    void shouldConfigureApiMetadataCorrectly() {
        assertThat(openAPI.getInfo()).isNotNull();
        assertThat(openAPI.getInfo().getTitle()).contains("Test Service");
        assertThat(openAPI.getInfo().getVersion()).isEqualTo("1.0.0");
        assertThat(openAPI.getInfo().getContact()).isNotNull();
        assertThat(openAPI.getInfo().getContact().getName()).isEqualTo("SafeZone Team");
        assertThat(openAPI.getInfo().getLicense()).isNotNull();
        assertThat(openAPI.getInfo().getLicense().getName()).isEqualTo("MIT License");
    }

    @Test
    @DisplayName("Should configure Bearer JWT security scheme")
    void shouldConfigureBearerJwtSecurityScheme() {
        var securityScheme = openAPI.getComponents().getSecuritySchemes().get("bearerAuth");

        assertThat(securityScheme).isNotNull();
        assertThat(securityScheme.getType()).hasToString("http");
        assertThat(securityScheme.getScheme()).isEqualTo("bearer");
        assertThat(securityScheme.getBearerFormat()).isEqualTo("JWT");
    }
}
