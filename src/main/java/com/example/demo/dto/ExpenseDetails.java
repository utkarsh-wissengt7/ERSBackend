package com.example.demo.dto;

import lombok.Data;

@Data
public class ExpenseDetails {
    private String category;
    private Double amount;
    private String description;
    private String receipt;
}