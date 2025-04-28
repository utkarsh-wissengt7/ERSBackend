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

    @Mock
    private EmailService emailService;

    @InjectMocks
    private ExpensesService expensesService;

    private User employee;
    private User manager;
    private Expenses expense;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup employee
        employee = new User();
        employee.setWissenID("WCS171");
        employee.setEmail("employee@test.com");
        employee.setName("Test Employee");

        // Setup manager
        manager = new User();
        manager.setWissenID("MGR001");
        manager.setEmail("manager@test.com");
        manager.setName("Test Manager");

        // Set manager for employee
        employee.setManager(manager);

        // Setup expense
        expense = new Expenses();
        expense.setExpenseID(1L);
        expense.setUser(employee);
        expense.setCategory("Travel");
        expense.setAmount(100.0);
        expense.setStatus("PENDING");
    }

    @Test
    void testGetFirstExpenseByUserWissenID_Success() {
        when(expensesRepository.findFirstByUser_WissenID("WCS171"))
            .thenReturn(Optional.of(expense));

        Optional<Expenses> result = expensesService.getFirstExpenseByUserWissenID("WCS171");

        assertTrue(result.isPresent());
        assertEquals(expense.getExpenseID(), result.get().getExpenseID());
        verify(expensesRepository).findFirstByUser_WissenID("WCS171");
    }

    @Test
    void testGetFirstExpenseByUserWissenID_NotFound() {
        when(expensesRepository.findFirstByUser_WissenID("WCS999"))
            .thenReturn(Optional.empty());

        Optional<Expenses> result = expensesService.getFirstExpenseByUserWissenID("WCS999");

        assertFalse(result.isPresent());
        verify(expensesRepository).findFirstByUser_WissenID("WCS999");
    }

    @Test
    void testExistsByIdAndUserWissenID_Exists() {
        when(expensesRepository.existsByIdAndUser_WissenID(1L, "WCS171"))
            .thenReturn(true);

        boolean result = expensesService.existsByIdAndUserWissenID(1L, "WCS171");

        assertTrue(result);
        verify(expensesRepository).existsByIdAndUser_WissenID(1L, "WCS171");
    }

    @Test
    void testExistsByIdAndUserWissenID_DoesNotExist() {
        when(expensesRepository.existsByIdAndUser_WissenID(999L, "WCS171"))
            .thenReturn(false);

        boolean result = expensesService.existsByIdAndUserWissenID(999L, "WCS171");

        assertFalse(result);
        verify(expensesRepository).existsByIdAndUser_WissenID(999L, "WCS171");
    }

    @Test
    void testGetExpensesByUserWissenIDAndStatus() {
        List<Expenses> expectedExpenses = Arrays.asList(expense);
        when(expensesRepository.findByUser_WissenIDAndStatus("WCS171", "PENDING"))
            .thenReturn(expectedExpenses);

        List<Expenses> result = expensesService.getExpensesByUserWissenIDAndStatus("WCS171", "PENDING");

        assertEquals(1, result.size());
        assertEquals(expense.getExpenseID(), result.get(0).getExpenseID());
        verify(expensesRepository).findByUser_WissenIDAndStatus("WCS171", "PENDING");
    }

    @Test
    void testCreateExpense_Success() throws MessagingException, IOException {
        when(expensesRepository.save(any(Expenses.class))).thenReturn(expense);
        when(userRepository.findById("WCS171")).thenReturn(Optional.of(employee));
        doNothing().when(emailService).sendEmail(anyString(), anyString(), anyString(), anyMap());

        Expenses result = expensesService.createExpense(expense);

        assertNotNull(result);
        assertEquals(expense.getExpenseID(), result.getExpenseID());
        verify(expensesRepository).save(expense);
        verify(userRepository).findById("WCS171");
        verify(emailService).sendEmail(eq(manager.getEmail()), eq("New Expense Submission"), 
            eq("templates/newExpenseTemplate.html"), anyMap());
    }

    @Test
    void testCreateExpense_UserNotFound() {
        when(userRepository.findById("WCS999")).thenReturn(Optional.empty());
        
        Expenses invalidExpense = new Expenses();
        User invalidUser = new User();
        invalidUser.setWissenID("WCS999");
        invalidExpense.setUser(invalidUser);

        assertThrows(IllegalArgumentException.class, () -> 
            expensesService.createExpense(invalidExpense));
    }

    @Test
    void testCreateExpense_NoManager() {
        employee.setManager(null);
        when(userRepository.findById("WCS171")).thenReturn(Optional.of(employee));
        
        assertThrows(IllegalArgumentException.class, () -> 
            expensesService.createExpense(expense));
    }

    @Test
    void testApproveExpense_Success() throws MessagingException, IOException {
        when(expensesRepository.findById(1L)).thenReturn(Optional.of(expense));
        when(expensesRepository.save(any(Expenses.class))).thenReturn(expense);
        when(userRepository.findById("WCS171")).thenReturn(Optional.of(employee));
        doNothing().when(emailService).sendEmail(anyString(), anyString(), anyString(), anyMap());

        Expenses result = expensesService.approveExpense(1L);

        assertEquals("APPROVED", result.getStatus());
        verify(expensesRepository).save(any(Expenses.class));
        verify(emailService).sendEmail(eq(employee.getEmail()), eq("Expense Approved"), 
            eq("templates/expenseEmailTemplate.html"), anyMap());
    }

    @Test
    void testRejectExpense_Success() throws MessagingException, IOException {
        when(expensesRepository.findById(1L)).thenReturn(Optional.of(expense));
        when(expensesRepository.save(any(Expenses.class))).thenReturn(expense);
        when(userRepository.findById("WCS171")).thenReturn(Optional.of(employee));
        doNothing().when(emailService).sendEmail(anyString(), anyString(), anyString(), anyMap());

        Expenses result = expensesService.rejectExpense(1L, "Invalid receipts");

        assertEquals("REJECTED", result.getStatus());
        assertEquals("Invalid receipts", result.getReasonForRejection());
        verify(expensesRepository).save(any(Expenses.class));
        verify(emailService).sendEmail(eq(employee.getEmail()), eq("Expense Rejected"), 
            eq("templates/expenseEmailTemplate.html"), anyMap());
    }

    @Test
    void testApproveExpense_NotFound() {
        when(expensesRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> 
            expensesService.approveExpense(999L));
    }

    @Test
    void testRejectExpense_NotFound() {
        when(expensesRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> 
            expensesService.rejectExpense(999L, "Invalid"));
    }

    @Test
    void testGetById() {
        when(expensesRepository.findByIdAndUser_WissenID(1L, employee.getWissenID()))
            .thenReturn(Optional.of(expense));
        when(userRepository.findById(employee.getWissenID())).thenReturn(Optional.of(employee));

        Optional<Expenses> result = expensesService.getExpenseByIdAndUserWissenID(1L, employee.getWissenID());
        
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getExpenseID());
        assertEquals(employee.getWissenID(), result.get().getUser().getWissenID());
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
        when(expensesRepository.findByUser_wissenID(employee.getWissenID())).thenReturn(expensesList);
        List<Expenses> result = expensesService.getExpensesByUserWissenID(employee.getWissenID());
        assertEquals(1, result.size());
        verify(expensesRepository, times(1)).findByUser_wissenID(employee.getWissenID());
    }

    @Test
    void testExpenseNotFound() {
        when(expensesRepository.findById(999L)).thenReturn(Optional.empty());
        Optional<Expenses> result = expensesService.getExpenseByIdAndUserWissenID(999L, employee.getWissenID());
        assertFalse(result.isPresent());
    }
}