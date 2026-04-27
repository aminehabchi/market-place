package com.example.shared.common.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class ApiResponseTest {

    @Test
    void successFactoryBuildsSuccessfulResponse() {
        ApiResponse<String> response = ApiResponse.success("ok", 201);

        assertTrue(response.success());
        assertEquals("ok", response.data());
        assertEquals(201, response.statusCode());
    }

    @Test
    void errorFactoryBuildsErrorResponse() {
        ApiResponse<Void> response = ApiResponse.error("bad", HttpStatus.BAD_REQUEST);

        assertFalse(response.success());
        assertEquals("bad", response.message());
        assertEquals(400, response.statusCode());
    }
}
