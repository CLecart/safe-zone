package com.safezone.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the User microservice.
 * <p>
 * This service handles user authentication, registration, profile management,
 * and role-based access control. It provides JWT-based authentication
 * for the entire e-commerce platform.
 * </p>
 *
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2024-01-06
 */
@SpringBootApplication(scanBasePackages = {"com.safezone.user", "com.safezone.common"})
public class UserServiceApplication {

    /**
     * Application entry point.
     * <p>
     * Bootstraps the Spring Boot application and starts the embedded server.
     * </p>
     *
     * @param args command line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
