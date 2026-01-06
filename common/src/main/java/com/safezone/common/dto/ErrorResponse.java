package com.safezone.common.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Structured error response for REST API exceptions.
 * Provides detailed error information including HTTP status, message, and field-level validation errors.
 *
 * @param status      HTTP status code
 * @param error       error type (e.g., "Bad Request", "Not Found")
 * @param message     detailed error message
 * @param path        the request path that caused the error
 * @param timestamp   when the error occurred
 * @param fieldErrors list of field-specific validation errors
 *
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2026-01-06
 */
public record ErrorResponse(
        int status,
        String error,
        String message,
        String path,
        LocalDateTime timestamp,
        List<FieldError> fieldErrors
) {
    /**
     * Represents a field-level validation error.
     *
     * @param field   the field name that failed validation
     * @param message the validation error message
     */
    public record FieldError(String field, String message) {}

    /**
     * Creates an error response without field errors.
     *
     * @param status  HTTP status code
     * @param error   error type
     * @param message error message
     * @param path    request path
     * @return a new ErrorResponse
     */
    public static ErrorResponse of(int status, String error, String message, String path) {
        return new ErrorResponse(status, error, message, path, LocalDateTime.now(), List.of());
    }

    /**
     * Creates an error response with field-level validation errors.
     *
     * @param status      HTTP status code
     * @param error       error type
     * @param message     error message
     * @param path        request path
     * @param fieldErrors list of field validation errors
     * @return a new ErrorResponse with field errors
     */
    public static ErrorResponse withFieldErrors(int status, String error, String message, String path, List<FieldError> fieldErrors) {
        return new ErrorResponse(status, error, message, path, LocalDateTime.now(), fieldErrors);
    }
}
