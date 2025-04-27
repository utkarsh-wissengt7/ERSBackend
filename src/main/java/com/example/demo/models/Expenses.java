package com.example.demo.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

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
    @JoinColumn(name = "wissen_id", referencedColumnName = "wissen_id")
    private User user;

    @Column(name = "wissen_id", insertable = false, updatable = false)
    private String wissenID;  // Add this field to match repository query

    private String description;
    private Double amount;
    private String category;
    private String receipt;
    private String status;
    @Column(length = 10)
    private String approvedBy;

    @Column(length = 10)
    private String rejectedBy;
    private String reasonForRejection;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    public Long getId() {
        return expenseID;
    }


    public void setId(long l) {
        this.expenseID = l;
    }
}
