package com.example.demo.controllers;

import com.example.demo.repositories.UserRepository;
import com.example.demo.dto.LoginRequest;
import com.example.demo.models.User;
import com.example.demo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users/")
public class UserController {

    @Autowired  // ✅ Injecting the repository
    private UserRepository userRepository;
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.createUser(user));
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
        Optional<User> userOptional = userRepository.findByEmailAndPassword(
                loginRequest.getEmail(), loginRequest.getPassword()
        );

        if (userOptional.isPresent()) {
            return ResponseEntity.ok(userOptional.get());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }


    @PutMapping("/{wissenID}")
    public ResponseEntity<User> updateUser(@PathVariable String wissenID, @RequestBody User user) {
        return ResponseEntity.ok(userService.updateUser(wissenID, user));
    }

    @DeleteMapping("/{wissenID}")
    public ResponseEntity<Void> deleteUser(@PathVariable String wissenID) {
        userService.deleteUser(wissenID);
        return ResponseEntity.noContent().build();
    }
}
