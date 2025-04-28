package com.example.demo.repositories;

import com.example.demo.models.Notification;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class NotificationRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private NotificationRepository notificationRepository;

    @Test
    void findByUserId() {
        // Arrange
        String userId = "WCS171";
        
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setMessage("Test notification");
        notification.setStatus("UNREAD");
        entityManager.persist(notification);
        entityManager.flush();

        // Act
        List<Notification> notifications = notificationRepository.findByUserId(userId);

        // Assert
        assertEquals(1, notifications.size());
        assertEquals(userId, notifications.get(0).getUserId());
    }

    @Test
    void findByStatus() {
        // Arrange
        Notification notification1 = new Notification();
        notification1.setStatus("UNREAD");
        notification1.setMessage("Unread notification");
        notification1.setUserId("WCS171");
        entityManager.persist(notification1);

        Notification notification2 = new Notification();
        notification2.setStatus("READ");
        notification2.setMessage("Read notification");
        notification2.setUserId("WCS172");
        entityManager.persist(notification2);

        entityManager.flush();

        // Act
        List<Notification> unreadNotifications = notificationRepository.findByStatus("UNREAD");
        List<Notification> readNotifications = notificationRepository.findByStatus("READ");

        // Assert
        assertEquals(1, unreadNotifications.size());
        assertEquals(1, readNotifications.size());
        assertEquals("UNREAD", unreadNotifications.get(0).getStatus());
        assertEquals("READ", readNotifications.get(0).getStatus());
    }

    @Test
    void findAllByStatus() {
        // Arrange
        String status = "PENDING";
        Notification notification = new Notification();
        notification.setStatus(status);
        notification.setMessage("Pending notification");
        notification.setUserId("WCS171");
        entityManager.persist(notification);
        entityManager.flush();

        // Act
        List<Notification> notifications = notificationRepository.findByStatus(status);

        // Assert
        assertEquals(1, notifications.size());
        assertEquals(status, notifications.get(0).getStatus());
    }

    @Test
    void findByUserIdAndStatus() {
        // Arrange
        String userId = "WCS171";
        String status = "UNREAD";
        
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setStatus(status);
        notification.setMessage("Test notification");
        entityManager.persist(notification);
        entityManager.flush();

        // Act
        List<Notification> notifications = notificationRepository.findByUserId(userId);
        List<Notification> statusNotifications = notifications.stream()
            .filter(n -> status.equals(n.getStatus()))
            .toList();

        // Assert
        assertEquals(1, statusNotifications.size());
        assertEquals(userId, statusNotifications.get(0).getUserId());
        assertEquals(status, statusNotifications.get(0).getStatus());
    }
}