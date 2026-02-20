package com.ecommerce.common.dto;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

class ApiErrorTest {
    @Test
    void of_shouldCreateErrorWithCorrectFields() {
        String message = "Something went wrong";
        String path = "/test";
        ApiError error = ApiError.of(HttpStatus.BAD_REQUEST, message, path);

        assertNotNull(error.getTimestamp());
        assertEquals(HttpStatus.BAD_REQUEST.value(), error.getStatus());
        assertEquals(HttpStatus.BAD_REQUEST.getReasonPhrase(), error.getError());
        assertEquals(message, error.getMessage());
        assertEquals(path, error.getPath());
    }
}
