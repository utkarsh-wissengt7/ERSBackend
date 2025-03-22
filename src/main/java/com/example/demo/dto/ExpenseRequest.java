package com.example.demo.dto;

import com.example.demo.models.Expenses;

public class ExpenseRequest {
    private Long userId;
    private Expenses expense;

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Expenses getExpense() {
        return expense;
    }

    public void setExpense(Expenses expense) {
        this.expense = expense;
    }
}
