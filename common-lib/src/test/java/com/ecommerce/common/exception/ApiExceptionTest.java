package com.ecommerce.common.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

class ApiExceptionTest {
    @Test
    void apiException_shouldExposeStatusAndMessage() {
        ApiException ex = new ApiException(HttpStatus.FORBIDDEN, "nope");
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatus());
        assertEquals("nope", ex.getMessage());
    }

    @Test
    void resourceNotFound_shouldFormatMessage() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Product", 123);
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        assertTrue(ex.getMessage().contains("Product"));
        assertTrue(ex.getMessage().contains("123"));
    }

    @Test
    void validationException_shouldBeBadRequest() {
        ValidationException ex = new ValidationException("bad input");
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }
}
