package com.safezone.order.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for application settings.
 * Maps properties under "application" prefix from application.yml.
 *
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2026-01-09
 */
@Component
@ConfigurationProperties(prefix = "application")
public class ApplicationProperties {

    private final Jwt jwt = new Jwt();

    public Jwt getJwt() {
        return jwt;
    }

    public static class Jwt {
        private String secret;

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }
    }
}
