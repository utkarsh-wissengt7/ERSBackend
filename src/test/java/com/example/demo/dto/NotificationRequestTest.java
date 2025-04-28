package com.example.demo.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NotificationRequestTest {

    @Test
    void testNotificationRequestBuilder() {
        // Act
        NotificationRequest request = NotificationRequest.builder()
                .message("Test notification")
                .status("PENDING")
                .userId("WCS171")
                .managerId("MGR001")
                .expenseId(1L)
                .build();

        // Assert
        assertEquals("Test notification", request.getMessage());
        assertEquals("PENDING", request.getStatus());
        assertEquals("WCS171", request.getUserId());
        assertEquals("MGR001", request.getManagerId());
        assertEquals(1L, request.getExpenseId());
    }

    @Test
    void testNotificationRequestSettersAndGetters() {
        // Arrange
        NotificationRequest request = new NotificationRequest();

        // Act
        request.setMessage("New expense submitted");
        request.setStatus("PENDING");
        request.setUserId("WCS171");
        request.setManagerId("MGR001");
        request.setExpenseId(2L);

        // Assert
        assertEquals("New expense submitted", request.getMessage());
        assertEquals("PENDING", request.getStatus());
        assertEquals("WCS171", request.getUserId());
        assertEquals("MGR001", request.getManagerId());
        assertEquals(2L, request.getExpenseId());
    }

    @Test
    void testNotificationRequestEquals() {
        // Arrange
        NotificationRequest request1 = NotificationRequest.builder()
                .message("Test")
                .status("PENDING")
                .userId("WCS171")
                .build();

        NotificationRequest request2 = NotificationRequest.builder()
                .message("Test")
                .status("PENDING")
                .userId("WCS171")
                .build();

        NotificationRequest request3 = NotificationRequest.builder()
                .message("Different")
                .status("APPROVED")
                .userId("WCS172")
                .build();

        // Assert
        assertEquals(request1, request2);
        assertNotEquals(request1, request3);
        assertEquals(request1.hashCode(), request2.hashCode());
    }

    @Test
    void testNotificationRequestNoArgsConstructor() {
        // Act
        NotificationRequest request = new NotificationRequest();

        // Assert
        assertNotNull(request);
        assertNull(request.getMessage());
        assertNull(request.getStatus());
        assertNull(request.getUserId());
        assertNull(request.getManagerId());
        assertNull(request.getExpenseId());
    }
}