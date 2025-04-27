package com.example.demo.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import com.cloudinary.utils.ObjectUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CloudinaryServiceTest {

    @Mock
    private Cloudinary cloudinary;

    @Mock
    private MultipartFile file;

    @InjectMocks
    private CloudinaryService cloudinaryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUploadPdf_Success() throws IOException {
        // Arrange
        Uploader uploader = mock(Uploader.class);
        when(cloudinary.uploader()).thenReturn(uploader);

        Map<String, Object> uploadResult = new HashMap<>();
        uploadResult.put("secure_url", "https://example.com/uploaded.pdf");

        when(file.getBytes()).thenReturn(new byte[]{1, 2, 3});
        when(uploader.upload(any(byte[].class), eq(ObjectUtils.asMap(
                "resource_type", "raw",
                "folder", "pdf-uploads"
        )))).thenReturn(uploadResult);

        // Act
        String result = cloudinaryService.uploadPdf(file);

        // Assert
        assertEquals("https://example.com/uploaded.pdf", result);
        verify(uploader, times(1)).upload(any(byte[].class), anyMap());
    }

    @Test
    void testUploadPdf_Failure() throws IOException {
        // Arrange
        Uploader uploader = mock(Uploader.class);
        when(cloudinary.uploader()).thenReturn(uploader);

        when(file.getBytes()).thenReturn(new byte[]{1, 2, 3});
        when(uploader.upload(any(byte[].class), anyMap())).thenThrow(new IOException("Upload failed"));

        // Act & Assert
        Exception exception = assertThrows(IOException.class, () -> cloudinaryService.uploadPdf(file));
        assertEquals("Upload failed", exception.getMessage());
        verify(uploader, times(1)).upload(any(byte[].class), anyMap());
    }
}