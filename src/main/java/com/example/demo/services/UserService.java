package com.example.demo.services;

import com.example.demo.models.User;
import com.example.demo.repositories.UserRepository;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import com.example.demo.exceptions.ResourceNotFoundException;
import org.springframework.web.bind.annotation.PutMapping;

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
        // Save the user
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getReportees() == null) {
            user.setReportees(new ArrayList<>());
        }
        user.setReportees(new ArrayList<>(user.getReportees()));
        System.out.println(user.getReportees());
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

    public Optional<User> authenticateUser(String email, String password) {
        return userRepository.findByEmail(email)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()));
    }

    public User updateUser(String wissenID, User updatedUser) {
        return userRepository.findById(wissenID).map(user -> {
            user.setName(updatedUser.getName());
            user.setEmail(updatedUser.getEmail());
            user.setRole(updatedUser.getRole());
            user.setDateOfJoining(updatedUser.getDateOfJoining());
            user.setManagerId(updatedUser.getManagerId());
            user.setIsManager(updatedUser.getIsManager());
            user.setReportees(updatedUser.getReportees());

            if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                if (!passwordEncoder.matches(updatedUser.getPassword(), user.getPassword())) {
                    user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
                }
            }

            return userRepository.save(user);
        }).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User toggleUserActiveStatus(String wissenID) {
        User user = userRepository.findById(wissenID)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + wissenID));
        user.setActive(!user.isActive());  // Changed from setIsActive/getIsActive
        return userRepository.save(user);
    }

    public User createOrUpdateOAuth2User(OAuth2User oAuth2User) {
        String email=oAuth2User.getAttribute("email");
        User user = userRepository.findByEmail(email).orElse(new User());

        user.setName(oAuth2User.getAttribute("name"));
        user.setEmail(email);
        user.setActive(true);
        user.setRole("USER");

        return userRepository.save(user);
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Fetch the user from the database
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // Convert the user to a UserDetails object
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.emptyList() // Replace with actual authorities if needed
        );
    }
}
