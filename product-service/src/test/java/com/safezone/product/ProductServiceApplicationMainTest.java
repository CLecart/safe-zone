package com.safezone.product;

import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ProductServiceApplication main() Test")
class ProductServiceApplicationMainTest {

    @Test
    @DisplayName("Invoke main method")
    void invokeMain() {
        assertThatCode(() -> ProductServiceApplication.main(new String[] {}))
                .doesNotThrowAnyException();
    }
}
