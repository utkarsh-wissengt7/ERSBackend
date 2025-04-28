package com.example.demo.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ExpenseDetailsTest {

    @Test
    void testExpenseDetailsSettersAndGetters() {
        // Arrange
        ExpenseDetails details = new ExpenseDetails();

        // Act
        details.setCategory("Travel");
        details.setAmount(150.0);
        details.setDescription("Business trip expenses");
        details.setReceipt("receipt123.pdf");

        // Assert
        assertEquals("Travel", details.getCategory());
        assertEquals(150.0, details.getAmount());
        assertEquals("Business trip expenses", details.getDescription());
        assertEquals("receipt123.pdf", details.getReceipt());
    }

    @Test
    void testExpenseDetailsEqualsAndHashCode() {
        // Arrange
        ExpenseDetails details1 = new ExpenseDetails();
        details1.setCategory("Food");
        details1.setAmount(50.0);
        details1.setDescription("Lunch");

        ExpenseDetails details2 = new ExpenseDetails();
        details2.setCategory("Food");
        details2.setAmount(50.0);
        details2.setDescription("Lunch");

        ExpenseDetails details3 = new ExpenseDetails();
        details3.setCategory("Travel");
        details3.setAmount(100.0);
        details3.setDescription("Taxi");

        // Assert
        assertEquals(details1, details2);
        assertNotEquals(details1, details3);
        assertEquals(details1.hashCode(), details2.hashCode());
        assertNotEquals(details1.hashCode(), details3.hashCode());
    }

    @Test
    void testExpenseDetailsToString() {
        // Arrange
        ExpenseDetails details = new ExpenseDetails();
        details.setCategory("Office");
        details.setAmount(75.0);
        details.setDescription("Supplies");
        details.setReceipt("office_supplies.pdf");

        // Act
        String toString = details.toString();

        // Assert
        assertTrue(toString.contains("Office"));
        assertTrue(toString.contains("75.0"));
        assertTrue(toString.contains("Supplies"));
        assertTrue(toString.contains("office_supplies.pdf"));
    }

    @Test
    void testExpenseDetailsNoArgsConstructor() {
        // Act
        ExpenseDetails details = new ExpenseDetails();

        // Assert
        assertNotNull(details);
        assertNull(details.getCategory());
        assertNull(details.getAmount());
        assertNull(details.getDescription());
        assertNull(details.getReceipt());
    }
}