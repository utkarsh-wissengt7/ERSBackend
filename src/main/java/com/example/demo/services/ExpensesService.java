package com.example.demo.services;

import com.example.demo.models.Expenses;
import com.example.demo.repositories.ExpensesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ExpensesService {

    @Autowired
    private ExpensesRepository expensesRepository;

    public List<Expenses> getExpensesByUserWissenID(String wissenID) {
        return expensesRepository.findByUser_wissenID(wissenID);
    }

    public Optional<Expenses> getFirstExpenseByUserWissenID(String wissenID) {
        return expensesRepository.findFirstByUser_WissenID(wissenID);
    }

    public Optional<Expenses> getExpenseByIdAndUserWissenID(Long id, String wissenID) {
        return expensesRepository.findByIdAndUser_WissenID(id, wissenID);
    }

    public boolean existsByIdAndUserWissenID(Long id, String wissenID) {
        return expensesRepository.existsByIdAndUser_WissenID(id, wissenID);
    }

    public List<Expenses> getExpensesByUserWissenIDAndStatus(String wissenID, String status) {
        return expensesRepository.findByUser_WissenIDAndStatus(wissenID, status);
    }
    public Expenses createExpense(Expenses expense) {
        return expensesRepository.save(expense);
    }
    public Expenses updateExpense(Expenses expense) {
        return expensesRepository.save(expense);
    }
    public void deleteExpense(Long id) {
        expensesRepository.deleteById(id);
    }
    private void attachReceipt(Long expenseId, String pdfUrl) {
        Optional<Expenses> optionalExpense = expensesRepository.findById(expenseId);
        if (optionalExpense.isPresent()) {
            Expenses expense = optionalExpense.get();
            expense.setReceipt(pdfUrl);  // ✅ Store the PDF URL
            expensesRepository.save(expense);
        } else {
            throw new IllegalArgumentException("Expense not found");
        }
    }
}