package com.example.demo.dto;

import lombok.Data;

@Data
public class ExpenseRequest {
    private String userId;
    private ExpenseDetails expense;
}