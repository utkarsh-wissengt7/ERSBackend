package com.example.demo.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;

import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private EmailService emailService;

    private static final String TEMPLATE_CONTENT = "<html><body>Hello {name}, your expense is {status}</body></html>";

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        
        // Create test template file
        java.io.File templateDir = new java.io.File("src/test/resources/templates");
        templateDir.mkdirs();
        java.io.File templateFile = new java.io.File(templateDir, "emailTemplate.html");
        Files.writeString(templateFile.toPath(), TEMPLATE_CONTENT);
    }

    @Test
    void testSendEmail_Success() throws MessagingException, IOException {
        // Arrange
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("name", "John Doe");
        placeholders.put("status", "approved");

        // Act
        emailService.sendEmail("test@example.com", "Welcome", "templates/emailTemplate.html", placeholders);

        // Assert
        verify(mailSender, times(1)).send(any(MimeMessage.class));
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

    // @Test
    // void testSendEmail_MessagingException() throws MessagingException {
    //     // Arrange
    //     doThrow(new MessagingException("Failed to send email"))
    //         .when(mailSender).send(any(MimeMessage.class));

    //     Map<String, String> placeholders = new HashMap<>();
    //     placeholders.put("name", "John Doe");
    //     placeholders.put("status", "approved");

    //     // Act & Assert
    //     assertThrows(MessagingException.class, () -> {
    //         emailService.sendEmail("test@example.com", "Subject", "templates/emailTemplate.html", placeholders);
    //     });
    // }

    @Test
    void testSendEmailWithMultiplePlaceholders() throws MessagingException, IOException {
        // Arrange
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("name", "John");
        placeholders.put("expenseId", "123");
        placeholders.put("amount", "100.00");
        placeholders.put("status", "pending");

        // Act
        emailService.sendEmail("test@example.com", "Expense Update", "templates/emailTemplate.html", placeholders);

        // Verify
        verify(mailSender).send(any(MimeMessage.class));
    }
}