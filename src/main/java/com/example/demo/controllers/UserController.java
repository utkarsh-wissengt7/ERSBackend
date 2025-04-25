package com.example.demo.controllers;

import com.example.demo.repositories.UserRepository;
import com.example.demo.dto.LoginRequest;
import com.example.demo.models.User;
import com.example.demo.services.UserService;
import com.example.demo.utils.JwtUtil;
import jakarta.mail.MessagingException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.example.demo.exceptions.ResourceNotFoundException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users/")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class UserController {

    @Autowired
    private UserRepository userRepository;
    private final UserService userService;




    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        try {
            User newUser = userService.createUser(user);
            return ResponseEntity.ok(newUser);
        } catch (IllegalArgumentException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } catch (MessagingException | IOException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Error creating user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{wissenID}")
    public ResponseEntity<User> getUserById(@PathVariable String wissenID) {
        return userService.getUserById(wissenID)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/getReporteeInfo")
    public ResponseEntity<Map<String, String>> getReporteeInfo(@RequestParam String reporteeWissenId) {
        return userService.getUserById(reporteeWissenId)
                .map(user -> ResponseEntity.ok(Map.of(
                        "wissenID", user.getWissenID(),
                        "name", user.getName(),
                        "email", user.getEmail()
                )))
                .orElse(ResponseEntity.notFound().build());
    }


    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {

            System.out.println("i am getting called in authentication ");

            // Authenticate the user
            System.out.println(loginRequest.getEmail());
            System.out.println(loginRequest.getPassword());
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );

            // Retrieve the user from the database
            Optional<User> userOptional = userRepository.findByEmail(loginRequest.getEmail());
            if (userOptional.isPresent()) {
                User user = userOptional.get();

                // Check if the user is active
                if (!user.isActive()) {
                    Map<String, String> response = new HashMap<>();
                    response.put("message", "Account is inactive. Please contact your administrator.");
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
                }

                // Generate JWT token
                String token = jwtUtil.generateToken(user.getEmail());

                // Return the token in the response
                Map<String, Object> response = new HashMap<>();
                response.put("token", token);
                response.put("name",user.getName());
                response.put("email",user.getEmail());
                response.put("wissenID" , user.getWissenID());
                response.put("role", user.getRole());
                response.put("isManager", String.valueOf(user.getIsManager()));
                response.put("reportees",user.getReportees());
                return ResponseEntity.ok(response);
            }

            // If user is not found
            Map<String, String> response = new HashMap<>();
            response.put("message", "Invalid credentials");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception e) {
            // Handle authentication failure
            System.out.println("Authentication failed: " + e.getMessage());
            Map<String, String> response = new HashMap<>();
            response.put("message", "Invalid email or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }


    @PutMapping("/{wissenID}")
    public ResponseEntity<User> updateUser(@PathVariable String wissenID, @RequestBody User user) {
        return ResponseEntity.ok(userService.updateUser(wissenID, user));
    }

    @PutMapping("/toggle-status/{wissenID}")
    public ResponseEntity<User> toggleUserStatus(@PathVariable String wissenID) {
        try {
            User updatedUser = userService.toggleUserActiveStatus(wissenID);
            return ResponseEntity.ok(updatedUser);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getAuthenticatedUser(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "User not authenticated"));
        }
        Map<String, Object> userDetails = Map.of(
                "name", principal.getAttribute("name"),
                "email", principal.getAttribute("email")
        );
        return ResponseEntity.ok(userDetails);
    }

    @PutMapping("/hash-passwords")
    public ResponseEntity<Map<String, String>> hashAllPasswords() {
        List<User> users = userRepository.findAll();
        int updatedCount = 0;

        for (User user : users) {
            String password = user.getPassword();
            // Check if the password is already hashed
            if (!password.startsWith("$2a$")) { // BCrypt hashed passwords start with "$2a$"
                user.setPassword(passwordEncoder.encode(password));
                userRepository.save(user);
                updatedCount++;
            }
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", "Password hashing completed");
        response.put("updatedCount", String.valueOf(updatedCount));
        return ResponseEntity.ok(response);
    }
}
