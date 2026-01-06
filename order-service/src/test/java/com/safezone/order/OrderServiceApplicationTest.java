package com.safezone.order;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisplayName("Order Service Application Tests")
class OrderServiceApplicationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @MockBean
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
