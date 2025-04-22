package com.example.demo.services;

import com.example.demo.models.Notification;
import com.example.demo.models.User;
import com.example.demo.models.Expenses;
import com.example.demo.repositories.NotificationRepository;
import com.example.demo.repositories.UserRepository;
import com.example.demo.repositories.ExpensesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository; // Repository for fetching User

    @Autowired
    private ExpensesRepository expenseRepository; // Repository for fetching Expense

    // Get all notifications
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    // Get a notification by ID
    public Optional<Notification> getNotificationById(Long id) {
        return notificationRepository.findById(id);
    }

    // Create a new notification
    public Notification createNotification(Notification notification) {

        return notificationRepository.save(notification);
    }

    public List<Notification> getNotificationsByUserId(String userId) {
        return notificationRepository.findByUserId(userId);
    }


    // Delete a notification by ID
    public void deleteNotification(Long id) {
        if (!notificationRepository.existsById(id)) {
            throw new RuntimeException("Notification not found");
        }
        notificationRepository.deleteById(id);
    }


    // Get notifications by status
    public List<Notification> getNotificationsByStatus(String status) {
        return notificationRepository.findByStatus(status);
    }
}
