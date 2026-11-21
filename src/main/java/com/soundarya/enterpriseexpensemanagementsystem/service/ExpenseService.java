
package com.soundarya.enterpriseexpensemanagementsystem.service;

import com.soundarya.enterpriseexpensemanagementsystem.dto.ExpenseRequestDTO;
import com.soundarya.enterpriseexpensemanagementsystem.dto.ExpenseResponseDTO;
import com.soundarya.enterpriseexpensemanagementsystem.entity.Expense;
import com.soundarya.enterpriseexpensemanagementsystem.entity.ExpenseStatus;
import com.soundarya.enterpriseexpensemanagementsystem.exception.ExpenseNotFoundException;
import com.soundarya.enterpriseexpensemanagementsystem.repository.ExpenseRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    @Transactional(readOnly = true)
    public List<ExpenseResponseDTO> getAllExpenses() {

        return expenseRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ExpenseResponseDTO getExpenseById(Long id) {

        Expense expense = findExpense(id);

        return mapToResponse(expense);
    }

    @Transactional
    public ExpenseResponseDTO createExpense(ExpenseRequestDTO request) {

        Expense expense = new Expense();

        expense.setAmount(request.getAmount());
        expense.setCategory(request.getCategory());
        expense.setDescription(request.getDescription());
        expense.setExpenseDate(request.getExpenseDate());
        expense.setStatus(ExpenseStatus.SUBMITTED);

        Expense savedExpense = expenseRepository.save(expense);

        return mapToResponse(savedExpense);
    }

    @Transactional
    public ExpenseResponseDTO updateExpense(
            Long id, ExpenseRequestDTO request) {

        Expense expense = findExpense(id);

        if (expense.getStatus() != ExpenseStatus.SUBMITTED) {
            throw new IllegalStateException(
                    "Only submitted expenses can be updated");
        }

        expense.setAmount(request.getAmount());
        expense.setCategory(request.getCategory());
        expense.setDescription(request.getDescription());
        expense.setExpenseDate(request.getExpenseDate());

        return mapToResponse(expenseRepository.save(expense));
    }

    @Transactional
    public void deleteExpense(Long id) {

        Expense expense = findExpense(id);

        if (expense.getStatus() != ExpenseStatus.SUBMITTED) {
            throw new IllegalStateException(
                    "Only submitted expenses can be deleted");
        }

        expenseRepository.delete(expense);
    }

    private Expense findExpense(Long id) {

        return expenseRepository.findById(id)
                .orElseThrow(() ->
                        new ExpenseNotFoundException(
                                "Expense not found with id: " + id));
    }

    private ExpenseResponseDTO mapToResponse(Expense expense) {

        return new ExpenseResponseDTO(
                expense.getId(),
                expense.getAmount(),
                expense.getCategory(),
                expense.getDescription(),
                expense.getExpenseDate(),
                expense.getStatus()
        );
    }
}
