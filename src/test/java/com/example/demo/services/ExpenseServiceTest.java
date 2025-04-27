//package com.example.demo.services;
//
//import com.example.demo.models.Expenses;
//import com.example.demo.models.User;
//import com.example.demo.repositories.ExpensesRepository;
//import com.example.demo.repositories.UserRepository;
//import jakarta.mail.MessagingException;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import java.io.IOException;
//import java.util.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//
//class ExpensesServiceTest {
//
//    @Mock
//    private ExpensesRepository expensesRepository;
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private EmailService emailService;
//
//    @InjectMocks
//    private ExpensesService expensesService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void testGetExpensesByUserWissenID() {
//        String wissenID = "WCS171";
//        List<Expenses> mockExpenses = Arrays.asList(new Expenses(), new Expenses());
//
//        when(expensesRepository.findByUser_wissenID(wissenID)).thenReturn(mockExpenses);
//
//        List<Expenses> result = expensesService.getExpensesByUserWissenID(wissenID);
//
//        assertNotNull(result);
//        assertEquals(2, result.size());
//    }
//
//    @Test
//    void testCreateExpense() throws MessagingException, IOException {
//        Expenses expense = new Expenses();
//        expense.setId(1L);
//
//        // Mock the user and manager
//        User user = mock(User.class);
//        User manager = mock(User.class);
//
//        // Set up mock behavior for user and manager
//        when(user.getWissenID()).thenReturn("WCS171");
//        when(user.getName()).thenReturn("Test User");
//        when(user.getEmail()).thenReturn("testuser@example.com");
//        when(user.getManager()).thenReturn(manager);
//
//        when(manager.getName()).thenReturn("Manager");
//        when(manager.getEmail()).thenReturn("manager@example.com");
//
//        expense.setUser(user);
//
//        // Mock repository behavior
//        when(userRepository.findById("WCS171")).thenReturn(Optional.of(user));
//        when(expensesRepository.save(any(Expenses.class))).thenReturn(expense);
//
//        // Call the service method
//        Expenses result = expensesService.createExpense(expense);
//
//        // Assertions and verifications
//        assertNotNull(result);
//        verify(emailService, times(1)).sendEmail(eq("manager@example.com"), anyString(), anyString(), anyMap());
//    }
//
//    @Test
//    void testApproveExpense() throws MessagingException, IOException {
//        Expenses expense = new Expenses();
//        expense.setId(1L);
//        expense.setStatus("PENDING");
//        User user = new User();
//        user.setWissenID("WCS171");
//        user.setName("Test User");
//        user.setEmail("test@example.com");
//        expense.setUser(user);
//
//        when(expensesRepository.findById(1L)).thenReturn(Optional.of(expense));
//        when(userRepository.findById("WCS171")).thenReturn(Optional.of(user));
//        when(expensesRepository.save(any(Expenses.class))).thenReturn(expense);
//
//        Expenses result = expensesService.approveExpense(1L);
//
//        assertNotNull(result);
//        assertEquals("APPROVED", result.getStatus());
//        verify(emailService, times(1)).sendEmail(eq("test@example.com"), anyString(), anyString(), anyMap());
//    }
//
//    @Test
//    void testRejectExpense() throws MessagingException, IOException {
//        Expenses expense = new Expenses();
//        expense.setId(1L);
//        expense.setStatus("PENDING");
//        User user = new User();
//        user.setWissenID("WCS171");
//        user.setName("Test User");
//        user.setEmail("test@example.com");
//        expense.setUser(user);
//
//        when(expensesRepository.findById(1L)).thenReturn(Optional.of(expense));
//        when(userRepository.findById("WCS171")).thenReturn(Optional.of(user));
//        when(expensesRepository.save(any(Expenses.class))).thenReturn(expense);
//
//        Expenses result = expensesService.rejectExpense(1L, "Invalid receipt");
//
//        assertNotNull(result);
//        assertEquals("REJECTED", result.getStatus());
//        verify(emailService, times(1)).sendEmail(eq("test@example.com"), anyString(), anyString(), anyMap());
//    }
//}
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
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ExpenseServiceTest {

    @Mock
    private ExpensesRepository expensesRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private ExpensesService expensesService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetExpensesByUserWissenID() {
        String wissenID = "WCS171";
        List<Expenses> mockExpenses = Arrays.asList(new Expenses(), new Expenses());

        when(expensesRepository.findByUser_wissenID(wissenID)).thenReturn(mockExpenses);

        List<Expenses> result = expensesService.getExpensesByUserWissenID(wissenID);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(expensesRepository, times(1)).findByUser_wissenID(wissenID);
    }

    @Test
    void testGetFirstExpenseByUserWissenID() {
        String wissenID = "WCS171";
        Expenses expense = new Expenses();
        when(expensesRepository.findFirstByUser_WissenID(wissenID)).thenReturn(Optional.of(expense));

        Optional<Expenses> result = expensesService.getFirstExpenseByUserWissenID(wissenID);

        assertTrue(result.isPresent());
        verify(expensesRepository, times(1)).findFirstByUser_WissenID(wissenID);
    }

    @Test
    void testGetExpenseByIdAndUserWissenID() {
        Long id = 1L;
        String wissenID = "WCS171";
        Expenses expense = new Expenses();
        when(expensesRepository.findByIdAndUser_WissenID(id, wissenID)).thenReturn(Optional.of(expense));

        Optional<Expenses> result = expensesService.getExpenseByIdAndUserWissenID(id, wissenID);

        assertTrue(result.isPresent());
        verify(expensesRepository, times(1)).findByIdAndUser_WissenID(id, wissenID);
    }

    @Test
    void testExistsByIdAndUserWissenID() {
        Long id = 1L;
        String wissenID = "WCS171";
        when(expensesRepository.existsByIdAndUser_WissenID(id, wissenID)).thenReturn(true);

        boolean result = expensesService.existsByIdAndUserWissenID(id, wissenID);

        assertTrue(result);
        verify(expensesRepository, times(1)).existsByIdAndUser_WissenID(id, wissenID);
    }

    @Test
    void testGetExpensesByUserWissenIDAndStatus() {
        String wissenID = "WCS171";
        String status = "APPROVED";
        List<Expenses> mockExpenses = Arrays.asList(new Expenses(), new Expenses());

        when(expensesRepository.findByUser_WissenIDAndStatus(wissenID, status)).thenReturn(mockExpenses);

        List<Expenses> result = expensesService.getExpensesByUserWissenIDAndStatus(wissenID, status);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(expensesRepository, times(1)).findByUser_WissenIDAndStatus(wissenID, status);
    }

    @Test
    void testCreateExpense_Success() throws MessagingException, IOException {
        Expenses expense = new Expenses();
        expense.setId(1L);

        User user = mock(User.class);
        User manager = mock(User.class);

        when(user.getWissenID()).thenReturn("WCS171");
        when(user.getName()).thenReturn("Test User");
        when(user.getEmail()).thenReturn("testuser@example.com");
        when(user.getManager()).thenReturn(manager);

        when(manager.getName()).thenReturn("Manager");
        when(manager.getEmail()).thenReturn("manager@example.com");

        expense.setUser(user);

        when(userRepository.findById("WCS171")).thenReturn(Optional.of(user));
        when(expensesRepository.save(any(Expenses.class))).thenReturn(expense);

        Expenses result = expensesService.createExpense(expense);

        assertNotNull(result);
        verify(emailService, times(1)).sendEmail(eq("manager@example.com"), anyString(), anyString(), anyMap());
    }

    @Test
    void testCreateExpense_UserNotFound() {
        Expenses expense = new Expenses();
        User user = new User();
        user.setWissenID("WCS999");
        expense.setUser(user);

        when(userRepository.findById("WCS999")).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> expensesService.createExpense(expense));

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testApproveExpense_Success() throws MessagingException, IOException {
        Expenses expense = new Expenses();
        expense.setId(1L);
        expense.setStatus("PENDING");
        User user = new User();
        user.setWissenID("WCS171");
        user.setName("Test User");
        user.setEmail("test@example.com");
        expense.setUser(user);

        when(expensesRepository.findById(1L)).thenReturn(Optional.of(expense));
        when(userRepository.findById("WCS171")).thenReturn(Optional.of(user));
        when(expensesRepository.save(any(Expenses.class))).thenReturn(expense);

        Expenses result = expensesService.approveExpense(1L);

        assertNotNull(result);
        assertEquals("APPROVED", result.getStatus());
        verify(emailService, times(1)).sendEmail(eq("test@example.com"), anyString(), anyString(), anyMap());
    }

    @Test
    void testApproveExpense_ExpenseNotFound() {
        when(expensesRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> expensesService.approveExpense(1L));

        assertEquals("Expense not found", exception.getMessage());
    }

    @Test
    void testRejectExpense_Success() throws MessagingException, IOException {
        Expenses expense = new Expenses();
        expense.setId(1L);
        expense.setStatus("PENDING");
        User user = new User();
        user.setWissenID("WCS171");
        user.setName("Test User");
        user.setEmail("test@example.com");
        expense.setUser(user);

        when(expensesRepository.findById(1L)).thenReturn(Optional.of(expense));
        when(userRepository.findById("WCS171")).thenReturn(Optional.of(user));
        when(expensesRepository.save(any(Expenses.class))).thenReturn(expense);

        Expenses result = expensesService.rejectExpense(1L, "Invalid receipt");

        assertNotNull(result);
        assertEquals("REJECTED", result.getStatus());
        verify(emailService, times(1)).sendEmail(eq("test@example.com"), anyString(), anyString(), anyMap());
    }

    @Test
    void testRejectExpense_ExpenseNotFound() {
        when(expensesRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> expensesService.rejectExpense(1L, "Invalid receipt"));

        assertEquals("Expense not found", exception.getMessage());
    }

    @Test
    void testUpdateExpense() {
        Expenses expense = new Expenses();
        when(expensesRepository.save(expense)).thenReturn(expense);

        Expenses result = expensesService.updateExpense(expense);

        assertNotNull(result);
        verify(expensesRepository, times(1)).save(expense);
    }

    @Test
    void testDeleteExpense() {
        Long id = 1L;

        expensesService.deleteExpense(id);

        verify(expensesRepository, times(1)).deleteById(id);
    }
}