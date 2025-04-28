package com.example.demo.models;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testUserConstructorsAndGettersSetters() {
        // Test no-args constructor
        User user = new User();
        assertNotNull(user);

        // Test setters
        user.setWissenID("WCS171");
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setPassword("password123");
        user.setRole("USER");
        user.setDateOfJoining(LocalDate.now());
        user.setIsManager(true);
        user.setActive(true);
        user.setManagerId("MGR001");
        List<String> reportees = new ArrayList<>();
        reportees.add("WCS172");
        user.setReportees(reportees);

        // Test getters
        assertEquals("WCS171", user.getWissenID());
        assertEquals("John Doe", user.getName());
        assertEquals("john@example.com", user.getEmail());
        assertEquals("password123", user.getPassword());
        assertEquals("USER", user.getRole());
        assertEquals(LocalDate.now(), user.getDateOfJoining());
        assertTrue(user.getIsManager());
        assertTrue(user.isActive());
        assertEquals("MGR001", user.getManagerId());
        assertEquals(1, user.getReportees().size());
        assertEquals("WCS172", user.getReportees().get(0));
    }

    // @Test
    // void testUserEqualsAndHashCode() {
    //     User user1 = new User();
    //     user1.setWissenID("WCS171");
    //     user1.setEmail("john@example.com");

    //     User user2 = new User();
    //     user2.setWissenID("WCS171");
    //     user2.setEmail("john@example.com");

    //     User user3 = new User();
    //     user3.setWissenID("WCS172");
    //     user3.setEmail("jane@example.com");

    //     // Test equals
    //     assertEquals(user1, user2);
    //     assertNotEquals(user1, user3);

    //     // Test hashCode
    //     assertEquals(user1.hashCode(), user2.hashCode());
    //     assertNotEquals(user1.hashCode(), user3.hashCode());
    // }

    @Test
    void testUserAuthorities() {
        User user = new User();
        user.setRole("ADMIN");

        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        
        assertNotNull(authorities);
        assertEquals(1, authorities.size());
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ADMIN")));
    }

    @Test
    void testUserAuthoritiesWithNullRole() {
        User user = new User();
        user.setRole(null);

        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        
        assertNotNull(authorities);
        assertTrue(authorities.isEmpty());
    }

    @Test
    void testAllArgsConstructor() {
        List<String> reportees = new ArrayList<>();
        reportees.add("WCS172");
        
        User user = new User(
            "WCS171",
            "John Doe",
            "john@example.com",
            "password123",
            "USER",
            LocalDate.now(),
            null,
            "MGR001",
            reportees,
            true,
            true
        );

        assertNotNull(user);
        assertEquals("WCS171", user.getWissenID());
        assertEquals("John Doe", user.getName());
        assertEquals("john@example.com", user.getEmail());
        assertEquals("password123", user.getPassword());
        assertEquals("USER", user.getRole());
        assertTrue(user.getIsManager());
        assertTrue(user.isActive());
        assertEquals("MGR001", user.getManagerId());
        assertEquals(1, user.getReportees().size());
    }
}