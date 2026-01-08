package com.safezone.common.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link ResourceNotFoundException}.
 *
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2026-01-08
 */
class ResourceNotFoundExceptionTest {

    @Test
    @DisplayName("Should create exception with resource details")
    void shouldCreateExceptionWithResourceDetails() {
        String resourceName = "Product";
        String fieldName = "id";
        Long fieldValue = 123L;

        ResourceNotFoundException exception = new ResourceNotFoundException(
                resourceName, fieldName, fieldValue);

        assertThat(exception.getMessage()).isEqualTo("Product not found with id: '123'");
        assertThat(exception.getResourceName()).isEqualTo(resourceName);
        assertThat(exception.getFieldName()).isEqualTo(fieldName);
        assertThat(exception.getFieldValue()).isEqualTo(fieldValue);
    }

    @Test
    @DisplayName("Should handle string field values")
    void shouldHandleStringFieldValues() {
        ResourceNotFoundException exception = new ResourceNotFoundException(
                "User", "username", "john.doe");

        assertThat(exception.getMessage()).isEqualTo("User not found with username: 'john.doe'");
        assertThat(exception.getFieldValue()).isEqualTo("john.doe");
    }
}
