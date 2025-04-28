package com.example.demo.exceptions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handleResourceNotFoundException() {
        // Arrange
        ResourceNotFoundException ex = new ResourceNotFoundException("Resource not found");

        // Act
        ResponseEntity<ErrorResponse> response = handler.handleResourceNotFoundException(ex);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Resource Not Found", response.getBody().getError());
        assertEquals("Resource not found", response.getBody().getMessage());
    }

    @Test
    void handleBadCredentialsException() {
        // Arrange
        BadCredentialsException ex = new BadCredentialsException("Invalid credentials");

        // Act
        ResponseEntity<ErrorResponse> response = handler.handleBadCredentialsException(ex);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Authentication Failed", response.getBody().getError());
        assertEquals("Invalid username or password", response.getBody().getMessage());
    }

    @Test
    void handleValidationExceptions() {
        // Arrange
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);
        
        FieldError fieldError = new FieldError("object", "field", "defaultMessage");
        when(bindingResult.getAllErrors()).thenReturn(java.util.Collections.singletonList(fieldError));

        // Act
        ResponseEntity<Map<String, String>> response = handler.handleValidationExceptions(ex);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("defaultMessage", response.getBody().get("field"));
    }

    @Test
    void handleGlobalException() {
        // Arrange
        Exception ex = new Exception("Unexpected error");

        // Act
        ResponseEntity<ErrorResponse> response = handler.handleGlobalException(ex);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Internal Server Error", response.getBody().getError());
        assertEquals("An unexpected error occurred", response.getBody().getMessage());
    }
}