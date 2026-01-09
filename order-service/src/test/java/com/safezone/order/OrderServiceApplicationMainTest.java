package com.safezone.order;

import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Main method test for {@link OrderServiceApplication}.
 * Verifies that the application can start without errors.
 *
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2026-01-09
 */
@DisplayName("OrderServiceApplication Main Tests")
class OrderServiceApplicationMainTest {

    @Test
    @DisplayName("Main method executes without exception")
    void mainMethodExecutesSuccessfully() {
        assertThatCode(() -> OrderServiceApplication.main(new String[] {}))
                .doesNotThrowAnyException();
    }
}
