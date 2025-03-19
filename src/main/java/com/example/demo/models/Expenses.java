package com.example.demo.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "expenses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Expenses {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long expenseID;

    @ManyToOne
    @JoinColumn(name = "userID")
    private User user;

    private String title;  // New field for title
    private String description; // New field for description
    private Double amount;
    private String category;
    private String receipt;
    private String status;
    private Long approvedBy;
    private Long rejectedBy;
    private String reasonForRejection;

    @Column(nullable = true, updatable = true)
    private LocalDate dateCreated; // Auto-set to current date

    @PrePersist
    protected void onCreate() {
        this.dateCreated = LocalDate.now(); // Set to the current date on creation
    }

    public Long getId() {
        return expenseID;
    }
}
