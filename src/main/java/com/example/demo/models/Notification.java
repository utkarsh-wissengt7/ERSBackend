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

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "userID", nullable = true)
    private User user;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private String status;  // Accepted, Rejected, Pending

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "expenseID", nullable = true)
    private Expenses expense;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "managerID", nullable = true)
    private User manager;

    @Column(nullable = false, length = 500)
    private String message; // Added the missing field

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
