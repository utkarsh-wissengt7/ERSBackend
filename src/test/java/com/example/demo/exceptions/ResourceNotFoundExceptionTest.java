package com.example.demo.exceptions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ResourceNotFoundExceptionTest {

    @Test
    void testConstructorWithMessage() {
        String message = "Resource not found";
        ResourceNotFoundException exception = new ResourceNotFoundException(message);
        
        assertEquals(message, exception.getMessage());
    }

    @Test
    void testConstructorWithMessageAndCause() {
        String message = "Resource not found";
        Throwable cause = new RuntimeException("Original error");
        ResourceNotFoundException exception = new ResourceNotFoundException(message, cause);
        
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testInheritance() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Test");
        
        assertTrue(exception instanceof RuntimeException);
    }
}