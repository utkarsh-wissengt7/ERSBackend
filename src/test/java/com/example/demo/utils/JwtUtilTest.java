package com.example.demo.utils;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.util.Date;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtUtilTest {

    private JwtUtil jwtUtil;
    private static final String SECRET = "thisIsAVeryLongSecretKeyForTestingPurposesOnly12345";
    private static final String TEST_EMAIL = "test@example.com";

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(SECRET);
    }

    @Test
    void testGenerateToken() {
        // Act
        String token = jwtUtil.generateToken(TEST_EMAIL);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(jwtUtil.validateToken(token));
        assertEquals(TEST_EMAIL, jwtUtil.extractEmail(token));
    }

    @Test
    void testGenerateTokenWithCustomExpiration() {
        // Arrange
        long pastTime = System.currentTimeMillis() - 2000; // 2 seconds in the past
        JwtUtil shortExpirationJwtUtil = new JwtUtil(SECRET, 1000L); // 1 second expiration
        
        // Create a token that was issued in the past
        String token = Jwts.builder()
                .setSubject(TEST_EMAIL)
                .setIssuedAt(new Date(pastTime))
                .setExpiration(new Date(pastTime + 1000)) // Set expiration 1 second after past time
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes()), SignatureAlgorithm.HS256)
                .compact();

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertFalse(shortExpirationJwtUtil.validateToken(token));
    }

    @Test
    void testValidateToken_ExpiredToken() {
        // Arrange
        JwtUtil shortExpirationJwtUtil = new JwtUtil(SECRET, 0); // Immediate expiration
        String token = shortExpirationJwtUtil.generateToken(TEST_EMAIL);

        // Act & Assert
        assertFalse(shortExpirationJwtUtil.validateToken(token));
    }

    @Test
    void testValidateToken_MalformedToken() {
        // Act & Assert
        assertFalse(jwtUtil.validateToken("malformed.jwt.token"));
    }

    @Test
    void testValidateToken_UnsupportedToken() {
        // Act & Assert
        assertFalse(jwtUtil.validateToken("eyJhbGciOiJub25lIn0.eyJzdWIiOiJ0ZXN0QGV4YW1wbGUuY29tIn0."));
    }

    @Test
    void testValidateToken_InvalidSignature() {
        // Arrange
        String differentSecret = "anotherVeryLongSecretKeyForTestingPurposesOnly98765432100";
        String tokenWithDifferentSecret = new JwtUtil(differentSecret).generateToken(TEST_EMAIL);

        // Act & Assert
        assertFalse(jwtUtil.validateToken(tokenWithDifferentSecret));
    }

    @Test
    void testValidateToken_NullToken() {
        // Act & Assert
        assertFalse(jwtUtil.validateToken(null));
    }

    @Test
    void testExtractEmail() {
        // Arrange
        String token = jwtUtil.generateToken(TEST_EMAIL);

        // Act
        String extractedEmail = jwtUtil.extractEmail(token);

        // Assert
        assertEquals(TEST_EMAIL, extractedEmail);
    }
}