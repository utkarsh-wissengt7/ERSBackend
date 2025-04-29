package com.example.demo.services;

import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.exceptions.UserValidationException;
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

class UserServiceTest {
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

        Exception exception = assertThrows(UserValidationException.class, () -> {
            userService.createUser(user);
        });

        assertEquals("Email test@example.com already exists.", exception.getMessage());
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

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            userService.updateUser("WCS999", new User());
        });

        assertEquals("User not found with ID: WCS999", exception.getMessage());
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

    @Test
    void testCreateUser_WissenIdAlreadyExists() {
        User user = new User();
        user.setWissenID("WCS171");
        
        when(userRepository.findById("WCS171")).thenReturn(Optional.of(new User()));

        Exception exception = assertThrows(UserValidationException.class, () -> {
            userService.createUser(user);
        });

        assertEquals("WissenID WCS171 already exists.", exception.getMessage());
        verify(userRepository, times(1)).findById("WCS171");
    }

    @Test
    void testCreateUser_WithValidManager() throws MessagingException, IOException {
        User user = new User();
        user.setWissenID("WCS171");
        user.setManagerId("MGR001");
        
        User manager = new User();
        manager.setWissenID("MGR001");
        manager.setReportees(new ArrayList<>());

        when(userRepository.findById("WCS171")).thenReturn(Optional.empty());
        when(userRepository.findById("MGR001")).thenReturn(Optional.of(manager));
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.createUser(user);

        assertTrue(manager.getReportees().contains("WCS171"));
        verify(userRepository, times(1)).save(manager);
    }

    @Test
    void testCreateUser_WithInvalidManager() {
        User user = new User();
        user.setWissenID("WCS171");
        user.setManagerId("INVALID");

        when(userRepository.findById("WCS171")).thenReturn(Optional.empty());
        when(userRepository.findById("INVALID")).thenReturn(Optional.empty());

        Exception exception = assertThrows(UserValidationException.class, () -> {
            userService.createUser(user);
        });

        assertEquals("Manager with ID INVALID does not exist.", exception.getMessage());
    }

    @Test
    void testCreateUser_WithInvalidReportees() {
        User user = new User();
        user.setWissenID("WCS171");
        List<String> reportees = Arrays.asList("WCS999", "WCS888");
        user.setReportees(reportees);

        when(userRepository.findById("WCS171")).thenReturn(Optional.empty());
        when(userRepository.findById("WCS999")).thenReturn(Optional.empty());
        when(userRepository.findById("WCS888")).thenReturn(Optional.empty());

        Exception exception = assertThrows(UserValidationException.class, () -> {
            userService.createUser(user);
        });

        assertEquals("Following reportee IDs do not exist: WCS999, WCS888", exception.getMessage());
    }

    @Test
    void testCreateUser_NullReportees() throws MessagingException, IOException {
        User user = new User();
        user.setWissenID("WCS171");
        user.setEmail("test@example.com");
        user.setReportees(null);

        when(userRepository.findById("WCS171")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.createUser(user);
        
        assertNotNull(result.getReportees());
        assertTrue(result.getReportees().isEmpty());
    }

    @Test
    void testUpdateUser_DuplicateEmail() {
        User existingUser = new User();
        existingUser.setWissenID("WCS171");
        existingUser.setEmail("existing@example.com");

        User otherUser = new User();
        otherUser.setWissenID("WCS172");
        otherUser.setEmail("other@example.com");

        User updatedUser = new User();
        updatedUser.setEmail("other@example.com");

        when(userRepository.findById("WCS171")).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail("other@example.com")).thenReturn(Optional.of(otherUser));

        Exception exception = assertThrows(UserValidationException.class, () -> {
            userService.updateUser("WCS171", updatedUser);
        });

        assertEquals("Email other@example.com is already in use.", exception.getMessage());
    }

    @Test
    void testUpdateUser_WithInvalidReportees() {
        User existingUser = new User();
        existingUser.setWissenID("WCS171");

        User updatedUser = new User();
        updatedUser.setReportees(Arrays.asList("INVALID1", "INVALID2"));

        when(userRepository.findById("WCS171")).thenReturn(Optional.of(existingUser));
        when(userRepository.findById("INVALID1")).thenReturn(Optional.empty());
        when(userRepository.findById("INVALID2")).thenReturn(Optional.empty());

        Exception exception = assertThrows(UserValidationException.class, () -> {
            userService.updateUser("WCS171", updatedUser);
        });

        assertEquals("Following reportee IDs do not exist: INVALID1, INVALID2", exception.getMessage());
    }

    @Test
    void testUpdateUser_WithNewManager_ManagerNotFound() {
        User existingUser = new User();
        existingUser.setWissenID("WCS171");
        existingUser.setManagerId("MGR001");

        User updatedUser = new User();
        updatedUser.setManagerId("INVALID");

        when(userRepository.findById("WCS171")).thenReturn(Optional.of(existingUser));
        when(userRepository.findById("INVALID")).thenReturn(Optional.empty());

        Exception exception = assertThrows(UserValidationException.class, () -> {
            userService.updateUser("WCS171", updatedUser);
        });

        assertEquals("Manager with ID INVALID does not exist.", exception.getMessage());
    }

    @Test
    void testUpdateUser_FullManagerTransition() {
        // Setup existing user and managers
        User existingUser = new User();
        existingUser.setWissenID("WCS171");
        existingUser.setManagerId("MGR001");

        User oldManager = new User();
        oldManager.setWissenID("MGR001");
        oldManager.setReportees(new ArrayList<>(Arrays.asList("WCS171", "WCS172")));

        User newManager = new User();
        newManager.setWissenID("MGR002");
        newManager.setReportees(new ArrayList<>());

        User updatedUser = new User();
        updatedUser.setManagerId("MGR002");

        when(userRepository.findById("WCS171")).thenReturn(Optional.of(existingUser));
        when(userRepository.findById("MGR001")).thenReturn(Optional.of(oldManager));
        when(userRepository.findById("MGR002")).thenReturn(Optional.of(newManager));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User result = userService.updateUser("WCS171", updatedUser);

        assertEquals("MGR002", result.getManagerId());
        assertFalse(oldManager.getReportees().contains("WCS171"));
        assertTrue(newManager.getReportees().contains("WCS171"));
        verify(userRepository, times(3)).save(any(User.class));
    }

    @Test
    void testUpdateUser_InitializeNewManagerReportees() {
        User existingUser = new User();
        existingUser.setWissenID("WCS171");

        User newManager = new User();
        newManager.setWissenID("MGR002");
        newManager.setReportees(null);

        User updatedUser = new User();
        updatedUser.setManagerId("MGR002");

        when(userRepository.findById("WCS171")).thenReturn(Optional.of(existingUser));
        when(userRepository.findById("MGR002")).thenReturn(Optional.of(newManager));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User result = userService.updateUser("WCS171", updatedUser);

        assertEquals("MGR002", result.getManagerId());
        assertNotNull(newManager.getReportees());
        assertTrue(newManager.getReportees().contains("WCS171"));
    }

    @Test
    void testToggleUserActiveStatus_Inactive() {
        User user = new User();
        user.setWissenID("WCS171");
        user.setActive(true);

        when(userRepository.findById("WCS171")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.toggleUserActiveStatus("WCS171");

        assertFalse(result.isActive());
        verify(userRepository).save(user);
    }
}