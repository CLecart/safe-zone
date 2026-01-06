package com.safezone.common.dto;

import java.time.LocalDateTime;

/**
 * Generic API response wrapper for consistent REST API responses.
 * Provides a standardized structure for both success and error responses.
 *
 * @param <T> the type of the response data payload
 * @param success    indicates if the operation was successful
 * @param message    human-readable message describing the result
 * @param data       the response payload (null for errors)
 * @param timestamp  when the response was generated
 *
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2026-01-06
 */
public record ApiResponse<T>(
        boolean success,
        String message,
        T data,
        LocalDateTime timestamp
) {
    /**
     * Creates a success response with default message.
     *
     * @param data the response data
     * @param <T>  the type of the data
     * @return a success ApiResponse with the provided data
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "Operation successful", data, LocalDateTime.now());
    }

    /**
     * Creates a success response with custom message.
     *
     * @param message custom success message
     * @param data    the response data
     * @param <T>     the type of the data
     * @return a success ApiResponse with the provided message and data
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data, LocalDateTime.now());
    }

    /**
     * Creates an error response with no data.
     *
     * @param message error description message
     * @param <T>     the type of the data (null for errors)
     * @return an error ApiResponse with the provided message
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null, LocalDateTime.now());
    }
}
