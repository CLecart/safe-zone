package com.safezone.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for OpenAPI/Swagger documentation.
 * Provides centralized API documentation with JWT authentication support.
 *
 * <p>This configuration enables Swagger UI at /swagger-ui.html with
 * automatic Bearer token authentication for protected endpoints.</p>
 *
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2026-01-06
 */
@Configuration
public class OpenApiConfig {

    /** The application name used in API documentation title. */
    @Value("${spring.application.name:SafeZone Service}")
    private String applicationName;

    /**
     * Creates a customized OpenAPI specification bean.
     * Configures JWT Bearer authentication and API metadata.
     *
     * @return the configured OpenAPI instance
     */
    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")))
                .info(new Info()
                        .title(applicationName + " API")
                        .description("REST API documentation for " + applicationName)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("SafeZone Team")
                                .email("contact@safezone.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
}
