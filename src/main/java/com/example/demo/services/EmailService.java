package com.example.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String templatePath, Map<String, String> placeholders) throws MessagingException, IOException {
        // Load the email template
        Path path = new ClassPathResource(templatePath).getFile().toPath();
        String template = Files.readString(path);

        // Replace placeholders with dynamic content
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            template = template.replace("{" + entry.getKey() + "}", entry.getValue());
        }

        // Send the email
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(template, true); // Enable HTML content
        mailSender.send(message);
    }
}