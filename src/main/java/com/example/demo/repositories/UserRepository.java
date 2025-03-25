package com.example.demo.repositories;

import com.example.demo.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> { // Changed Long to String

    Optional<User> findByEmail(String email);

    List<User> findByRole(String role);

    List<User> findByManagerId(String managerId); // No change needed since managerId is also a String

    Optional<User> findByEmailAndPassword(String email, String password);

    List<User> findByRoleNot(String role);
}
