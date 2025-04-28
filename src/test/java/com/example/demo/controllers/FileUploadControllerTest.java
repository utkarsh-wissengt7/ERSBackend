package com.example.demo.controllers;

import com.example.demo.services.CloudinaryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
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
        MockMultipartFile file = new MockMultipartFile(
            "file", 
            "test.pdf",
            "application/pdf",
            "test content".getBytes()
        );
        String expectedUrl = "https://res.cloudinary.com/demo/test.pdf";
        when(cloudinaryService.uploadPdf(file)).thenReturn(expectedUrl);

        // Act
        ResponseEntity<String> response = fileUploadController.uploadPdf(file);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedUrl, response.getBody());
        verify(cloudinaryService, times(1)).uploadPdf(file);
    }

    // @Test
    // void testUploadPdf_EmptyFile() throws IOException {
    //     // Arrange
    //     MockMultipartFile file = new MockMultipartFile(
    //         "file",
    //         "empty.pdf",
    //         "application/pdf",
    //         new byte[0]
    //     );

    //     // Act
    //     ResponseEntity<String> response = fileUploadController.uploadPdf(file);

    //     // Assert
    //     assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    //     assertEquals("Empty file", response.getBody());
    //     verify(cloudinaryService, never()).uploadPdf(any(MultipartFile.class));
    // }

    // @Test
    // void testUploadPdf_InvalidFileType() throws IOException {
    //     // Arrange
    //     MockMultipartFile file = new MockMultipartFile(
    //         "file",
    //         "test.txt",
    //         "text/plain",
    //         "test content".getBytes()
    //     );

    //     // Act
    //     ResponseEntity<String> response = fileUploadController.uploadPdf(file);

    //     // Assert
    //     assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    //     assertEquals("Only PDF files are allowed", response.getBody());
    //     verify(cloudinaryService, never()).uploadPdf(any(MultipartFile.class));
    // }

    @Test
    void testUploadPdf_UploadError() throws IOException {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test.pdf",
            "application/pdf",
            "test content".getBytes()
        );
        when(cloudinaryService.uploadPdf(file)).thenThrow(new IOException("Upload failed"));

        // Act
        ResponseEntity<String> response = fileUploadController.uploadPdf(file);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Upload failed: Upload failed", response.getBody());
        verify(cloudinaryService, times(1)).uploadPdf(file);
    }

    // @Test
    // void testUploadPdf_NullFile() throws IOException {
    //     // Act
    //     ResponseEntity<String> response = fileUploadController.uploadPdf(null);

    //     // Assert
    //     assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    //     assertEquals("No file provided", response.getBody());
    //     verify(cloudinaryService, never()).uploadPdf(any(MultipartFile.class));
    // }

    // @Test
    // void testUploadPdf_InvalidFile() throws IOException {
    //     // Arrange
    //     MockMultipartFile file = new MockMultipartFile(
    //         "file",
    //         "invalid.pdf",
    //         "application/pdf",
    //         "invalid content".getBytes()
    //     );

    //     // Act
    //     ResponseEntity<String> response = fileUploadController.uploadPdf(file);

    //     // Assert
    //     assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    //     assertEquals("Invalid file", response.getBody());
    //     verify(cloudinaryService, never()).uploadPdf(any(MultipartFile.class));
    // }
}
