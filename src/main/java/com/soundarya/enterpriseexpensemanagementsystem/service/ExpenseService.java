
package com.soundarya.enterpriseexpensemanagementsystem.service;

import com.soundarya.enterpriseexpensemanagementsystem.dto.ExpenseRequestDTO;
import com.soundarya.enterpriseexpensemanagementsystem.dto.ExpenseResponseDTO;
import com.soundarya.enterpriseexpensemanagementsystem.entity.Employee;
import com.soundarya.enterpriseexpensemanagementsystem.entity.Expense;
import com.soundarya.enterpriseexpensemanagementsystem.entity.ExpenseStatus;
import com.soundarya.enterpriseexpensemanagementsystem.exception.EmployeeNotFoundException;
import com.soundarya.enterpriseexpensemanagementsystem.exception.ExpenseNotFoundException;
import com.soundarya.enterpriseexpensemanagementsystem.repository.EmployeeRepository;
import com.soundarya.enterpriseexpensemanagementsystem.repository.ExpenseRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final EmployeeRepository employeeRepository;

    public ExpenseService(
            ExpenseRepository expenseRepository,
            EmployeeRepository employeeRepository) {

        this.expenseRepository = expenseRepository;
        this.employeeRepository = employeeRepository;
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
        return mapToResponse(findExpense(id));
    }

    @Transactional(readOnly = true)
    public List<ExpenseResponseDTO> getExpensesByEmployee(Long employeeId) {

        findEmployee(employeeId);

        return expenseRepository.findByEmployeeId(employeeId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public ExpenseResponseDTO createExpense(ExpenseRequestDTO request) {

        Employee employee = findEmployee(request.getEmployeeId());

        Expense expense = new Expense();
        expense.setEmployee(employee);
        expense.setAmount(request.getAmount());
        expense.setCategory(request.getCategory());
        expense.setDescription(request.getDescription());
        expense.setExpenseDate(request.getExpenseDate());
        expense.setStatus(ExpenseStatus.SUBMITTED);

        return mapToResponse(expenseRepository.save(expense));
    }

    @Transactional
    public ExpenseResponseDTO updateExpense(
            Long id, ExpenseRequestDTO request) {

        Expense expense = findExpense(id);

        ensureSubmitted(expense);

        // Keep the original owner unchanged.
        if (!expense.getEmployee().getId()
                .equals(request.getEmployeeId())) {
            throw new IllegalStateException(
                    "Expense cannot be transferred to another employee");
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

        ensureSubmitted(expense);

        expenseRepository.delete(expense);
    }

    @Transactional
    public ExpenseResponseDTO approveExpense(Long id) {

        Expense expense = findExpense(id);

        ensureSubmitted(expense);

        expense.setStatus(ExpenseStatus.APPROVED);

        return mapToResponse(expenseRepository.save(expense));
    }

    @Transactional
    public ExpenseResponseDTO rejectExpense(Long id) {

        Expense expense = findExpense(id);

        ensureSubmitted(expense);

        expense.setStatus(ExpenseStatus.REJECTED);

        return mapToResponse(expenseRepository.save(expense));
    }

    private void ensureSubmitted(Expense expense) {

        if (expense.getStatus() != ExpenseStatus.SUBMITTED) {
            throw new IllegalStateException(
                    "Only submitted expenses can be modified");
        }
    }

    private Employee findEmployee(Long id) {

        return employeeRepository.findById(id)
                .orElseThrow(() ->
                        new EmployeeNotFoundException(
                                "Employee not found with id: " + id));
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
                expense.getStatus(),
                expense.getEmployee().getId()
        );
    }
}