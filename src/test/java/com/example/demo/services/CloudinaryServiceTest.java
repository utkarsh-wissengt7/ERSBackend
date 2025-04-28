package com.example.demo.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import com.cloudinary.utils.ObjectUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class CloudinaryServiceTest {

    @Mock
    private Cloudinary cloudinary;

    @Mock
    private Uploader uploader;

    @InjectMocks
    private CloudinaryService cloudinaryService;

    private MultipartFile file;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        file = new MockMultipartFile(
            "file",
            "test.pdf",
            "application/pdf",
            "test content".getBytes()
        );
        when(cloudinary.uploader()).thenReturn(uploader);
    }

    @Test
    void testUploadPdf_Success() throws IOException {
        // Arrange
        Map<String, Object> expectedResponse = new HashMap<>();
        expectedResponse.put("secure_url", "https://res.cloudinary.com/demo/test.pdf");
        
        when(uploader.upload(
            any(byte[].class),
            eq(ObjectUtils.asMap(
                "resource_type", "raw",
                "folder", "pdf-uploads"
            ))
        )).thenReturn(expectedResponse);

        // Act
        String result = cloudinaryService.uploadPdf(file);

        // Assert
        assertEquals("https://res.cloudinary.com/demo/test.pdf", result);
        verify(uploader, times(1)).upload(any(byte[].class), anyMap());
    }

    @Test
    void testUploadPdf_NullFile() throws IOException {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            cloudinaryService.uploadPdf(null)
        );
        verify(uploader, never()).upload(any(byte[].class), anyMap());
    }

    @Test
    void testUploadPdf_EmptyFile() throws IOException {
        // Arrange
        MultipartFile emptyFile = new MockMultipartFile(
            "file",
            "empty.pdf",
            "application/pdf",
            new byte[0]
        );

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            cloudinaryService.uploadPdf(emptyFile)
        );
        verify(uploader, never()).upload(any(byte[].class), anyMap());
    }

    @Test
    void testUploadPdf_UploadFailure() throws IOException {
        // Arrange
        when(uploader.upload(
            any(byte[].class),
            anyMap()
        )).thenThrow(new IOException("Upload failed"));

        // Act & Assert
        Exception exception = assertThrows(IOException.class, () -> 
            cloudinaryService.uploadPdf(file)
        );
        assertEquals("Upload failed", exception.getMessage());
        verify(uploader, times(1)).upload(any(byte[].class), anyMap());
    }

    @Test
    void testUploadPdf_InvalidResponseFormat() throws IOException {
        // Arrange
        Map<String, Object> invalidResponse = new HashMap<>();
        // Missing secure_url in response
        
        when(uploader.upload(
            any(byte[].class),
            anyMap()
        )).thenReturn(invalidResponse);

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> 
            cloudinaryService.uploadPdf(file)
        );
        assertEquals("Failed to get secure URL from upload response", exception.getMessage());
        verify(uploader, times(1)).upload(any(byte[].class), anyMap());
    }
}