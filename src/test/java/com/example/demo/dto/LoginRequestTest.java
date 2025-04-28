package com.example.demo.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidLoginRequest() {
        LoginRequest request = new LoginRequest("test@example.com", "password123");
        
        assertTrue(validator.validate(request).isEmpty());
        assertEquals("test@example.com", request.getEmail());
        assertEquals("password123", request.getPassword());
    }

    @Test
    void testLoginRequestWithEmptyEmail() {
        LoginRequest request = new LoginRequest("", "password123");
        
        assertFalse(validator.validate(request).isEmpty());
    }

    @Test
    void testLoginRequestWithInvalidEmail() {
        LoginRequest request = new LoginRequest("invalid-email", "password123");
        
        assertFalse(validator.validate(request).isEmpty());
    }

    @Test
    void testLoginRequestWithNullEmail() {
        LoginRequest request = new LoginRequest(null, "password123");
        
        assertFalse(validator.validate(request).isEmpty());
    }

    @Test
    void testLoginRequestWithEmptyPassword() {
        LoginRequest request = new LoginRequest("test@example.com", "");
        
        assertFalse(validator.validate(request).isEmpty());
    }

    @Test
    void testLoginRequestWithNullPassword() {
        LoginRequest request = new LoginRequest("test@example.com", null);
        
        assertFalse(validator.validate(request).isEmpty());
    }

    @Test
    void testLoginRequestConstructor() {
        LoginRequest request = new LoginRequest();
        
        assertNotNull(request);
        assertNull(request.getEmail());
        assertNull(request.getPassword());
    }

    @Test
    void testLoginRequestSettersAndGetters() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        
        assertEquals("test@example.com", request.getEmail());
        assertEquals("password123", request.getPassword());
    }

    
}