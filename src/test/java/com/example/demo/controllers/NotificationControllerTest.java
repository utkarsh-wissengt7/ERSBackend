package com.example.demo.controllers;

import com.example.demo.dto.NotificationRequest;
import com.example.demo.models.Notification;
import com.example.demo.services.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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
        List<Notification> notifications = Arrays.asList(new Notification(), new Notification());
        when(notificationService.getAllNotifications()).thenReturn(notifications);

        List<Notification> response = notificationController.getAllNotifications();

        assertEquals(2, response.size());
        verify(notificationService, times(1)).getAllNotifications();
    }

    @Test
    void testGetNotificationById_Success() {
        Notification notification = new Notification();
        when(notificationService.getNotificationById(1L)).thenReturn(Optional.of(notification));

        Optional<Notification> response = notificationController.getNotificationById(1L);

        assertTrue(response.isPresent());
        assertEquals(notification, response.get());
        verify(notificationService, times(1)).getNotificationById(1L);
    }

    @Test
    void testGetNotificationById_NotFound() {
        when(notificationService.getNotificationById(1L)).thenReturn(Optional.empty());

        Optional<Notification> response = notificationController.getNotificationById(1L);

        assertFalse(response.isPresent());
        verify(notificationService, times(1)).getNotificationById(1L);
    }

    @Test
    void testGetNotificationsByUserId() {
        List<Notification> notifications = Arrays.asList(new Notification(), new Notification());
        when(notificationService.getNotificationsByUserId("user1")).thenReturn(notifications);

        ResponseEntity<List<Notification>> response = notificationController.getNotificationsByUserId("user1");

        assertEquals(2, response.getBody().size());
        assertEquals(200, response.getStatusCodeValue());
        verify(notificationService, times(1)).getNotificationsByUserId("user1");
    }

    @Test
    void testCreateNotification_Success() {
        NotificationRequest request = new NotificationRequest();
        request.setMessage("Test Message");
        request.setUserId("user1");

        Notification notification = new Notification();
        when(notificationService.createNotification(any(Notification.class))).thenReturn(notification);

        ResponseEntity<Notification> response = notificationController.createNotification(request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(notification, response.getBody());
        verify(notificationService, times(1)).createNotification(any(Notification.class));
    }

    @Test
    void testCreateNotification_BadRequest() {
        NotificationRequest request = new NotificationRequest(); // Missing required fields

        ResponseEntity<Notification> response = notificationController.createNotification(request);

        assertEquals(400, response.getStatusCodeValue());
        verify(notificationService, never()).createNotification(any(Notification.class));
    }

    @Test
    void testCreateNotification_Exception() {
        NotificationRequest request = new NotificationRequest();
        request.setMessage("Test Message");
        request.setUserId("user1");

        when(notificationService.createNotification(any(Notification.class))).thenThrow(new RuntimeException("Error"));

        ResponseEntity<Notification> response = notificationController.createNotification(request);

        assertEquals(400, response.getStatusCodeValue());
        verify(notificationService, times(1)).createNotification(any(Notification.class));
    }

    @Test
    void testDeleteNotification() {
        doNothing().when(notificationService).deleteNotification(1L);

        notificationController.deleteNotification(1L);

        verify(notificationService, times(1)).deleteNotification(1L);
    }

    @Test
    void testGetNotificationsByStatus() {
        List<Notification> notifications = Arrays.asList(new Notification(), new Notification());
        when(notificationService.getNotificationsByStatus("PENDING")).thenReturn(notifications);

        List<Notification> response = notificationController.getNotificationsByStatus("PENDING");

        assertEquals(2, response.size());
        verify(notificationService, times(1)).getNotificationsByStatus("PENDING");
    }
}