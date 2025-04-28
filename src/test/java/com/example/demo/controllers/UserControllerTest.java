package com.example.demo.controllers;

import com.example.demo.dto.LoginRequest;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.models.User;
import com.example.demo.repositories.UserRepository;
import com.example.demo.services.UserService;
import com.example.demo.utils.JwtUtil;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateUser_Success() throws MessagingException, IOException {
        User user = new User();
        when(userService.createUser(user)).thenReturn(user);

        ResponseEntity<?> response = userController.createUser(user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    @Test
    void testCreateUser_Conflict() throws MessagingException, IOException {
        User user = new User();
        when(userService.createUser(user)).thenThrow(new IllegalArgumentException("User already exists"));

        ResponseEntity<?> response = userController.createUser(user);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("User already exists", ((Map<?, ?>) response.getBody()).get("error"));
    }

    @Test
    void testCreateUser_MessagingException() throws MessagingException, IOException {
        User user = new User();
        when(userService.createUser(user)).thenThrow(new MessagingException("Email error"));

        ResponseEntity<?> response = userController.createUser(user);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error creating user: Email error", ((Map<?, ?>) response.getBody()).get("error"));
    }

    @Test
    void testCreateUser_IOException() throws MessagingException, IOException {
        User user = new User();
        when(userService.createUser(user)).thenThrow(new IOException("IO error"));

        ResponseEntity<?> response = userController.createUser(user);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error creating user: IO error", ((Map<?, ?>) response.getBody()).get("error"));
    }


    @Test
    void testAuthenticateUser_InvalidCredentials() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("test@example.com", "wrongPassword");

        // Mock authentication failure
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Invalid credentials"));

        // Act
        ResponseEntity<?> response = userController.authenticateUser(loginRequest);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        Map<?, ?> responseBody = (Map<?, ?>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("Invalid email or password", responseBody.get("message"));
    }

    @Test
    void testAuthenticateUser_Exception() {
        LoginRequest loginRequest = new LoginRequest("test@example.com", "password");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Authentication failed"));

        ResponseEntity<?> response = userController.authenticateUser(loginRequest);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid email or password", ((Map<?, ?>) response.getBody()).get("message"));
    }

    @Test
    void testGetUserById_Success() {
        User user = new User();
        when(userService.getUserById("WCS171")).thenReturn(Optional.of(user));

        ResponseEntity<User> response = userController.getUserById("WCS171");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    @Test
    void testGetUserById_NotFound() {
        when(userService.getUserById("WCS999")).thenReturn(Optional.empty());

        ResponseEntity<User> response = userController.getUserById("WCS999");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetAllUsers() {
        List<User> users = Arrays.asList(new User(), new User());
        when(userService.getAllUsers()).thenReturn(users);

        ResponseEntity<List<User>> response = userController.getAllUsers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void testUpdateUser() {
        User user = new User();
        when(userService.updateUser("WCS171", user)).thenReturn(user);

        ResponseEntity<User> response = userController.updateUser("WCS171", user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    @Test
    void testToggleUserStatus_Success() {
        User user = new User();
        when(userService.toggleUserActiveStatus("WCS171")).thenReturn(user);

        ResponseEntity<User> response = userController.toggleUserStatus("WCS171");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    @Test
    void testToggleUserStatus_NotFound() {
        when(userService.toggleUserActiveStatus("WCS999")).thenThrow(new ResourceNotFoundException("User not found"));

        ResponseEntity<User> response = userController.toggleUserStatus("WCS999");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testToggleUserStatus_Exception() {
        when(userService.toggleUserActiveStatus("WCS999")).thenThrow(new RuntimeException("Unexpected error"));

        ResponseEntity<User> response = userController.toggleUserStatus("WCS999");

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void testGetReporteeInfo_Success() {
        User user = new User();
        user.setWissenID("WCS171");
        user.setName("Test User");
        user.setEmail("test@example.com");

        when(userService.getUserById("WCS171")).thenReturn(Optional.of(user));

        ResponseEntity<Map<String, String>> response = userController.getReporteeInfo("WCS171");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("WCS171", response.getBody().get("wissenID"));
        assertEquals("Test User", response.getBody().get("name"));
        assertEquals("test@example.com", response.getBody().get("email"));
    }

    @Test
    void testGetReporteeInfo_NotFound() {
        when(userService.getUserById("WCS999")).thenReturn(Optional.empty());

        ResponseEntity<Map<String, String>> response = userController.getReporteeInfo("WCS999");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetAuthenticatedUser_Success() {
        OAuth2User principal = mock(OAuth2User.class);
        when(principal.getAttribute("name")).thenReturn("Test User");
        when(principal.getAttribute("email")).thenReturn("test@example.com");

        ResponseEntity<Map<String, Object>> response = userController.getAuthenticatedUser(principal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Test User", response.getBody().get("name"));
        assertEquals("test@example.com", response.getBody().get("email"));
    }

    @Test
    void testGetAuthenticatedUser_Unauthenticated() {
        ResponseEntity<Map<String, Object>> response = userController.getAuthenticatedUser(null);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("User not authenticated", response.getBody().get("error"));
    }
}
