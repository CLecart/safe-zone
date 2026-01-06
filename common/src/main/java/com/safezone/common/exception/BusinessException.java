package com.safezone.common.exception;

/**
 * Exception thrown when a business rule violation occurs.
 * Used for domain-specific errors that should be communicated to the client.
 *
 * <p>Examples include: duplicate entries, invalid state transitions,
 * or business constraint violations.</p>
 *
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2026-01-06
 */
public class BusinessException extends RuntimeException {

    private final String errorCode;

    /**
     * Constructs a BusinessException with default error code.
     *
     * @param message the detail message
     */
    public BusinessException(String message) {
        super(message);
        this.errorCode = "BUSINESS_ERROR";
    }

    /**
     * Constructs a BusinessException with custom error code.
     *
     * @param errorCode the error code for categorization
     * @param message   the detail message
     */
    public BusinessException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * Returns the error code associated with this exception.
     *
     * @return the error code
     */
    public String getErrorCode() {
        return errorCode;
    }
}
