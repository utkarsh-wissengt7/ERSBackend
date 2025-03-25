package com.example.demo.controllers;

import com.example.demo.dto.ExpenseRequest;
import com.example.demo.models.Expenses;
import com.example.demo.models.User;
import com.example.demo.services.ExpensesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/expenses/")
public class ExpensesController {

    @Autowired
    private ExpensesService expensesService;

    @GetMapping("/user")
    public List<Expenses> getAllExpensesByUserWissenID(@RequestParam String userId) {
        return expensesService.getExpensesByUserWissenID(userId);
    }

    @GetMapping("/user/{wissenID}/first")
    public ResponseEntity<Expenses> getFirstExpenseByUserWissenID(@PathVariable String wissenID) {
        Optional<Expenses> expense = expensesService.getFirstExpenseByUserWissenID(wissenID);
        return expense.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/user/{wissenID}")
    public ResponseEntity<Expenses> getExpenseByIdAndUserWissenID(@PathVariable Long id, @PathVariable String wissenID) {
        Optional<Expenses> expense = expensesService.getExpenseByIdAndUserWissenID(id, wissenID);
        return expense.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/check/{id}/user/{wissenID}")
    public boolean checkExpenseExists(@PathVariable Long id, @PathVariable String wissenID) {
        return expensesService.existsByIdAndUserWissenID(id, wissenID);
    }

    @GetMapping("/user/{wissenID}/status/{status}")
    public List<Expenses> getExpensesByUserWissenIDAndStatus(@PathVariable String wissenID, @PathVariable String status) {
        return expensesService.getExpensesByUserWissenIDAndStatus(wissenID, status);
    }

    @PostMapping
    public ResponseEntity<Expenses> createExpense(@RequestBody ExpenseRequest request) {
        try {
            Expenses expense = new Expenses();
            expense.setCategory(request.getExpense().getCategory());
            expense.setAmount(request.getExpense().getAmount());
            expense.setDescription(request.getExpense().getDescription());
            expense.setReceipt(request.getExpense().getReceipt());

            // Set additional fields
            expense.setStatus("PENDING");
            expense.setCreatedAt(new Date());

            // Set user details using the userId from request
            User user = new User();
            user.setWissenID(request.getUserId());
            expense.setUser(user);

            return ResponseEntity.ok(expensesService.createExpense(expense));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<Expenses> updateExpense(
            @PathVariable Long id,
            @RequestParam String userId,
            @RequestBody ExpenseRequest request) {
        try {
            Optional<Expenses> existingExpense = expensesService.getExpenseByIdAndUserWissenID(id, userId);
            if (existingExpense.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Expenses expense = existingExpense.get();
            expense.setCategory(request.getExpense().getCategory());
            expense.setAmount(request.getExpense().getAmount());
            expense.setDescription(request.getExpense().getDescription());
            expense.setReceipt(request.getExpense().getReceipt());

            return ResponseEntity.ok(expensesService.updateExpense(expense));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    @PutMapping("/{id}/status/{action}")
    public ResponseEntity<Expenses> updateExpenseStatus(
            @PathVariable Long id,
            @PathVariable String action,
            @RequestParam String userId,
            @RequestParam String status,
            @RequestParam(required = false) String approvedBy,
            @RequestParam(required = false) String rejectedBy,
            @RequestParam(required = false) String reason) {
        try {
            Optional<Expenses> existingExpense = expensesService.getExpenseByIdAndUserWissenID(id, userId);
            if (existingExpense.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Expenses expense = existingExpense.get();
            expense.setStatus(status);

            if ("approve".equals(action) && approvedBy != null) {
                expense.setApprovedBy(approvedBy);
            } else if ("reject".equals(action) && rejectedBy != null) {
                expense.setRejectedBy(rejectedBy);
                expense.setReasonForRejection(reason);
            }

            return ResponseEntity.ok(expensesService.updateExpense(expense));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(
            @PathVariable Long id,
            @RequestParam String userId) {
        try {
            Optional<Expenses> expense = expensesService.getExpenseByIdAndUserWissenID(id, userId);
            if (expense.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            expensesService.deleteExpense(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}