package com.safezone.common.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link BusinessException}.
 *
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2026-01-08
 */
class BusinessExceptionTest {

    @Test
    @DisplayName("Should create exception with default error code")
    void shouldCreateExceptionWithDefaultErrorCode() {
        String message = "Business rule violated";

        BusinessException exception = new BusinessException(message);

        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getErrorCode()).isEqualTo("BUSINESS_ERROR");
    }

    @Test
    @DisplayName("Should create exception with custom error code")
    void shouldCreateExceptionWithCustomErrorCode() {
        String errorCode = "DUPLICATE_ENTRY";
        String message = "Product SKU already exists";

        BusinessException exception = new BusinessException(errorCode, message);

        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getErrorCode()).isEqualTo(errorCode);
    }
}
