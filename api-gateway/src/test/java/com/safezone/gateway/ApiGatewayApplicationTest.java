package com.safezone.gateway;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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

    @Test
    @DisplayName("Should have product service route")
    void shouldHaveProductServiceRoute() {
        var routes = routeLocator.getRoutes().collectList().block();
        assertThat(routes).isNotNull();
        assertThat(routes.stream().anyMatch(r -> r.getId().contains("product")))
                .as("Product service route should exist")
                .isTrue();
    }

    @Test
    @DisplayName("Should have order service route")
    void shouldHaveOrderServiceRoute() {
        var routes = routeLocator.getRoutes().collectList().block();
        assertThat(routes).isNotNull();
        assertThat(routes.stream().anyMatch(r -> r.getId().contains("order")))
                .as("Order service route should exist")
                .isTrue();
    }

    @Test
    @DisplayName("Should have user service route")
    void shouldHaveUserServiceRoute() {
        var routes = routeLocator.getRoutes().collectList().block();
        assertThat(routes).isNotNull();
        assertThat(routes.stream().anyMatch(r -> r.getId().contains("user")))
                .as("User service route should exist")
                .isTrue();
    }
}
