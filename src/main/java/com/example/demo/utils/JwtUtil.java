package com.example.demo.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@Component
public class JwtUtil {

    private final Key key;
    private final long expirationTime;
    private static final long DEFAULT_EXPIRATION_TIME = 1000L * 60 * 60; // 1 hour

    @Autowired
    public JwtUtil(@Value("${jwt.secret}") String secret) {
        this(secret, DEFAULT_EXPIRATION_TIME);
    }

    public JwtUtil(String secret, long expirationTime) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.expirationTime = expirationTime;
    }

    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            log.info("Token validation started: " + token);
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.info("Token expired: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT: " + e.getMessage());
        } catch (MalformedJwtException e) {
            log.info("Malformed JWT: " + e.getMessage());
        } catch (SignatureException e) {
            log.info("Invalid signature: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            log.info("Illegal argument: " + e.getMessage());
        }
        return false;
    }

}