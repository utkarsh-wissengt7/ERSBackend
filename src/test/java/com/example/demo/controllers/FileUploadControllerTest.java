package com.example.demo.controllers;

import com.example.demo.services.CloudinaryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class FileUploadControllerTest {

    @Mock
    private CloudinaryService cloudinaryService;

    @InjectMocks
    private FileUploadController fileUploadController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUploadPdf_Success() throws IOException {
        // Arrange
        MockMultipartFile file = new MockMultipartFile("file", "test.pdf", "application/pdf", "dummy content".getBytes());
        when(cloudinaryService.uploadPdf(file)).thenReturn("http://example.com/test.pdf");

        // Act
        ResponseEntity<String> response = fileUploadController.uploadPdf(file);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("http://example.com/test.pdf", response.getBody());
        verify(cloudinaryService, times(1)).uploadPdf(file);
    }

    @Test
    void testUploadPdf_EmptyFile() throws IOException {
        // Arrange
        MockMultipartFile file = new MockMultipartFile("file", "", "application/pdf", new byte[0]);

        // Act
        ResponseEntity<String> response = fileUploadController.uploadPdf(file);

        // Assert
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("No file uploaded!", response.getBody());
        verify(cloudinaryService, never()).uploadPdf(file);
    }

    @Test
    void testUploadPdf_Exception() throws IOException {
        // Arrange
        MockMultipartFile file = new MockMultipartFile("file", "test.pdf", "application/pdf", "dummy content".getBytes());
        when(cloudinaryService.uploadPdf(file)).thenThrow(new IOException("Upload error"));

        // Act
        ResponseEntity<String> response = fileUploadController.uploadPdf(file);

        // Assert
        assertEquals(500, response.getStatusCodeValue());
        assertEquals("Upload failed: Upload error", response.getBody());
        verify(cloudinaryService, times(1)).uploadPdf(file);
    }
}
