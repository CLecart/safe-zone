package com.safezone.common.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.safezone.common.dto.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Unit tests for {@link GlobalExceptionHandler}.
 * Verifies proper handling of various exception types and HTTP response
 * formatting.
 *
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2026-01-06
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Global Exception Handler Tests")
class GlobalExceptionHandlerTest {

    /** The exception handler under test. */
    private GlobalExceptionHandler exceptionHandler;

    /** Mock HTTP servlet request for testing. */
    @Mock
    private HttpServletRequest httpServletRequest;

    /** Mock validation exception for testing. */
    @Mock
    private MethodArgumentNotValidException methodArgumentNotValidException;

    /** Mock binding result for validation tests. */
    @Mock
    private BindingResult bindingResult;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        given(httpServletRequest.getRequestURI()).willReturn("/api/test");
    }

    @Test
    @DisplayName("Should handle ResourceNotFoundException")
    void shouldHandleResourceNotFoundException() {
        ResourceNotFoundException exception = new ResourceNotFoundException("User", "id", 1L);

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleResourceNotFoundException(exception,
                httpServletRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(Objects.requireNonNull(response.getBody()).status()).isEqualTo(404);
        assertThat(Objects.requireNonNull(response.getBody()).error()).isEqualTo("Not Found");
    }

    @Test
    @DisplayName("Should handle BusinessException")
    void shouldHandleBusinessException() {
        BusinessException exception = new BusinessException("Invalid operation");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleBusinessException(exception,
                httpServletRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(Objects.requireNonNull(response.getBody()).status()).isEqualTo(400);
        assertThat(Objects.requireNonNull(response.getBody()).message()).isEqualTo("Invalid operation");
    }

    @Test
    @DisplayName("Should handle validation exceptions")
    void shouldHandleValidationExceptions() {
        FieldError fieldError = new FieldError("request", "email", "Email is required");
        given(methodArgumentNotValidException.getBindingResult()).willReturn(bindingResult);
        given(bindingResult.getFieldErrors()).willReturn(List.of(fieldError));

        ResponseEntity<ErrorResponse> response = exceptionHandler
                .handleValidationException(methodArgumentNotValidException, httpServletRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(Objects.requireNonNull(response.getBody()).fieldErrors()).hasSize(1);
        assertThat(Objects.requireNonNull(response.getBody()).fieldErrors().get(0).field()).isEqualTo("email");
    }

    @Test
    @DisplayName("Should handle generic exceptions")
    void shouldHandleGenericExceptions() {
        Exception exception = new Exception("Unexpected error");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGenericException(exception, httpServletRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(Objects.requireNonNull(response.getBody()).status()).isEqualTo(500);
    }
}
