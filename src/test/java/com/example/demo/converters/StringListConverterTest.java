package com.example.demo.converters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StringListConverterTest {

    private StringListConverter converter;

    @BeforeEach
    void setUp() {
        converter = new StringListConverter();
    }

    @Test
    void testConvertToDatabaseColumn_ValidList() {
        // Arrange
        List<String> attribute = Arrays.asList("item1", "item2", "item3");

        // Act
        String result = converter.convertToDatabaseColumn(attribute);

        // Assert
        assertTrue(result.contains("item1"));
        assertTrue(result.contains("item2"));
        assertTrue(result.contains("item3"));
    }

    @Test
    void testConvertToDatabaseColumn_EmptyList() {
        // Arrange
        List<String> attribute = Arrays.asList();

        // Act
        String result = converter.convertToDatabaseColumn(attribute);

        // Assert
        assertEquals("[]", result);
    }


    @Test
    void testConvertToEntityAttribute_ValidJson() {
        // Arrange
        String dbData = "[\"item1\",\"item2\",\"item3\"]";

        // Act
        List<String> result = converter.convertToEntityAttribute(dbData);

        // Assert
        assertEquals(3, result.size());
        assertTrue(result.contains("item1"));
        assertTrue(result.contains("item2"));
        assertTrue(result.contains("item3"));
    }

    @Test
    void testConvertToEntityAttribute_EmptyJson() {
        // Arrange
        String dbData = "[]";

        // Act
        List<String> result = converter.convertToEntityAttribute(dbData);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void testConvertToEntityAttribute_InvalidJson() {
        // Arrange
        String dbData = "invalid json";

        // Act
        List<String> result = converter.convertToEntityAttribute(dbData);

        // Assert
        assertTrue(result.isEmpty());
    }

}