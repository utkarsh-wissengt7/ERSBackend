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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    private Notification notification;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        notification = Notification.builder()
                .id(1L)
                .userId("WCS171")
                .message("Test notification")
                .status("UNREAD")
                .build();
    }

    @Test
    void testCreateNotification() {
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        Notification result = notificationService.createNotification(notification);

        assertNotNull(result);
        assertEquals("Test notification", result.getMessage());
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void testGetAllNotifications() {
        List<Notification> notifications = Arrays.asList(notification);
        when(notificationRepository.findAll()).thenReturn(notifications);

        List<Notification> result = notificationService.getAllNotifications();

        assertEquals(1, result.size());
        verify(notificationRepository, times(1)).findAll();
    }

    @Test
    void testGetNotificationById() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));

        Optional<Notification> result = notificationService.getNotificationById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void testGetNotificationsByStatus() {
        List<Notification> notifications = Arrays.asList(notification);
        when(notificationRepository.findByStatus("UNREAD")).thenReturn(notifications);

        List<Notification> result = notificationService.getNotificationsByStatus("UNREAD");

        assertEquals(1, result.size());
        assertEquals("UNREAD", result.get(0).getStatus());
    }

    @Test
    void testGetNotificationsByUserId() {
        List<Notification> notifications = Arrays.asList(notification);
        when(notificationRepository.findByUserId("WCS171")).thenReturn(notifications);

        List<Notification> result = notificationService.getNotificationsByUserId("WCS171");

        assertEquals(1, result.size());
        assertEquals("WCS171", result.get(0).getUserId());
    }

    @Test
    void testDeleteNotification_ExistingNotification() {
        // Arrange
        when(notificationRepository.existsById(1L)).thenReturn(true);
        doNothing().when(notificationRepository).deleteById(1L);

        // Act & Assert - should not throw exception
        assertDoesNotThrow(() -> notificationService.deleteNotification(1L));
        verify(notificationRepository, times(1)).existsById(1L);
        verify(notificationRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteNotification_NonExistingNotification() {
        // Arrange
        when(notificationRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> notificationService.deleteNotification(999L));
        assertEquals("Notification not found", exception.getMessage());
        verify(notificationRepository, times(1)).existsById(999L);
        verify(notificationRepository, never()).deleteById(999L);
    }

    @Test
    void testNotificationNotFound() {
        when(notificationRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Notification> result = notificationService.getNotificationById(999L);

        assertFalse(result.isPresent());
    }
}