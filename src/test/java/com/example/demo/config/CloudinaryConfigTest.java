package com.example.demo.config;

import com.cloudinary.Cloudinary;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CloudinaryConfigTest {

    private final CloudinaryConfig config = new CloudinaryConfig();

    @Test
    void testCloudinaryConfiguration() {
        // Act
        Cloudinary cloudinary = config.cloudinary();

        // Assert
        assertNotNull(cloudinary);
        assertEquals("drmr3akoc", cloudinary.config.cloudName);
        assertEquals("711469594242557", cloudinary.config.apiKey);
        assertEquals("Ux8hJQMGYiIfitpM5NaMUKmQj5M", cloudinary.config.apiSecret);
        assertTrue(cloudinary.config.secure);
    }
}