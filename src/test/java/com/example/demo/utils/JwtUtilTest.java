package com.example.demo.utils;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private static final String SECRET = "VGhpcyBpcyBhIHZhbGlkIHNlY3JldCBrZXk=";

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(SECRET);
    }

    @Test
    void testGenerateToken() {
        String email = "test@example.com";
        String token = jwtUtil.generateToken(email);
        
        assertNotNull(token);
        assertTrue(token.length() > 0);
        assertTrue(jwtUtil.validateToken(token));
        assertEquals(email, jwtUtil.extractEmail(token));
    }

    @Test
    void testExtractEmail() {
        String email = "test@example.com";
        String token = jwtUtil.generateToken(email);
        
        String extractedEmail = jwtUtil.extractEmail(token);
        assertEquals(email, extractedEmail);
    }

    @Test
    void testValidateToken_ValidToken() {
        String token = jwtUtil.generateToken("test@example.com");
        assertTrue(jwtUtil.validateToken(token));
    }

    @Test
    void testValidateToken_InvalidToken() {
        assertFalse(jwtUtil.validateToken("invalid.token.here"));
    }

    @Test
    void testValidateToken_ExpiredToken() throws Exception {
        // Create a JwtUtil with very short expiration
        JwtUtil shortJwtUtil = new JwtUtil(SECRET);
        ReflectionTestUtils.setField(shortJwtUtil, "EXPIRATION_TIME", 1); // 1ms expiration
        
        String token = shortJwtUtil.generateToken("test@example.com");
        Thread.sleep(2); // Wait for token to expire
        
        assertFalse(shortJwtUtil.validateToken(token));
    }
    
    @Test
    void testValidateToken_NullToken() {
        assertFalse(jwtUtil.validateToken(null));
    }
}