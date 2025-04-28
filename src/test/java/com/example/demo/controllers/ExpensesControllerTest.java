package com.example.demo.controllers;

import com.example.demo.dto.ExpenseRequest;
import com.example.demo.models.Expenses;
import com.example.demo.models.User;
import com.example.demo.services.ExpensesService;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExpensesControllerTest {

    @Mock
    private ExpensesService expensesService;

    @InjectMocks
    private ExpensesController expensesController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllExpensesByUserWissenID() {
        List<Expenses> expenses = Arrays.asList(new Expenses(), new Expenses());
        when(expensesService.getExpensesByUserWissenID("WCS171")).thenReturn(expenses);

        List<Expenses> response = expensesController.getAllExpensesByUserWissenID("WCS171");

        assertEquals(2, response.size());
        verify(expensesService, times(1)).getExpensesByUserWissenID("WCS171");
    }

    @Test
    void testGetFirstExpenseByUserWissenID_Success() {
        Expenses expense = new Expenses();
        when(expensesService.getFirstExpenseByUserWissenID("WCS171")).thenReturn(Optional.of(expense));

        ResponseEntity<Expenses> response = expensesController.getFirstExpenseByUserWissenID("WCS171");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expense, response.getBody());
    }

    @Test
    void testGetFirstExpenseByUserWissenID_NotFound() {
        when(expensesService.getFirstExpenseByUserWissenID("WCS171")).thenReturn(Optional.empty());

        ResponseEntity<Expenses> response = expensesController.getFirstExpenseByUserWissenID("WCS171");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetExpenseByIdAndUserWissenID_Success() {
        Expenses expense = new Expenses();
        when(expensesService.getExpenseByIdAndUserWissenID(1L, "WCS171")).thenReturn(Optional.of(expense));

        ResponseEntity<Expenses> response = expensesController.getExpenseByIdAndUserWissenID(1L, "WCS171");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expense, response.getBody());
    }

    @Test
    void testGetExpenseByIdAndUserWissenID_NotFound() {
        when(expensesService.getExpenseByIdAndUserWissenID(1L, "WCS171")).thenReturn(Optional.empty());

        ResponseEntity<Expenses> response = expensesController.getExpenseByIdAndUserWissenID(1L, "WCS171");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testCheckExpenseExists() {
        when(expensesService.existsByIdAndUserWissenID(1L, "WCS171")).thenReturn(true);

        boolean response = expensesController.checkExpenseExists(1L, "WCS171");

        assertTrue(response);
        verify(expensesService, times(1)).existsByIdAndUserWissenID(1L, "WCS171");
    }

    @Test
    void testGetExpensesByUserWissenIDAndStatus() {
        List<Expenses> expenses = Arrays.asList(new Expenses(), new Expenses());
        when(expensesService.getExpensesByUserWissenIDAndStatus("WCS171", "PENDING")).thenReturn(expenses);

        List<Expenses> response = expensesController.getExpensesByUserWissenIDAndStatus("WCS171", "PENDING");

        assertEquals(2, response.size());
        verify(expensesService, times(1)).getExpensesByUserWissenIDAndStatus("WCS171", "PENDING");
    }

    @Test
    void testCreateExpense_Success() throws MessagingException, IOException {
        ExpenseRequest request = new ExpenseRequest();
        request.setUserId("WCS171");
        request.setCategory("Travel");
        request.setAmount(100.0);
        request.setDescription("Business trip");
        
        Expenses expense = new Expenses();
        when(expensesService.createExpense(any(Expenses.class))).thenReturn(expense);

        ResponseEntity<Expenses> response = expensesController.createExpense(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expense, response.getBody());
    }

    @Test
    void testCreateExpense_Exception() throws MessagingException, IOException {
        ExpenseRequest request = new ExpenseRequest();
        request.setUserId("WCS171");
        request.setCategory("Travel");
        request.setAmount(100.0);
        request.setDescription("Business trip");

        when(expensesService.createExpense(any(Expenses.class))).thenThrow(new RuntimeException("Error"));

        ResponseEntity<Expenses> response = expensesController.createExpense(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testUpdateExpense_Success() {
        ExpenseRequest request = new ExpenseRequest();
        request.setCategory("Food");
        request.setAmount(50.0);
        request.setDescription("Lunch");

        Expenses existingExpense = new Expenses();
        when(expensesService.getExpenseByIdAndUserWissenID(1L, "WCS171")).thenReturn(Optional.of(existingExpense));
        when(expensesService.updateExpense(any(Expenses.class))).thenReturn(existingExpense);

        ResponseEntity<Expenses> response = expensesController.updateExpense(1L, "WCS171", request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(existingExpense, response.getBody());
    }

    @Test
    void testUpdateExpense_NotFound() {
        ExpenseRequest request = new ExpenseRequest();
        when(expensesService.getExpenseByIdAndUserWissenID(1L, "WCS171")).thenReturn(Optional.empty());

        ResponseEntity<Expenses> response = expensesController.updateExpense(1L, "WCS171", request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testUpdateExpenseStatus_Approve() throws MessagingException, IOException {
        Expenses expense = new Expenses();
        when(expensesService.approveExpense(1L)).thenReturn(expense);

        ResponseEntity<Expenses> response = expensesController.updateExpenseStatus(1L, "approve", "WCS171", "APPROVED", null, null, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expense, response.getBody());
    }

    @Test
    void testUpdateExpenseStatus_Reject() throws MessagingException, IOException {
        Expenses expense = new Expenses();
        when(expensesService.rejectExpense(1L, "Not valid")).thenReturn(expense);

        ResponseEntity<Expenses> response = expensesController.updateExpenseStatus(1L, "reject", "WCS171", "REJECTED", null, null, "Not valid");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expense, response.getBody());
    }

    @Test
    void testUpdateExpenseStatus_BadRequest() {
        ResponseEntity<Expenses> response = expensesController.updateExpenseStatus(1L, "invalid", "WCS171", "INVALID", null, null, null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testDeleteExpense_Success() {
        Expenses expense = new Expenses();
        when(expensesService.getExpenseByIdAndUserWissenID(1L, "WCS171")).thenReturn(Optional.of(expense));
        doNothing().when(expensesService).deleteExpense(1L);

        ResponseEntity<Void> response = expensesController.deleteExpense(1L, "WCS171");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(expensesService, times(1)).deleteExpense(1L);
    }

    @Test
    void testDeleteExpense_NotFound() {
        when(expensesService.getExpenseByIdAndUserWissenID(1L, "WCS171")).thenReturn(Optional.empty());

        ResponseEntity<Void> response = expensesController.deleteExpense(1L, "WCS171");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}