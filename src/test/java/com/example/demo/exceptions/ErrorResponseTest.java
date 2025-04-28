package com.example.demo.exceptions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseTest {

    @Test
    void testErrorResponseConstructor() {
        ErrorResponse response = new ErrorResponse(404, "Not Found", "Resource not found");
        
        assertEquals(404, response.getStatus());
        assertEquals("Not Found", response.getError());
        assertEquals("Resource not found", response.getMessage());
    }

    @Test
    void testErrorResponseSettersAndGetters() {
        ErrorResponse response = new ErrorResponse();
        response.setStatus(500);
        response.setError("Internal Server Error");
        response.setMessage("An unexpected error occurred");
        
        assertEquals(500, response.getStatus());
        assertEquals("Internal Server Error", response.getError());
        assertEquals("An unexpected error occurred", response.getMessage());
    }

    @Test
    void testErrorResponseEqualsAndHashCode() {
        ErrorResponse response1 = new ErrorResponse(404, "Not Found", "Resource not found");
        ErrorResponse response2 = new ErrorResponse(404, "Not Found", "Resource not found");
        ErrorResponse response3 = new ErrorResponse(500, "Server Error", "Internal error");
        
        assertEquals(response1, response2);
        assertNotEquals(response1, response3);
        assertEquals(response1.hashCode(), response2.hashCode());
        assertNotEquals(response1.hashCode(), response3.hashCode());
    }

    @Test
    void testDefaultConstructor() {
        ErrorResponse response = new ErrorResponse();
        
        assertNotNull(response);
        assertEquals(0, response.getStatus());
        assertNull(response.getError());
        assertNull(response.getMessage());
    }
}