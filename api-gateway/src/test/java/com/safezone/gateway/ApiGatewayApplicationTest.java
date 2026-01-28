package com.safezone.gateway;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

/**
 * Integration tests for the API Gateway Application.
 * <p>
 * Verifies that the Spring application context loads correctly,
 * routes are properly configured, and filters are working.
 * </p>
 *
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2024-01-06
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("API Gateway Application Tests")
class ApiGatewayApplicationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private RouteLocator routeLocator;

    @Test
    @DisplayName("Should load application context successfully")
    void shouldLoadContext() {
        assertThat(applicationContext).isNotNull();
    }

    @Test
    @DisplayName("Should configure routes")
    void shouldConfigureRoutes() {
        assertThat(routeLocator).isNotNull();
        assertThat(routeLocator.getRoutes().collectList().block()).isNotEmpty();
    }

    @ParameterizedTest(name = "Should have {0} service route")
    @ValueSource(strings = { "product", "order", "user" })
    @DisplayName("Should have service routes configured")
    void shouldHaveServiceRoute(String serviceName) {
        var routes = routeLocator.getRoutes().collectList().block();
        assertThat(routes).isNotNull();
        assertThat(routes.stream().anyMatch(r -> r.getId().contains(serviceName)))
                .as(serviceName + " service route should exist")
                .isTrue();
    }
}
