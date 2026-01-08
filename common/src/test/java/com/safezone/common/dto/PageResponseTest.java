package com.safezone.common.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link PageResponse}.
 *
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2026-01-08
 */
class PageResponseTest {

    @Test
    @DisplayName("Should create page response with of() factory method")
    void shouldCreatePageResponseWithFactoryMethod() {
        List<String> content = Arrays.asList("item1", "item2", "item3");
        int page = 0;
        int size = 3;
        long totalElements = 10;

        PageResponse<String> response = PageResponse.of(content, page, size, totalElements);

        assertThat(response.content()).isEqualTo(content);
        assertThat(response.page()).isEqualTo(page);
        assertThat(response.size()).isEqualTo(size);
        assertThat(response.totalElements()).isEqualTo(totalElements);
        assertThat(response.totalPages()).isEqualTo(4); // ceil(10/3)
        assertThat(response.first()).isTrue();
        assertThat(response.last()).isFalse();
    }

    @Test
    @DisplayName("Should identify first page correctly")
    void shouldIdentifyFirstPage() {
        PageResponse<Integer> response = PageResponse.of(
                Collections.singletonList(1), 0, 10, 100);

        assertThat(response.first()).isTrue();
        assertThat(response.last()).isFalse();
    }

    @Test
    @DisplayName("Should identify last page correctly")
    void shouldIdentifyLastPage() {
        PageResponse<Integer> response = PageResponse.of(
                Collections.singletonList(1), 9, 10, 100);

        assertThat(response.first()).isFalse();
        assertThat(response.last()).isTrue();
    }

    @Test
    @DisplayName("Should handle single page response")
    void shouldHandleSinglePageResponse() {
        List<String> content = Arrays.asList("only", "item");
        PageResponse<String> response = PageResponse.of(content, 0, 10, 2);

        assertThat(response.totalPages()).isEqualTo(1);
        assertThat(response.first()).isTrue();
        assertThat(response.last()).isTrue();
    }

    @Test
    @DisplayName("Should handle empty page response")
    void shouldHandleEmptyPageResponse() {
        PageResponse<String> response = PageResponse.of(Collections.emptyList(), 0, 10, 0);

        assertThat(response.content()).isEmpty();
        assertThat(response.totalPages()).isZero();
        assertThat(response.totalElements()).isZero();
    }

    @Test
    @DisplayName("Should create response with record constructor")
    void shouldCreateResponseWithConstructor() {
        List<Integer> content = Arrays.asList(1, 2, 3);

        PageResponse<Integer> response = new PageResponse<>(content, 1, 10, 25, 3, false, false);

        assertThat(response.content()).isEqualTo(content);
        assertThat(response.page()).isEqualTo(1);
        assertThat(response.size()).isEqualTo(10);
        assertThat(response.totalElements()).isEqualTo(25);
        assertThat(response.totalPages()).isEqualTo(3);
        assertThat(response.first()).isFalse();
        assertThat(response.last()).isFalse();
    }
}
