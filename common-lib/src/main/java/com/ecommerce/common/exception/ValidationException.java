package com.ecommerce.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Used when validation fails on input.  Carries details in message.
 */
public class ValidationException extends ApiException {
    public ValidationException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
