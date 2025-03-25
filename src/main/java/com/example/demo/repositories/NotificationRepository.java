package com.example.demo.repositories;

import com.example.demo.models.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserId(String userId); // Fetch notifications by User ID
    List<Notification> findByStatus(String status); // Fetch notifications by status (Accepted, Rejected, Pending)
}
