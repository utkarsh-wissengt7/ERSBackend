package com.example.demo.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "userID", length = 10)
    private String userId;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private String status;  // Accepted, Rejected, Pending

    @Column(name = "expenseID", nullable = true)
    private Long expenseId; // Store only expense ID

    @Column(name = "managerID", length = 10)
    private String managerId;

    @Column(nullable = false, length = 500)
    private String message; // Notification message

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.status == null || this.status.isEmpty()) {
            this.status = "Pending";
        }
    }
}
