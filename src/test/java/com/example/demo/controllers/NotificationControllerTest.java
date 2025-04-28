package com.example.demo.controllers;

import com.example.demo.dto.NotificationRequest;
import com.example.demo.models.Notification;
import com.example.demo.services.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class NotificationControllerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationController notificationController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllNotifications() {
        List<Notification> notifications = Arrays.asList(
            Notification.builder()
                .id(1L)
                .userId("WCS171")
                .message("Test notification 1")
                .build(),
            Notification.builder()
                .id(2L)
                .userId("WCS171")
                .message("Test notification 2")
                .build()
        );
        when(notificationService.getAllNotifications()).thenReturn(notifications);

        List<Notification> response = notificationController.getAllNotifications();

        assertEquals(2, response.size());
        verify(notificationService, times(1)).getAllNotifications();
    }

    @Test
    void testGetNotificationsByUserId() {
        String userId = "WCS171";
        List<Notification> notifications = Arrays.asList(
            Notification.builder()
                .id(1L)
                .userId(userId)
                .message("Test notification")
                .build()
        );
        when(notificationService.getNotificationsByUserId(userId)).thenReturn(notifications);

        ResponseEntity<List<Notification>> response = notificationController.getNotificationsByUserId(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(userId, response.getBody().get(0).getUserId());
        verify(notificationService, times(1)).getNotificationsByUserId(userId);
    }

    @Test
    void testGetNotificationsByStatus() {
        String status = "UNREAD";
        List<Notification> notifications = Arrays.asList(
            Notification.builder()
                .id(1L)
                .userId("WCS171")
                .status(status)
                .message("Unread notification")
                .build()
        );
        when(notificationService.getNotificationsByStatus(status)).thenReturn(notifications);

        List<Notification> response = notificationController.getNotificationsByStatus(status);

        assertEquals(1, response.size());
        assertEquals(status, response.get(0).getStatus());
        verify(notificationService, times(1)).getNotificationsByStatus(status);
    }

    @Test
    void testCreateNotification() {
        // Arrange
        NotificationRequest request = new NotificationRequest();
        request.setUserId("WCS171");
        request.setMessage("Test notification");
        
        Notification notification = new Notification();
        notification.setUserId("WCS171");
        notification.setMessage("Test notification");
        
        when(notificationService.createNotification(any(Notification.class))).thenReturn(notification);

        // Act
        ResponseEntity<Notification> responseEntity = notificationController.createNotification(request);
        Notification response = responseEntity.getBody();

        // Assert
        assertNotNull(response);
        assertEquals("Test notification", response.getMessage());
        verify(notificationService, times(1)).createNotification(any(Notification.class));
    }

    @Test
    void testDeleteNotification() {
        Long notificationId = 1L;
        doNothing().when(notificationService).deleteNotification(notificationId);

        notificationController.deleteNotification(notificationId);

        verify(notificationService, times(1)).deleteNotification(notificationId);
    }

    @Test
    void testGetNotificationById() {
        Long notificationId = 1L;
        Notification notification = Notification.builder()
            .id(notificationId)
            .build();
        when(notificationService.getNotificationById(notificationId)).thenReturn(Optional.of(notification));

        Optional<Notification> response = notificationController.getNotificationById(notificationId);

        assertTrue(response.isPresent());
        assertEquals(notificationId, response.get().getId());
        verify(notificationService, times(1)).getNotificationById(notificationId);
    }
}