package com.example.demo.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Column(unique = true)
    private String email;

    @JsonIgnore
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    private String role;

    private LocalDate dateOfJoining;

    // Store only manager's ID instead of User reference
    @Column(name = "manager_id")
    private Long managerId;

    // Store only subordinate IDs instead of User references
    @ElementCollection
    @CollectionTable(name = "user_subordinates", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "subordinate_id")
    private List<Long> subordinateIds;

    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isManager; // Newly added column
}
