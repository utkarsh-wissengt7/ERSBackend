package com.example.demo.controllers;

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
    public ResponseEntity<List<Notification>> getNotificationsByUserId(@PathVariable Long userId) {
        List<Notification> notifications = notificationService.getNotificationsByUserId(userId);
        return ResponseEntity.ok(notifications);
    }


    @PostMapping
    public ResponseEntity<Notification> createNotification(@RequestBody Notification notification) {
        Notification createdNotification = notificationService.createNotification(notification);
        return ResponseEntity.ok(createdNotification);
    }


//    @PutMapping("/{id}")
//    public Notification updateNotification(@PathVariable Long id, @RequestBody Notification notificationDetails) {
//        return notificationService.updateNotification(id, notificationDetails);
//    }

    @DeleteMapping("/{id}")
    public void deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
    }


    @GetMapping("/status/{status}")
    public List<Notification> getNotificationsByStatus(@PathVariable String status) {
        return notificationService.getNotificationsByStatus(status);
    }
}
