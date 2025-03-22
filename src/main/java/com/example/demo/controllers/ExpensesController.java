package com.example.demo.controllers;

import com.example.demo.dto.ExpenseRequest;
import com.example.demo.models.Expenses;
import com.example.demo.services.ExpensesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/expenses")
public class ExpensesController {

    @Autowired
    private ExpensesService expensesService;

    // Fetch all expenses for the logged-in user
    @GetMapping
    public List<Expenses> getAllExpenses(@RequestParam Long userId) {
        return expensesService.getAllExpensesByUserId(userId);
    }

    // Fetch a specific expense by userId only (not by expense ID)
    @GetMapping("/user")
    public List<Expenses> getAllExpensesByUserId(@RequestParam Long userId) {
        return expensesService.getAllExpensesByUserId(userId);
    }

    // Create a new expense for the logged-in user
    @PostMapping
    public Expenses createExpense(@RequestBody ExpenseRequest request) {
        return expensesService.createExpense(request.getUserId(), request.getExpense());
    }


    // Update an existing expense for the logged-in user
    @PutMapping("/{id}")
    public Expenses updateExpense(@PathVariable Long id, @RequestParam Long userId, @RequestBody Expenses updatedExpense) {
        return expensesService.updateExpense(id, userId, updatedExpense);
    }

    // Delete an expense for the logged-in user
    @DeleteMapping("/{id}")
    public void deleteExpense(@PathVariable Long id, @RequestParam Long userId) {
        expensesService.deleteExpense(id, userId);
    }

    // Get all pending expenses for the logged-in user
    @GetMapping("/pending")
    public List<Expenses> getPendingExpenses(@RequestParam Long userId) {
        return expensesService.getPendingExpensesByUserId(userId);
    }

    // Get all approved expenses for the logged-in user
    @GetMapping("/approved")
    public List<Expenses> getApprovedExpenses(@RequestParam Long userId) {
        return expensesService.getApprovedExpensesByUserId(userId);
    }

    // Update approval status for an expense for the logged-in user

    @PutMapping("/{id}/status/approve")
    public Expenses updateApprovalStatus(@PathVariable Long id, @RequestParam Long userId, @RequestParam String status,
                                         @RequestParam Long approvedBy) {
        return expensesService.updateApprovalStatus(id, userId, status, approvedBy);
    }
    @PutMapping("/{id}/status/reject")
    public Expenses updateRejectionStatus(@PathVariable Long id, @RequestParam Long userId, @RequestParam String status,
                                         @RequestParam String reason, @RequestParam Long rejectedBy) {
        return expensesService.updateRejectionStatus(id, userId, status, reason, rejectedBy);
    }
}
