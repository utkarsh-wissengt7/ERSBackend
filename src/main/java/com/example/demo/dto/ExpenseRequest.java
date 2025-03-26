package com.example.demo.dto;

import lombok.Data;

@Data
public class ExpenseRequest {
    private String userId;
    private String category;
    private Double amount;
    private String description;
    private String receipt;
}