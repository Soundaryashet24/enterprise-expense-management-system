package com.soundarya.enterpriseexpensemanagementsystem.repository;

import com.soundarya.enterpriseexpensemanagementsystem.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
}