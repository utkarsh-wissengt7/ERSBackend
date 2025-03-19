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
        // Fetch existing User
        if (notification.getUser() != null && notification.getUser().getId() != null) {
            User user = userRepository.findById(notification.getUser().getId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            notification.setUser(user);
        }

        // Fetch existing Manager
        if (notification.getManager() != null && notification.getManager().getId() != null) {
            User manager = userRepository.findById(notification.getManager().getId())
                    .orElseThrow(() -> new RuntimeException("Manager not found"));
            notification.setManager(manager);
        }

        // Fetch existing Expense
        if (notification.getExpense() != null && notification.getExpense().getId() != null) {
            Expenses expense = expenseRepository.findById(notification.getExpense().getId())
                    .orElseThrow(() -> new RuntimeException("Expense not found"));
            notification.setExpense(expense);
        }

        return notificationRepository.save(notification);
    }

    // Update an existing notification
    public Notification updateNotification(Long id, Notification notificationDetails) {
        return notificationRepository.findById(id).map(notification -> {
            notification.setMessage(notificationDetails.getMessage());
            notification.setStatus(notificationDetails.getStatus());

            // Fetch and set updated user if provided
            if (notificationDetails.getUser() != null && notificationDetails.getUser().getId() != null) {
                User user = userRepository.findById(notificationDetails.getUser().getId())
                        .orElseThrow(() -> new RuntimeException("User not found"));
                notification.setUser(user);
            }

            // Fetch and set updated manager if provided
            if (notificationDetails.getManager() != null && notificationDetails.getManager().getId() != null) {
                User manager = userRepository.findById(notificationDetails.getManager().getId())
                        .orElseThrow(() -> new RuntimeException("Manager not found"));
                notification.setManager(manager);
            }

            // Fetch and set updated expense if provided
            if (notificationDetails.getExpense() != null && notificationDetails.getExpense().getId() != null) {
                Expenses expense = expenseRepository.findById(notificationDetails.getExpense().getId())
                        .orElseThrow(() -> new RuntimeException("Expense not found"));
                notification.setExpense(expense);
            }

            return notificationRepository.save(notification);
        }).orElseThrow(() -> new RuntimeException("Notification not found"));
    }

    // Delete a notification by ID
    public void deleteNotification(Long id) {
        if (!notificationRepository.existsById(id)) {
            throw new RuntimeException("Notification not found");
        }
        notificationRepository.deleteById(id);
    }

    // Get notifications by User ID
    public List<Notification> getNotificationsByUserId(Long userId) {
        return notificationRepository.findByUserId(userId);
    }

    // Get notifications by status
    public List<Notification> getNotificationsByStatus(String status) {
        return notificationRepository.findByStatus(status);
    }
}
