package com.example.demo.services;

import com.example.demo.models.Expenses;
import com.example.demo.models.User;
import com.example.demo.repositories.ExpensesRepository;
import com.example.demo.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ExpensesService {

    @Autowired
    private ExpensesRepository expensesRepository;

    @Autowired
    private UserRepository userRepository; // Inject UserRepository

    public List<Expenses> getAllExpensesByUserId(Long userId) {
        return expensesRepository.findByUserId(userId);
    }

    public Optional<Expenses> getExpenseByUserId(Long userId) {
        return expensesRepository.findFirstByUserId(userId);
    }

    public Expenses createExpense(Long userId, Expenses expense) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        expense.setUser(user); // Set User object instead of just ID
        expense.setStatus("PENDING"); // Set default status
        System.out.println("yeh leeee"+String.valueOf(expense));
        return expensesRepository.save(expense);
    }

    public Expenses updateExpense(Long id, Long userId, Expenses updatedExpense) {
        return expensesRepository.findByIdAndUserId(id, userId).map(expense -> {
            expense.setAmount(updatedExpense.getAmount());
            expense.setCategory(updatedExpense.getCategory());
            expense.setReceipt(updatedExpense.getReceipt());
            expense.setStatus(updatedExpense.getStatus());
            return expensesRepository.save(expense);
        }).orElseThrow(() -> new IllegalArgumentException("Expense not found for user"));
    }

    public void deleteExpense(Long id, Long userId) {
        if (!expensesRepository.existsByIdAndUserId(id, userId)) {
            throw new IllegalArgumentException("Expense not found for user");
        }
        expensesRepository.deleteById(id);
    }

    public List<Expenses> getPendingExpensesByUserId(Long userId) {
        return expensesRepository.findByUserIdAndStatus(userId, "PENDING");
    }

    public List<Expenses> getApprovedExpensesByUserId(Long userId) {
        return expensesRepository.findByUserIdAndStatus(userId, "APPROVED");
    }

    public Expenses updateApprovalStatus(Long id, Long userId, String status) {
        return expensesRepository.findByIdAndUserId(id, userId).map(expense -> {
            if (status.equalsIgnoreCase("APPROVED") || status.equalsIgnoreCase("REJECTED")) {
                expense.setStatus(status);
                return expensesRepository.save(expense);
            }
            throw new IllegalArgumentException("Invalid status");
        }).orElseThrow(() -> new IllegalArgumentException("Expense not found for user"));
    }
}
