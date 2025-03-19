package com.example.demo.repositories;

import com.example.demo.models.Expenses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExpensesRepository extends JpaRepository<Expenses, Long> {
    List<Expenses> findByUserId(Long userId);
    Optional<Expenses> findFirstByUserId(Long userId);
    Optional<Expenses> findByIdAndUserId(Long id, Long userId);
    boolean existsByIdAndUserId(Long id, Long userId);
    List<Expenses> findByUserIdAndStatus(Long userId, String status);
}
