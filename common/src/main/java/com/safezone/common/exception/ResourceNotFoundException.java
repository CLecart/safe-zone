package com.safezone.common.exception;

/**
 * Exception thrown when a requested resource cannot be found.
 * Typically results in an HTTP 404 response.
 *
 * <p>
 * Provides detailed information about which resource was not found
 * and the criteria used for the search.
 * </p>
 *
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2026-01-06
 */
public class ResourceNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String resourceName;
    private final String fieldName;
    private final transient Object fieldValue;

    /**
     * Constructs a ResourceNotFoundException with resource details.
     *
     * @param resourceName the type of resource (e.g., "Product", "User")
     * @param fieldName    the field used for lookup (e.g., "id", "sku")
     * @param fieldValue   the value that was searched for
     */
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s: '%s'", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    /**
     * Returns the name of the resource type.
     *
     * @return the resource name
     */
    public String getResourceName() {
        return resourceName;
    }

    /**
     * Returns the field name used in the lookup.
     *
     * @return the field name
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * Returns the value that was searched for.
     *
     * @return the field value
     */
    public Object getFieldValue() {
        return fieldValue;
    }
}
