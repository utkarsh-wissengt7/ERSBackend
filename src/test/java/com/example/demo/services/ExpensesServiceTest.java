package com.example.demo.services;

import com.example.demo.models.Expenses;
import com.example.demo.models.User;
import com.example.demo.repositories.ExpensesRepository;
import com.example.demo.repositories.UserRepository;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ExpensesServiceTest {

    @Mock
    private ExpensesRepository expensesRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ExpensesService expensesService;

    private Expenses expense;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        user = new User();
        user.setWissenID("WCS171");
        user.setEmail("test@example.com");
        user.setName("Test User");
        
        expense = new Expenses();
        expense.setExpenseID(1L);
        expense.setUser(user);
        expense.setAmount(100.0);
        
        // Mock user repository to return the test user
        when(userRepository.findById(user.getWissenID())).thenReturn(Optional.of(user));
    }

    // @Test
    // void testCreateExpense() throws MessagingException, IOException {
    //     when(expensesRepository.save(any(Expenses.class))).thenReturn(expense);
    //     when(userRepository.findById(user.getWissenID())).thenReturn(Optional.of(user));

    //     Expenses result = expensesService.createExpense(expense);

    //     assertNotNull(result);
    //     assertEquals(1L, result.getExpenseID());
    //     verify(expensesRepository, times(1)).save(any(Expenses.class));
    //     verify(userRepository, times(1)).findById(user.getWissenID());
    // }

    // @Test
    // void testApproveExpense() throws MessagingException, IOException {
    //     when(expensesRepository.findById(1L)).thenReturn(Optional.of(expense));
    //     when(expensesRepository.save(any(Expenses.class))).thenReturn(expense);
    //     when(userRepository.findById(user.getWissenID())).thenReturn(Optional.of(user));

    //     Expenses result = expensesService.approveExpense(1L);

    //     assertEquals("APPROVED", result.getStatus());
    //     verify(expensesRepository, times(1)).save(any(Expenses.class));
    // }

    // @Test
    // void testRejectExpense() throws MessagingException, IOException {
    //     when(expensesRepository.findById(1L)).thenReturn(Optional.of(expense));
    //     when(expensesRepository.save(any(Expenses.class))).thenReturn(expense);
    //     when(userRepository.findById(user.getWissenID())).thenReturn(Optional.of(user));

    //     Expenses result = expensesService.rejectExpense(1L, "Invalid receipts");

    //     assertEquals("REJECTED", result.getStatus());
    //     assertEquals("Invalid receipts", result.getReasonForRejection());
    //     verify(expensesRepository, times(1)).save(any(Expenses.class));
    // }

    @Test
    void testGetById() {
        when(expensesRepository.findByIdAndUser_WissenID(1L, user.getWissenID()))
            .thenReturn(Optional.of(expense));
        when(userRepository.findById(user.getWissenID())).thenReturn(Optional.of(user));

        Optional<Expenses> result = expensesService.getExpenseByIdAndUserWissenID(1L, user.getWissenID());
        
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getExpenseID());
        assertEquals(user.getWissenID(), result.get().getUser().getWissenID());
    }

    @Test
    void testDeleteExpense() {
        doNothing().when(expensesRepository).deleteById(1L);

        expensesService.deleteExpense(1L);

        verify(expensesRepository, times(1)).deleteById(1L);
    }

    @Test
    void testUpdateExpense() {
        Expenses updatedExpense = new Expenses();
        updatedExpense.setAmount(200.0);

        when(expensesRepository.save(any(Expenses.class))).thenReturn(updatedExpense);

        Expenses result = expensesService.updateExpense(updatedExpense);

        assertEquals(200.0, result.getAmount());
        verify(expensesRepository, times(1)).save(any(Expenses.class));
    }

    @Test
    void testGetExpenses() {
        List<Expenses> expensesList = Arrays.asList(expense);
        when(expensesRepository.findByUser_wissenID(user.getWissenID())).thenReturn(expensesList);
        List<Expenses> result = expensesService.getExpensesByUserWissenID(user.getWissenID());
        assertEquals(1, result.size());
        verify(expensesRepository, times(1)).findByUser_wissenID(user.getWissenID());
    }

    @Test
    void testExpenseNotFound() {
        when(expensesRepository.findById(999L)).thenReturn(Optional.empty());
        Optional<Expenses> result = expensesService.getExpenseByIdAndUserWissenID(999L, user.getWissenID());
        assertFalse(result.isPresent());
    }
}