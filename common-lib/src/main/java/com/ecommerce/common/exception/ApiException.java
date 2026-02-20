package com.ecommerce.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Base runtime exception that carries an HTTP status code and message.
 */
public class ApiException extends RuntimeException {
    private final HttpStatus status;

    public ApiException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
