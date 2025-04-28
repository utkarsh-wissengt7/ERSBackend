package com.example.demo.dto;

import org.junit.jupiter.api.Test;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import static org.junit.jupiter.api.Assertions.*;

class ExpenseRequestTest {
    
    private final Validator validator;
    
    ExpenseRequestTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidExpenseRequest() {
        ExpenseRequest request = new ExpenseRequest();
        request.setUserId("WCS171");
        request.setCategory("Travel");
        request.setAmount(100.0);
        request.setDescription("Business trip expenses");
        request.setReceipt("receipt.pdf");

        assertTrue(validator.validate(request).isEmpty());
        assertEquals("WCS171", request.getUserId());
        assertEquals("Travel", request.getCategory());
        assertEquals(100.0, request.getAmount());
        assertEquals("Business trip expenses", request.getDescription());
        assertEquals("receipt.pdf", request.getReceipt());
    }

    // @Test
    // void testExpenseRequestMissingRequiredFields() {
    //     ExpenseRequest request = new ExpenseRequest();
    //     assertFalse(validator.validate(request).isEmpty());
    // }

    // @Test
    // void testExpenseRequestInvalidAmount() {
    //     ExpenseRequest request = new ExpenseRequest();
    //     request.setUserId("WCS171");
    //     request.setCategory("Travel");
    //     request.setAmount(-100.0); // Negative amount
    //     request.setDescription("Business trip expenses");

    //     assertFalse(validator.validate(request).isEmpty());
    // }

    @Test
    void testExpenseRequestEquality() {
        ExpenseRequest request1 = new ExpenseRequest();
        request1.setUserId("WCS171");
        request1.setCategory("Travel");
        request1.setAmount(100.0);

        ExpenseRequest request2 = new ExpenseRequest();
        request2.setUserId("WCS171");
        request2.setCategory("Travel");
        request2.setAmount(100.0);

        ExpenseRequest request3 = new ExpenseRequest();
        request3.setUserId("WCS172");
        request3.setCategory("Food");
        request3.setAmount(50.0);

        assertEquals(request1, request2);
        assertNotEquals(request1, request3);
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotEquals(request1.hashCode(), request3.hashCode());
    }

    @Test
    void testExpenseRequestToString() {
        ExpenseRequest request = new ExpenseRequest();
        request.setUserId("WCS171");
        request.setCategory("Travel");
        request.setAmount(100.0);
        request.setDescription("Business trip");

        String toString = request.toString();
        assertTrue(toString.contains("WCS171"));
        assertTrue(toString.contains("Travel"));
        assertTrue(toString.contains("100.0"));
        assertTrue(toString.contains("Business trip"));
    }
}