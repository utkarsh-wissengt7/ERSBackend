package com.example.demo.controllers;

import com.example.demo.dto.NotificationRequest;
import com.example.demo.models.Notification;
import com.example.demo.services.NotificationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/notifications/")
public class NotificationController {

    private static final Logger log = LogManager.getLogger(NotificationController.class);
    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public List<Notification> getAllNotifications() {
        return notificationService.getAllNotifications();
    }

    @GetMapping("/{id}")
    public Optional<Notification> getNotificationById(@PathVariable Long id) {
        return notificationService.getNotificationById(id);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getNotificationsByUserId(@PathVariable String userId) {
        List<Notification> notifications = notificationService.getNotificationsByUserId(userId);
        return ResponseEntity.ok(notifications);
    }


    @PostMapping
    public ResponseEntity<Notification> createNotification(@RequestBody NotificationRequest request) {
        try {
            if (request.getMessage() == null || request.getUserId() == null) {
                return ResponseEntity.badRequest().build();
            }

            Notification notification = Notification.builder()
                    .message(request.getMessage())
                    .status(request.getStatus())
                    .userId(request.getUserId())
                    .managerId(request.getManagerId())
                    .expenseId(request.getExpenseId())
                    .build();

            Notification createdNotification = notificationService.createNotification(notification);
            return ResponseEntity.ok(createdNotification);
        } catch (Exception e) {
            log.error("Error creating notification: ", e);
            return ResponseEntity.badRequest().build();
        }
    }



    @DeleteMapping("/{id}")
    public void deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
    }


    @GetMapping("/status/{status}")
    public List<Notification> getNotificationsByStatus(@PathVariable String status) {
        return notificationService.getNotificationsByStatus(status);
    }
}
