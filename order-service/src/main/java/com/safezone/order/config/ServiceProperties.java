package com.safezone.order.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for external services.
 * Maps properties under "services" prefix from application.yml.
 *
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2026-01-09
 */
@Component
@ConfigurationProperties(prefix = "services")
public class ServiceProperties {

    private final Product product = new Product();
    private final User user = new User();

    public Product getProduct() {
        return product;
    }

    public User getUser() {
        return user;
    }

    public static class Product {
        private String url;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public static class User {
        private String url;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
