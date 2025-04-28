package com.example.demo.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.junit.jupiter.api.Assertions.*;

class CorsConfigTest {

    private final CorsConfig corsConfig = new CorsConfig();

    @Test
    void testCorsConfigurer() {
        // Act
        WebMvcConfigurer configurer = corsConfig.corsConfigurer();
        
        // Create a test registry to verify configuration
        TestCorsRegistry registry = new TestCorsRegistry();
        configurer.addCorsMappings(registry);

        // Assert
        assertEquals("/api/**", registry.getPathPattern());
        assertEquals("http://localhost:5173", registry.getAllowedOrigins().get(0));
        assertTrue(registry.getAllowedMethods().contains("GET"));
        assertTrue(registry.getAllowedMethods().contains("POST"));
        assertTrue(registry.getAllowedMethods().contains("PUT"));
        assertTrue(registry.getAllowedMethods().contains("DELETE"));
        assertTrue(registry.getAllowedMethods().contains("OPTIONS"));
        assertTrue(registry.isAllowCredentials());
    }

    // Test helper class to capture CORS configuration
    private static class TestCorsRegistry extends CorsRegistry {
        private String pathPattern;
        private java.util.List<String> allowedOrigins = new java.util.ArrayList<>();
        private java.util.List<String> allowedMethods = new java.util.ArrayList<>();
        private boolean allowCredentials;

        @Override
        public org.springframework.web.servlet.config.annotation.CorsRegistration addMapping(String pathPattern) {
            this.pathPattern = pathPattern;
            return new org.springframework.web.servlet.config.annotation.CorsRegistration(pathPattern) {
                @Override
                public org.springframework.web.servlet.config.annotation.CorsRegistration allowedOrigins(String... origins) {
                    allowedOrigins.addAll(java.util.Arrays.asList(origins));
                    return this;
                }

                @Override
                public org.springframework.web.servlet.config.annotation.CorsRegistration allowedMethods(String... methods) {
                    allowedMethods.addAll(java.util.Arrays.asList(methods));
                    return this;
                }

                @Override
                public org.springframework.web.servlet.config.annotation.CorsRegistration allowCredentials(boolean allowCredentials) {
                    TestCorsRegistry.this.allowCredentials = allowCredentials;
                    return this;
                }
            };
        }

        public String getPathPattern() {
            return pathPattern;
        }

        public java.util.List<String> getAllowedOrigins() {
            return allowedOrigins;
        }

        public java.util.List<String> getAllowedMethods() {
            return allowedMethods;
        }

        public boolean isAllowCredentials() {
            return allowCredentials;
        }
    }
}