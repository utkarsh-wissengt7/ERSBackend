package com.example.demo.models;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class NotificationTest {

    @Test
    void testNotificationConstructorsAndGettersSetters() {
        // Test no-args constructor
        Notification notification = new Notification();
        assertNotNull(notification);

        // Test setters
        notification.setId(1L);
        notification.setUserId("WCS171");
        LocalDateTime now = LocalDateTime.now();
        notification.setCreatedAt(now);
        notification.setStatus("PENDING");
        notification.setExpenseId(100L);
        notification.setManagerId("MGR001");
        notification.setMessage("New expense submitted");

        // Test getters
        assertEquals(1L, notification.getId());
        assertEquals("WCS171", notification.getUserId());
        assertEquals(now, notification.getCreatedAt());
        assertEquals("PENDING", notification.getStatus());
        assertEquals(100L, notification.getExpenseId());
        assertEquals("MGR001", notification.getManagerId());
        assertEquals("New expense submitted", notification.getMessage());
    }

    @Test
    void testNotificationBuilder() {
        // Test builder pattern
        LocalDateTime now = LocalDateTime.now();
        Notification notification = Notification.builder()
                .id(1L)
                .userId("WCS171")
                .createdAt(now)
                .status("PENDING")
                .expenseId(100L)
                .managerId("MGR001")
                .message("New expense submitted")
                .build();

        assertEquals(1L, notification.getId());
        assertEquals("WCS171", notification.getUserId());
        assertEquals(now, notification.getCreatedAt());
        assertEquals("PENDING", notification.getStatus());
        assertEquals(100L, notification.getExpenseId());
        assertEquals("MGR001", notification.getManagerId());
        assertEquals("New expense submitted", notification.getMessage());
    }

    // @Test
    // void testNotificationEqualsAndHashCode() {
    //     Notification notification1 = Notification.builder()
    //             .id(1L)
    //             .userId("WCS171")
    //             .status("PENDING")
    //             .build();

    //     Notification notification2 = Notification.builder()
    //             .id(1L)
    //             .userId("WCS171")
    //             .status("PENDING")
    //             .build();

    //     Notification notification3 = Notification.builder()
    //             .id(2L)
    //             .userId("WCS172")
    //             .status("APPROVED")
    //             .build();

    //     // Test equals
    //     assertEquals(notification1, notification2);
    //     assertNotEquals(notification1, notification3);

    //     // Test hashCode
    //     assertEquals(notification1.hashCode(), notification2.hashCode());
    //     assertNotEquals(notification1.hashCode(), notification3.hashCode());
    // }

    @Test
    void testPrePersist() {
        // Test automatic field population on persist
        Notification notification = new Notification();
        notification.onCreate();

        assertNotNull(notification.getCreatedAt());
        assertEquals("Pending", notification.getStatus());
    }

    @Test
    void testAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        Notification notification = new Notification(
                1L,
                "WCS171",
                now,
                "PENDING",
                100L,
                "MGR001",
                "New expense submitted"
        );

        assertNotNull(notification);
        assertEquals(1L, notification.getId());
        assertEquals("WCS171", notification.getUserId());
        assertEquals(now, notification.getCreatedAt());
        assertEquals("PENDING", notification.getStatus());
        assertEquals(100L, notification.getExpenseId());
        assertEquals("MGR001", notification.getManagerId());
        assertEquals("New expense submitted", notification.getMessage());
    }
}