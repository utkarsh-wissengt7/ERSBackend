// package com.example.demo.repositories;

// import com.example.demo.models.Expenses;
// import com.example.demo.models.User;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
// import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
// import org.springframework.test.context.ActiveProfiles;

// import java.util.List;
// import java.util.Optional;

// import static org.junit.jupiter.api.Assertions.*;

// @DataJpaTest
// @ActiveProfiles("test")
// class ExpensesRepositoryTest {

//     @Autowired
//     private TestEntityManager entityManager;

//     @Autowired
//     private ExpensesRepository expensesRepository;

//     @Test
//     void findByUser_WissenIDAndStatus() {
//         // Arrange
//         User user = new User();
//         user.setWissenID("WCS171");
//         user.setName("Test User");
//         user.setEmail("test@example.com");
//         user.setPassword("password123");
//         entityManager.persist(user);

//         Expenses expense1 = new Expenses();
//         expense1.setUser(user);
//         expense1.setStatus("PENDING");
//         expense1.setAmount(100.0);
//         entityManager.persist(expense1);

//         Expenses expense2 = new Expenses();
//         expense2.setUser(user);
//         expense2.setStatus("APPROVED");
//         expense2.setAmount(200.0);
//         entityManager.persist(expense2);

//         entityManager.flush();

//         // Act
//         List<Expenses> pendingExpenses = expensesRepository.findByUser_WissenIDAndStatus("WCS171", "PENDING");
//         List<Expenses> approvedExpenses = expensesRepository.findByUser_WissenIDAndStatus("WCS171", "APPROVED");

//         // Assert
//         assertEquals(1, pendingExpenses.size());
//         assertEquals(100.0, pendingExpenses.get(0).getAmount());
//         assertEquals(1, approvedExpenses.size());
//         assertEquals(200.0, approvedExpenses.get(0).getAmount());
//     }

//     @Test
//     void findFirstByUser_WissenID() {
//         // Arrange
//         User user = new User();
//         user.setWissenID("WCS171");
//         user.setName("Test User");
//         user.setEmail("test@example.com");
//         user.setPassword("password123");
//         entityManager.persist(user);

//         Expenses expense1 = new Expenses();
//         expense1.setUser(user);
//         expense1.setAmount(100.0);
//         entityManager.persist(expense1);

//         Expenses expense2 = new Expenses();
//         expense2.setUser(user);
//         expense2.setAmount(200.0);
//         entityManager.persist(expense2);

//         entityManager.flush();

//         // Act
//         Optional<Expenses> result = expensesRepository.findFirstByUser_WissenID("WCS171");

//         // Assert
//         assertTrue(result.isPresent());
//         assertEquals(100.0, result.get().getAmount());
//     }

//     @Test
//     void findByIdAndUser_WissenID() {
//         // Arrange
//         User user = new User();
//         user.setWissenID("WCS171");
//         user.setName("Test User");
//         user.setEmail("test@example.com");
//         user.setPassword("password123");
//         entityManager.persist(user);

//         Expenses expense = new Expenses();
//         expense.setUser(user);
//         expense.setAmount(100.0);
//         entityManager.persist(expense);
//         entityManager.flush();

//         Long expenseId = expense.getExpenseID();

//         // Act
//         Optional<Expenses> result = expensesRepository.findByIdAndUser_WissenID(expenseId, "WCS171");

//         // Assert
//         assertTrue(result.isPresent());
//         assertEquals(100.0, result.get().getAmount());
//     }

//     @Test
//     void existsByIdAndUser_WissenID() {
//         // Arrange
//         User user = new User();
//         user.setWissenID("WCS171");
//         user.setName("Test User");
//         user.setEmail("test@example.com");
//         user.setPassword("password123");
//         entityManager.persist(user);

//         Expenses expense = new Expenses();
//         expense.setUser(user);
//         expense.setAmount(100.0);
//         entityManager.persist(expense);
//         entityManager.flush();

//         Long expenseId = expense.getExpenseID();

//         // Act & Assert
//         assertTrue(expensesRepository.existsByIdAndUser_WissenID(expenseId, "WCS171"));
//         assertFalse(expensesRepository.existsByIdAndUser_WissenID(expenseId + 1, "WCS171"));
//     }
// }