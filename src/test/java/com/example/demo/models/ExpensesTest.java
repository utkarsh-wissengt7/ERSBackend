package com.example.demo.models;

import org.junit.jupiter.api.Test;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;

class ExpensesTest {

    @Test
    void testExpensesConstructorsAndGettersSetters() {
        // Test no-args constructor
        Expenses expense = new Expenses();
        assertNotNull(expense);

        // Create a user for testing
        User user = new User();
        user.setWissenID("WCS171");

        // Test setters
        expense.setExpenseID(1L);
        expense.setUser(user);
        expense.setWissenID("WCS171");
        expense.setDescription("Business lunch");
        expense.setAmount(50.0);
        expense.setCategory("Food");
        expense.setReceipt("receipt.pdf");
        expense.setStatus("PENDING");
        expense.setApprovedBy("MGR001");
        expense.setRejectedBy(null);
        expense.setReasonForRejection(null);
        Date createdAt = new Date();
        expense.setCreatedAt(createdAt);

        // Test getters
        assertEquals(1L, expense.getExpenseID());
        assertEquals(user, expense.getUser());
        assertEquals("WCS171", expense.getWissenID());
        assertEquals("Business lunch", expense.getDescription());
        assertEquals(50.0, expense.getAmount());
        assertEquals("Food", expense.getCategory());
        assertEquals("receipt.pdf", expense.getReceipt());
        assertEquals("PENDING", expense.getStatus());
        assertEquals("MGR001", expense.getApprovedBy());
        assertNull(expense.getRejectedBy());
        assertNull(expense.getReasonForRejection());
        assertEquals(createdAt, expense.getCreatedAt());
    }

    

    @Test
    void testGetId() {
        Expenses expense = new Expenses();
        expense.setExpenseID(1L);
        
        assertEquals(1L, expense.getId());
    }

    @Test
    void testSetId() {
        Expenses expense = new Expenses();
        expense.setId(1L);
        
        assertEquals(1L, expense.getExpenseID());
    }

    @Test
    void testAllArgsConstructor() {
        User user = new User();
        user.setWissenID("WCS171");
        Date createdAt = new Date();

        Expenses expense = new Expenses(
            1L,
            user,
            "WCS171",
            "Business lunch",
            50.0,
            "Food",
            "receipt.pdf",
            "PENDING",
            "MGR001",
            null,
            null,
            createdAt
        );

        assertNotNull(expense);
        assertEquals(1L, expense.getExpenseID());
        assertEquals(user, expense.getUser());
        assertEquals("WCS171", expense.getWissenID());
        assertEquals("Business lunch", expense.getDescription());
        assertEquals(50.0, expense.getAmount());
        assertEquals("Food", expense.getCategory());
        assertEquals("receipt.pdf", expense.getReceipt());
        assertEquals("PENDING", expense.getStatus());
        assertEquals("MGR001", expense.getApprovedBy());
        assertNull(expense.getRejectedBy());
        assertNull(expense.getReasonForRejection());
        assertEquals(createdAt, expense.getCreatedAt());
    }
}