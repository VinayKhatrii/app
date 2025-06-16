package com.book_library.app.core.configs;

import com.book_library.app.core.controller.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.LazyInitializationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResponse<Void> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        log.warn("Authentication error: {}", ex.getMessage());
        return new ApiResponse<>(false, "Authentication failed: " + ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("Illegal argument: {}", ex.getMessage());
        return new ApiResponse<>(false, ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiResponse<Void> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        log.error("Data integrity violation: {}", ex.getMessage(), ex);

        String errorMessage = "A data conflict occurred. Please ensure the submitted data is unique.";
        if (ex.getCause() != null && ex.getCause().getMessage() != null) {
            String detailedMessage = ex.getCause().getMessage();
            if (detailedMessage.contains("Key") && detailedMessage.contains("already exists")) {
                int detailStart = detailedMessage.indexOf("Key");
                int detailEnd = detailedMessage.indexOf("already exists") + "already exists".length();
                if (detailStart != -1 && detailEnd != -1) {
                    errorMessage = detailedMessage.substring(detailStart, detailEnd).replace("Key", "Field");
                }
            }
        }

        return new ApiResponse<>(false, errorMessage.trim());
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ApiResponse<Void> handleAccessDenied(org.springframework.security.access.AccessDeniedException ex) {
        log.warn("Access denied: {}", ex.getMessage());
        return new ApiResponse<>(false, ex.getMessage());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ApiResponse<Void> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        log.warn("Unsupported HTTP method: {}", ex.getMessage());
        return new ApiResponse<>(false, "Request method not supported. Supported methods: " + ex.getSupportedHttpMethods());
    }

    @ExceptionHandler(LazyInitializationException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleLazyInitializationException(LazyInitializationException ex) {
        log.error("Lazy initialization error: {}", ex.getMessage(), ex);
        return new ApiResponse<>(false, "Unexpected lazy loading error. Please try again.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception ex, WebRequest request) {
        log.error("Unhandled exception: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(
                new ApiResponse<>(false, "An unexpected error occurred. Please contact support."),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
