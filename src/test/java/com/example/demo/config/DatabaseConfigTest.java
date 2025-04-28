package com.example.demo.config;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DatabaseConfigTest {

    @Test
    void testDatabaseConfig() {
        // Arrange & Act
        DatabaseConfig config = new DatabaseConfig();

        // Assert
        assertNotNull(config);
    }
}