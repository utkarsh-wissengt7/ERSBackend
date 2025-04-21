package com.example.demo.services;

import com.example.demo.models.Expenses;
import com.example.demo.models.User;
import com.example.demo.repositories.ExpensesRepository;
import com.example.demo.repositories.UserRepository;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ExpensesService {

    @Autowired
    private ExpensesRepository expensesRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    public List<Expenses> getExpensesByUserWissenID(String wissenID) {
        System.out.println("Fetching expenses for user with WissenID: " + wissenID);
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

    public Expenses createExpense(Expenses expense) throws MessagingException, IOException {
        Expenses savedExpense = expensesRepository.save(expense);

        // Fetch the employee who submitted the expense
        User employee = userRepository.findById(expense.getUser().getWissenID())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Fetch the manager of the employee
        User manager = employee
                .getManager();
        if (manager == null || manager.getEmail() == null) {
            throw new IllegalArgumentException("Manager not found for this employee");
        }

        // Prepare email content
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("name", manager.getName());
        placeholders.put("employeeName", employee.getName());
        placeholders.put("expenseId", String.valueOf(savedExpense.getId()));
        placeholders.put("category", savedExpense.getCategory());
        placeholders.put("amount", String.valueOf(savedExpense.getAmount()));
        placeholders.put("status", savedExpense.getStatus());

        // Send email to the manager
        emailService.sendEmail(manager.getEmail(), "New Expense Submission", "templates/newExpenseTemplate.html", placeholders);

        return savedExpense;
    }

//    public Expenses approveExpense(Long expenseId) throws MessagingException, IOException {
//        Expenses expense = expensesRepository.findById(expenseId)
//                .orElseThrow(() -> new IllegalArgumentException("Expense not found"));
//
//        expense.setStatus("Approved");
//        Expenses updatedExpense = expensesRepository.save(expense);
//
//        // Fetch the employee who submitted the expense
//        User employee = userRepository.findById(expense.getUser().getWissenID())
//                .orElseThrow(() -> new IllegalArgumentException("User not found"));
//
//        // Prepare email content
//        Map<String, String> placeholders = new HashMap<>();
//        placeholders.put("name", employee.getName());
//        placeholders.put("expenseId", String.valueOf(updatedExpense.getId()));
//        placeholders.put("status", "approved");
//        placeholders.put("message", "Your expense has been approved successfully.");
//
//        // Send email to the employee
//        emailService.sendEmail(employee.getEmail(), "Expense Approved", "templates/expenseEmailTemplate.html", placeholders);
//
//        return updatedExpense;
//    }
//
//    public Expenses rejectExpense(Long expenseId, String reason) throws MessagingException, IOException {
//        Expenses expense = expensesRepository.findById(expenseId)
//                .orElseThrow(() -> new IllegalArgumentException("Expense not found"));
//
//        expense.setStatus("Rejected");
//        expense.setReasonForRejection(reason);
//        Expenses updatedExpense = expensesRepository.save(expense);
//
//        // Fetch the employee who submitted the expense
//        User employee = userRepository.findById(expense.getUser().getWissenID())
//                .orElseThrow(() -> new IllegalArgumentException("User not found"));
//
//        // Prepare email content
//        Map<String, String> placeholders = new HashMap<>();
//        placeholders.put("name", employee.getName());
//        placeholders.put("expenseId", String.valueOf(updatedExpense.getId()));
//        placeholders.put("status", "rejected");
//        placeholders.put("message", "Reason for rejection: " + reason);
//
//        // Send email to the employee
//        emailService.sendEmail(employee.getEmail(), "Expense Rejected", "templates/expenseEmailTemplate.html", placeholders);
//
//        return updatedExpense;
//    }

    public Expenses approveExpense(Long expenseId) throws MessagingException, IOException {
        Expenses expense = expensesRepository.findById(expenseId)
                .orElseThrow(() -> new IllegalArgumentException("Expense not found"));

        expense.setStatus("Approved");
        Expenses updatedExpense = expensesRepository.save(expense);

        User employee = userRepository.findById(expense.getUser().getWissenID())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("name", employee.getName());
        placeholders.put("expenseId", String.valueOf(updatedExpense.getId()));
        placeholders.put("status", "approved");
        placeholders.put("message", "Your expense has been approved successfully.");

        emailService.sendEmail(employee.getEmail(), "Expense Approved", "templates/expenseEmailTemplate.html", placeholders);

        return updatedExpense;
    }

    public Expenses rejectExpense(Long expenseId, String reason) throws MessagingException, IOException {
        Expenses expense = expensesRepository.findById(expenseId)
                .orElseThrow(() -> new IllegalArgumentException("Expense not found"));

        expense.setStatus("Rejected");
        expense.setReasonForRejection(reason);
        Expenses updatedExpense = expensesRepository.save(expense);

        User employee = userRepository.findById(expense.getUser().getWissenID())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("name", employee.getName());
        placeholders.put("expenseId", String.valueOf(updatedExpense.getId()));
        placeholders.put("status", "rejected");
        placeholders.put("message", "Reason for rejection: " + reason);

        emailService.sendEmail(employee.getEmail(), "Expense Rejected", "templates/expenseEmailTemplate.html", placeholders);

        return updatedExpense;
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
            expense.setReceipt(pdfUrl);
            expensesRepository.save(expense);
        } else {
            throw new IllegalArgumentException("Expense not found");
        }
    }
}