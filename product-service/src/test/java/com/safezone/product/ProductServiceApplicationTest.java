package com.safezone.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisplayName("Product Service Application Tests")
class ProductServiceApplicationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    @DisplayName("Should load application context successfully")
    void shouldLoadContext() {
        assertThat(applicationContext).isNotNull();
    }

    @Test
    @DisplayName("Should have product service bean")
    void shouldHaveProductServiceBean() {
        assertThat(applicationContext.containsBean("productServiceImpl")).isTrue();
    }
}
