package com.example.demo.services;

import com.example.demo.models.User;
import com.example.demo.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(User user) {
//        user.setPassword(passwordEncoder.encode(user.getPassword())); // Encrypt password
        user.setPassword(user.getPassword());
        if (user.getReportees() == null) {
            user.setReportees(new ArrayList<>());
        }
        user.setReportees(new ArrayList<>(user.getReportees()));
        return userRepository.save(user);
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

            if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                if (!passwordEncoder.matches(updatedUser.getPassword(), user.getPassword())) {
                    user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
                }
            }

            return userRepository.save(user);
        }).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public void deleteUser(String wissenID) {
        userRepository.deleteById(wissenID);
    }
}
