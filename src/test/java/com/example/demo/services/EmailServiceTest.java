package com.example.demo.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSendEmail_Success() throws MessagingException, IOException {
        // Arrange
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("name", "John Doe");
        placeholders.put("message", "Welcome to our platform!");

        String templatePath = "templates/emailTemplate.html";
        String templateContent = "<html><body><p>Hello {name},</p><p>{message}</p></body></html>";
        ClassPathResource resource = mock(ClassPathResource.class);
        when(resource.getFile()).thenReturn(Files.createTempFile("emailTemplate", ".html").toFile());
        Files.writeString(resource.getFile().toPath(), templateContent);

        // Act
        emailService.sendEmail("test@example.com", "Welcome", templatePath, placeholders);

        // Assert
        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    void testSendEmail_TemplateNotFound() {
        // Arrange
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("name", "John Doe");

        // Act & Assert
        assertThrows(IOException.class, () -> {
            emailService.sendEmail("test@example.com", "Subject", "invalid/path.html", placeholders);
        });
    }

    @Test
    void testSendEmail_MessagingException() throws IOException {
        // Arrange
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new org.springframework.mail.MailException("Failed to send email") {}).when(mailSender).send(any(MimeMessage.class));

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("name", "John Doe");

        String templatePath = "templates/emailTemplate.html";
        Files.writeString(Files.createTempFile("emailTemplate", ".html"), "<html><body>Hello {name}</body></html>");

        // Act & Assert
        assertThrows(org.springframework.mail.MailException.class, () -> {
            emailService.sendEmail("test@example.com", "Subject", templatePath, placeholders);
        });
    }
}