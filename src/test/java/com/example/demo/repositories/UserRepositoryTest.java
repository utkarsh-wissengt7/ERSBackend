// package com.example.demo.repositories;

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
// class UserRepositoryTest {

//     @Autowired
//     private TestEntityManager entityManager;

//     @Autowired
//     private UserRepository userRepository;

//     @Test
//     void findByEmail() {
//         // Arrange
//         User user = new User();
//         user.setWissenID("WCS171");
//         user.setName("Test User");
//         user.setEmail("test@example.com");
//         user.setPassword("password123");
//         entityManager.persist(user);
//         entityManager.flush();

//         // Act
//         Optional<User> found = userRepository.findByEmail("test@example.com");

//         // Assert
//         assertTrue(found.isPresent());
//         assertEquals("WCS171", found.get().getWissenID());
//         assertEquals("test@example.com", found.get().getEmail());
//     }

//     @Test
//     void findByRole() {
//         // Arrange
//         User admin = new User();
//         admin.setWissenID("ADM001");
//         admin.setName("Admin User");
//         admin.setEmail("admin@example.com");
//         admin.setPassword("password123");
//         admin.setRole("ADMIN");
//         entityManager.persist(admin);

//         User employee = new User();
//         employee.setWissenID("WCS171");
//         employee.setName("Regular User");
//         employee.setEmail("user@example.com");
//         employee.setPassword("password123");
//         employee.setRole("USER");
//         entityManager.persist(employee);

//         entityManager.flush();

//         // Act
//         List<User> adminUsers = userRepository.findByRole("ADMIN");
//         List<User> regularUsers = userRepository.findByRole("USER");

//         // Assert
//         assertEquals(1, adminUsers.size());
//         assertEquals(1, regularUsers.size());
//         assertEquals("ADMIN", adminUsers.get(0).getRole());
//         assertEquals("USER", regularUsers.get(0).getRole());
//     }

//     @Test
//     void findById() {
//         // Arrange
//         User user = new User();
//         user.setWissenID("WCS171");
//         user.setName("Test User");
//         user.setEmail("test@example.com");
//         user.setPassword("password123");
//         entityManager.persist(user);
//         entityManager.flush();

//         // Act
//         Optional<User> found = userRepository.findById("WCS171");

//         // Assert
//         assertTrue(found.isPresent());
//         assertEquals("Test User", found.get().getName());
//         assertEquals("test@example.com", found.get().getEmail());
//     }

//     @Test
//     void findByManagerId() {
//         // Arrange
//         User manager = new User();
//         manager.setWissenID("MGR001");
//         manager.setName("Manager User");
//         manager.setEmail("manager@example.com");
//         manager.setPassword("password123");
//         manager.setRole("MANAGER");
//         entityManager.persist(manager);

//         User employee1 = new User();
//         employee1.setWissenID("WCS171");
//         employee1.setName("Employee One");
//         employee1.setEmail("emp1@example.com");
//         employee1.setPassword("password123");
//         employee1.setManagerId("MGR001");
//         entityManager.persist(employee1);

//         User employee2 = new User();
//         employee2.setWissenID("WCS172");
//         employee2.setName("Employee Two");
//         employee2.setEmail("emp2@example.com");
//         employee2.setPassword("password123");
//         employee2.setManagerId("MGR001");
//         entityManager.persist(employee2);

//         entityManager.flush();

//         // Act
//         List<User> reportees = userRepository.findByManagerId("MGR001");

//         // Assert
//         assertEquals(2, reportees.size());
//         assertEquals("MGR001", reportees.get(0).getManagerId());
//         assertEquals("MGR001", reportees.get(1).getManagerId());
//     }
// }