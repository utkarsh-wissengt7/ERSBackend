package com.example.demo.config;

import com.example.demo.filters.JwtAuthenticationFilter;
import com.example.demo.utils.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.config.Customizer;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private SecurityConfig securityConfig;

    @Test
    void testPasswordEncoderBean() {
        // Act
        PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();

        // Assert
        assertNotNull(passwordEncoder);
        String encoded = passwordEncoder.encode("password");
        assertTrue(passwordEncoder.matches("password", encoded));
    }

    @Test
    void testSecurityFilterChain() throws Exception {
        // Arrange
        HttpSecurity httpSecurity = mock(HttpSecurity.class, RETURNS_SELF);
        DefaultSecurityFilterChain filterChain = mock(DefaultSecurityFilterChain.class);

        // Mock the builder pattern returns
        when(httpSecurity.csrf(any())).thenReturn(httpSecurity);
        when(httpSecurity.sessionManagement(any())).thenReturn(httpSecurity);
        when(httpSecurity.authorizeHttpRequests(any())).thenReturn(httpSecurity);
        when(httpSecurity.cors(any())).thenReturn(httpSecurity);
        when(httpSecurity.addFilterBefore(any(), any())).thenReturn(httpSecurity);
        when(httpSecurity.build()).thenReturn(filterChain);

        // Act
        SecurityFilterChain result = securityConfig.securityFilterChain(httpSecurity);

        // Assert
        assertNotNull(result);
        verify(httpSecurity).csrf(any());
        verify(httpSecurity).sessionManagement(any());
        verify(httpSecurity).cors(any());
        verify(httpSecurity).authorizeHttpRequests(any());
        verify(httpSecurity).addFilterBefore(any(JwtAuthenticationFilter.class), eq(UsernamePasswordAuthenticationFilter.class));
    }

    /**
     * This test documents and validates our decision to disable CSRF.
     * CSRF is disabled because:
     * 1. We use JWT tokens for authentication (stateless)
     * 2. All state-changing operations require a valid JWT token
     * 3. We don't use cookies for session management
     * 4. Our API is designed to be consumed by a SPA frontend
     */
    @Test
    void testCsrfIsDisabledForStatelessApi() throws Exception {
        // Arrange
        HttpSecurity httpSecurity = mock(HttpSecurity.class, RETURNS_SELF);
        DefaultSecurityFilterChain filterChain = mock(DefaultSecurityFilterChain.class);

        when(httpSecurity.csrf(any())).thenReturn(httpSecurity);
        when(httpSecurity.sessionManagement(any())).thenReturn(httpSecurity);
        when(httpSecurity.authorizeHttpRequests(any())).thenReturn(httpSecurity);
        when(httpSecurity.cors(any())).thenReturn(httpSecurity);
        when(httpSecurity.build()).thenReturn(filterChain);

        // Act
        securityConfig.securityFilterChain(httpSecurity);

        // Assert
        verify(httpSecurity).csrf(any());
        verify(httpSecurity).sessionManagement(any());
        verify(httpSecurity, never()).addFilter(any(CsrfFilter.class));
    }

    @Test
    void testSessionManagementIsStateless() throws Exception {
        // Arrange
        HttpSecurity httpSecurity = mock(HttpSecurity.class, RETURNS_SELF);
        DefaultSecurityFilterChain filterChain = mock(DefaultSecurityFilterChain.class);

        when(httpSecurity.csrf(any())).thenReturn(httpSecurity);
        when(httpSecurity.sessionManagement(any())).thenReturn(httpSecurity);
        when(httpSecurity.authorizeHttpRequests(any())).thenReturn(httpSecurity);
        when(httpSecurity.cors(any())).thenReturn(httpSecurity);
        when(httpSecurity.build()).thenReturn(filterChain);

        // Act
        securityConfig.securityFilterChain(httpSecurity);

        // Assert
        verify(httpSecurity).sessionManagement(any());
    }
}