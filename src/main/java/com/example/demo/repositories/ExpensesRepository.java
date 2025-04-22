package com.example.demo.repositories;

import com.example.demo.models.Expenses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExpensesRepository extends JpaRepository<Expenses, Long> {
    List<Expenses> findByUser_WissenIDAndStatus(String wissenID, String status);
    Optional<Expenses> findFirstByUser_WissenID(String wissenID);
    Optional<Expenses> findByIdAndUser_WissenID(Long id, String wissenID);
    boolean existsByIdAndUser_WissenID(Long id, String wissenID);

    List<Expenses> findByUser_wissenID(String wissenID);
}