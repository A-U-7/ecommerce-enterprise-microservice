package com.ecommerce.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when a requested entity cannot be found.
 */
public class ResourceNotFoundException extends ApiException {
    public ResourceNotFoundException(String resourceName, Object id) {
        super(HttpStatus.NOT_FOUND, String.format("%s with identifier '%s' not found", resourceName, id));
    }
}
