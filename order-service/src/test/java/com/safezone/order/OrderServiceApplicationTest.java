package com.safezone.order;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.RestTemplate;

/**
 * Integration tests for the Order Service Application.
 * <p>
 * Verifies that the Spring application context loads correctly
 * and all required beans are properly configured.
 * </p>
 *
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2024-01-06
 */
@SpringBootTest
@DisplayName("Order Service Application Tests")
class OrderServiceApplicationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @MockitoBean
    private RestTemplate restTemplate;

    @Test
    @DisplayName("Should load application context successfully")
    void shouldLoadContext() {
        assertThat(applicationContext).isNotNull();
    }

    @Test
    @DisplayName("Should have order service bean")
    void shouldHaveOrderServiceBean() {
        assertThat(applicationContext.containsBean("orderServiceImpl")).isTrue();
    }
}
