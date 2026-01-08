package com.safezone.common.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link ApiResponse}.
 *
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2026-01-08
 */
class ApiResponseTest {

    @Test
    @DisplayName("Should create success response with default message")
    void shouldCreateSuccessResponseWithDefaultMessage() {
        String testData = "test-data";

        ApiResponse<String> response = ApiResponse.success(testData);

        assertThat(response.success()).isTrue();
        assertThat(response.message()).isEqualTo("Operation successful");
        assertThat(response.data()).isEqualTo(testData);
        assertThat(response.timestamp()).isNotNull();
        assertThat(response.timestamp()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should create success response with custom message")
    void shouldCreateSuccessResponseWithCustomMessage() {
        String customMessage = "Custom success";
        Integer testData = 42;

        ApiResponse<Integer> response = ApiResponse.success(customMessage, testData);

        assertThat(response.success()).isTrue();
        assertThat(response.message()).isEqualTo(customMessage);
        assertThat(response.data()).isEqualTo(testData);
        assertThat(response.timestamp()).isNotNull();
    }

    @Test
    @DisplayName("Should create error response")
    void shouldCreateErrorResponse() {
        String errorMessage = "Operation failed";

        ApiResponse<Object> response = ApiResponse.error(errorMessage);

        assertThat(response.success()).isFalse();
        assertThat(response.message()).isEqualTo(errorMessage);
        assertThat(response.data()).isNull();
        assertThat(response.timestamp()).isNotNull();
    }

    @Test
    @DisplayName("Should create response with record constructor")
    void shouldCreateResponseWithConstructor() {
        LocalDateTime now = LocalDateTime.now();
        String data = "data";

        ApiResponse<String> response = new ApiResponse<>(true, "msg", data, now);

        assertThat(response.success()).isTrue();
        assertThat(response.message()).isEqualTo("msg");
        assertThat(response.data()).isEqualTo(data);
        assertThat(response.timestamp()).isEqualTo(now);
    }
}
