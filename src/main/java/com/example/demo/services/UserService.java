package com.example.demo.services;

import com.example.demo.models.User;
import com.example.demo.repositories.UserRepository;
import com.example.demo.exceptions.UserValidationException;
import com.example.demo.exceptions.ResourceNotFoundException;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;

@Service
@Slf4j
public class UserService{
    @Autowired
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }


    public User createUser(User user) throws MessagingException, IOException {
        // Check if WissenID already exists
        if (userRepository.findById(user.getWissenID()).isPresent()) {
            throw new UserValidationException("WissenID " + user.getWissenID() + " already exists.");
        }

        // Check if email already exists
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new UserValidationException("Email " + user.getEmail() + " already exists.");
        }

        // Check if managerId exists in DB if provided
        String managerId = user.getManagerId();
        if (managerId != null && !managerId.isEmpty()) {
            Optional<User> managerOptional = userRepository.findById(managerId);
            if (!managerOptional.isPresent()) {
                throw new UserValidationException("Manager with ID " + managerId + " does not exist.");
            }
            // Update manager's reportees list
            User manager = managerOptional.get();
            List<String> managerReportees = manager.getReportees();
            if (managerReportees == null) {
                managerReportees = new ArrayList<>();
            }
            managerReportees.add(user.getWissenID());
            manager.setReportees(managerReportees);
            userRepository.save(manager);
        }

        // Validate reportees if provided
        List<String> reportees = user.getReportees();
        if (reportees != null && !reportees.isEmpty()) {
            List<String> invalidReportees = reportees.stream()
                .filter(reporteeId -> !userRepository.findById(reporteeId).isPresent())
                .toList();
            
            if (!invalidReportees.isEmpty()) {
                throw new UserValidationException("Following reportee IDs do not exist: " + String.join(", ", invalidReportees));
            }
        }

        // Save the user
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Initialize reportees list if null
        if (user.getReportees() == null) {
            user.setReportees(new ArrayList<>());
        }

        User savedUser = userRepository.save(user);

        // Prepare email placeholders
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("name", user.getName());
        placeholders.put("message", "Your account has been successfully created.");
        placeholders.put("wissenID", user.getWissenID());
        placeholders.put("actionUrl", "http://localhost:5173/login");

        // Send email using the template
        String subject = MessageFormat.format("Welcome to Our Platform, {0}!", user.getName());
        emailService.sendEmail(user.getEmail(), subject, "templates/emailTemplate.html", placeholders);

        return savedUser;
    }

    public Optional<User> getUserById(String wissenID) {
        return userRepository.findById(wissenID);
    }

    public List<User> getAllUsers() {
        return userRepository.findByRoleNot("ADMIN");
    }


    public User updateUser(String wissenID, User updatedUser) {
        return userRepository.findById(wissenID).map(user -> {
            // Check if new email already exists for a different user
            Optional<User> existingUserWithEmail = userRepository.findByEmail(updatedUser.getEmail());
            if (existingUserWithEmail.isPresent() && !existingUserWithEmail.get().getWissenID().equals(wissenID)) {
                throw new UserValidationException("Email " + updatedUser.getEmail() + " is already in use.");
            }

            // Validate new manager if being changed
            String oldManagerId = user.getManagerId();
            String newManagerId = updatedUser.getManagerId();
            boolean managerChanged = newManagerId != null && !newManagerId.equals(oldManagerId);

            if (managerChanged && !userRepository.findById(newManagerId).isPresent()) {
                throw new UserValidationException("Manager with ID " + newManagerId + " does not exist.");
            }

            // Validate reportees if provided
            List<String> newReportees = updatedUser.getReportees();
            if (newReportees != null && !newReportees.isEmpty()) {
                List<String> invalidReportees = newReportees.stream()
                    .filter(reporteeId -> !userRepository.findById(reporteeId).isPresent())
                    .toList();
                
                if (!invalidReportees.isEmpty()) {
                    throw new UserValidationException("Following reportee IDs do not exist: " + String.join(", ", invalidReportees));
                }
            }

            // Update user details
            user.setName(updatedUser.getName());
            user.setEmail(updatedUser.getEmail());
            user.setRole(updatedUser.getRole());
            user.setDateOfJoining(updatedUser.getDateOfJoining());
            user.setIsManager(updatedUser.getIsManager());
            user.setReportees(updatedUser.getReportees());

            // Handle password update if needed
            if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                if (!passwordEncoder.matches(updatedUser.getPassword(), user.getPassword())) {
                    user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
                }
            }

            // If manager is changed, update the reportees lists of both old and new managers
            if (managerChanged) {
                // Remove user from old manager's reportees list if old manager exists
                if (oldManagerId != null && !oldManagerId.isEmpty()) {
                    userRepository.findById(oldManagerId).ifPresent(oldManager -> {
                        List<String> oldManagerReportees = oldManager.getReportees();
                        if (oldManagerReportees != null) {
                            oldManagerReportees.remove(wissenID);
                            userRepository.save(oldManager);
                        }
                    });
                }

                // Add user to new manager's reportees list
                userRepository.findById(newManagerId).ifPresent(newManager -> {
                    List<String> newManagerReportees = newManager.getReportees();
                    if (newManagerReportees == null) {
                        newManagerReportees = new ArrayList<>();
                        newManager.setReportees(newManagerReportees);
                    }
                    if (!newManagerReportees.contains(wissenID)) {
                        newManagerReportees.add(wissenID);
                        userRepository.save(newManager);
                    }
                });

                // Update the user's managerId after handling reportees
                user.setManagerId(newManagerId);
            } else {
                // If manager hasn't changed, just set the managerId
                user.setManagerId(updatedUser.getManagerId());
            }

            return userRepository.save(user);
        }).orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + wissenID));
    }

    public User toggleUserActiveStatus(String wissenID) {
        User user = userRepository.findById(wissenID)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + wissenID));
        user.setActive(!user.isActive());  // Changed from setIsActive/getIsActive
        return userRepository.save(user);
    }

//    
}
