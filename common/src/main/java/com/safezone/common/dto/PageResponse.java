package com.safezone.common.dto;

import java.util.List;

/**
 * Generic paginated response wrapper for list endpoints.
 * Provides pagination metadata alongside the content.
 *
 * @param <T>           the type of elements in the page
 * @param content       the list of elements in the current page
 * @param page          current page number (zero-based)
 * @param size          number of elements per page
 * @param totalElements total number of elements across all pages
 * @param totalPages    total number of pages
 * @param first         true if this is the first page
 * @param last          true if this is the last page
 *
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2026-01-06
 */
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last
) {
    /**
     * Creates a PageResponse from content and pagination parameters.
     * Automatically calculates totalPages, first, and last flags.
     *
     * @param content       the page content
     * @param page          current page number (zero-based)
     * @param size          page size
     * @param totalElements total count of all elements
     * @param <T>           the type of elements
     * @return a fully populated PageResponse
     */
    public static <T> PageResponse<T> of(List<T> content, int page, int size, long totalElements) {
        int totalPages = (int) Math.ceil((double) totalElements / size);
        return new PageResponse<>(
                content,
                page,
                size,
                totalElements,
                totalPages,
                page == 0,
                page >= totalPages - 1
        );
    }
}
