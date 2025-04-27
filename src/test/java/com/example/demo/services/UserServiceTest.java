package com.example.demo.services;

import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.models.User;
import com.example.demo.repositories.UserRepository;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateUser() throws MessagingException, IOException {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setName("Test User");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User createdUser = userService.createUser(user);

        assertNotNull(createdUser);
        assertEquals("test@example.com", createdUser.getEmail());
        verify(emailService, times(1)).sendEmail(eq("test@example.com"), anyString(), anyString(), anyMap());
    }

    @Test
    void testCreateUser_EmailAlreadyExists() {
        User user = new User();
        user.setEmail("test@example.com");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(user);
        });

        assertEquals("Email already exists.", exception.getMessage());
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    void testGetUserById() {
        User user = new User();
        user.setWissenID("WCS171");

        when(userRepository.findById("WCS171")).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserById("WCS171");

        assertTrue(result.isPresent());
        assertEquals("WCS171", result.get().getWissenID());
    }

    @Test
    void testGetAllUsers() {
        List<User> users = Arrays.asList(new User(), new User());
        when(userRepository.findByRoleNot("ADMIN")).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userRepository, times(1)).findByRoleNot("ADMIN");
    }

    @Test
    void testUpdateUser_ManagerChanged() {
        // Arrange: Set up existing user, old manager, and new manager
        User existingUser = new User();
        existingUser.setWissenID("WCS171");
        existingUser.setManagerId("MGR001");

        User updatedUser = new User();
        updatedUser.setManagerId("MGR002");

        User oldManager = new User();
        oldManager.setWissenID("MGR001");
        oldManager.setReportees(new ArrayList<>(List.of("WCS171")));

        User newManager = new User();
        newManager.setWissenID("MGR002");
        newManager.setReportees(new ArrayList<>());

        // Mock repository behavior
        when(userRepository.findById("WCS171")).thenReturn(Optional.of(existingUser));
        when(userRepository.findById("MGR001")).thenReturn(Optional.of(oldManager));
        when(userRepository.findById("MGR002")).thenReturn(Optional.of(newManager));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act: Call the updateUser method
        User result = userService.updateUser("WCS171", updatedUser);

        // Assert: Verify the user and managers were updated correctly
        assertNotNull(result);
        assertEquals("MGR002", result.getManagerId());
        assertFalse(oldManager.getReportees().contains("WCS171"));
        assertTrue(newManager.getReportees().contains("WCS171"));

        // Verify save calls (3 times: old manager, new manager, updated user)
        verify(userRepository, times(3)).save(any(User.class));
    }

    @Test
    void testUpdateUser_ManagerNotChanged() {
        User existingUser = new User();
        existingUser.setWissenID("WCS171");
        existingUser.setManagerId("MGR001");

        User updatedUser = new User();
        updatedUser.setManagerId("MGR001");

        when(userRepository.findById("WCS171")).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.updateUser("WCS171", updatedUser);

        assertNotNull(result);
        assertEquals("MGR001", result.getManagerId());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testUpdateUser_UserNotFound() {
        when(userRepository.findById("WCS999")).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.updateUser("WCS999", new User());
        });

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findById("WCS999");
    }

    @Test
    void testUpdateUser_PasswordUpdated() {
        User existingUser = new User();
        existingUser.setWissenID("WCS171");
        existingUser.setPassword("oldEncodedPassword");

        User updatedUser = new User();
        updatedUser.setPassword("newPassword");

        when(userRepository.findById("WCS171")).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("newPassword", "oldEncodedPassword")).thenReturn(false);
        when(passwordEncoder.encode("newPassword")).thenReturn("newEncodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.updateUser("WCS171", updatedUser);

        assertNotNull(result);
        assertEquals("newEncodedPassword", result.getPassword());
        verify(passwordEncoder, times(1)).encode("newPassword");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testToggleUserActiveStatus() {
        User user = new User();
        user.setWissenID("WCS171");
        user.setActive(false);

        when(userRepository.findById("WCS171")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.toggleUserActiveStatus("WCS171");

        assertNotNull(result);
        assertTrue(result.isActive());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testToggleUserActiveStatus_UserNotFound() {
        when(userRepository.findById("WCS999")).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            userService.toggleUserActiveStatus("WCS999");
        });

        assertEquals("User not found with ID: WCS999", exception.getMessage());
        verify(userRepository, times(1)).findById("WCS999");
    }
}