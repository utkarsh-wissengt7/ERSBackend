package com.example.demo.services;

import com.example.demo.models.Notification;
import com.example.demo.repositories.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllNotifications() {
        Notification notification1 = new Notification();
        Notification notification2 = new Notification();
        when(notificationRepository.findAll()).thenReturn(Arrays.asList(notification1, notification2));

        List<Notification> notifications = notificationService.getAllNotifications();

        assertEquals(2, notifications.size());
        verify(notificationRepository, times(1)).findAll();
    }

    @Test
    void testGetNotificationById_Success() {
        Notification notification = new Notification();
        notification.setId(1L);
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));

        Optional<Notification> result = notificationService.getNotificationById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        verify(notificationRepository, times(1)).findById(1L);
    }

    @Test
    void testGetNotificationById_NotFound() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Notification> result = notificationService.getNotificationById(1L);

        assertFalse(result.isPresent());
        verify(notificationRepository, times(1)).findById(1L);
    }

    @Test
    void testCreateNotification() {
        Notification notification = new Notification();
        when(notificationRepository.save(notification)).thenReturn(notification);

        Notification result = notificationService.createNotification(notification);

        assertNotNull(result);
        verify(notificationRepository, times(1)).save(notification);
    }

    @Test
    void testDeleteNotification_Success() {
        when(notificationRepository.existsById(1L)).thenReturn(true);

        notificationService.deleteNotification(1L);

        verify(notificationRepository, times(1)).existsById(1L);
        verify(notificationRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteNotification_NotFound() {
        when(notificationRepository.existsById(1L)).thenReturn(false);

        Exception exception = assertThrows(RuntimeException.class, () -> notificationService.deleteNotification(1L));

        assertEquals("Notification not found", exception.getMessage());
        verify(notificationRepository, times(1)).existsById(1L);
        verify(notificationRepository, never()).deleteById(1L);
    }

    @Test
    void testGetNotificationsByStatus() {
        Notification notification = new Notification();
        notification.setStatus("Accepted");
        when(notificationRepository.findByStatus("Accepted")).thenReturn(Arrays.asList(notification));

        List<Notification> notifications = notificationService.getNotificationsByStatus("Accepted");

        assertEquals(1, notifications.size());
        assertEquals("Accepted", notifications.get(0).getStatus());
        verify(notificationRepository, times(1)).findByStatus("Accepted");
    }

    @Test
    void testGetNotificationsByUserId() {
        Notification notification = new Notification();
        notification.setUserId("USER123");
        when(notificationRepository.findByUserId("USER123")).thenReturn(Arrays.asList(notification));

        List<Notification> notifications = notificationService.getNotificationsByUserId("USER123");

        assertEquals(1, notifications.size());
        assertEquals("USER123", notifications.get(0).getUserId());
        verify(notificationRepository, times(1)).findByUserId("USER123");
    }
}