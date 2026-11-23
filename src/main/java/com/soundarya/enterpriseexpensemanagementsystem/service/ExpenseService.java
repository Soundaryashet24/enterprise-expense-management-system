
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
import com.soundarya.enterpriseexpensemanagementsystem.security.ExpenseAccess;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final EmployeeRepository employeeRepository;
    private final ExpenseAccess expenseAccess;

    public ExpenseService(
            ExpenseRepository expenseRepository,
            EmployeeRepository employeeRepository,
            ExpenseAccess expenseAccess) {

        this.expenseRepository = expenseRepository;
        this.employeeRepository = employeeRepository;
        this.expenseAccess = expenseAccess;
    }

    private Authentication currentAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    private void requireManager() {
        if (!expenseAccess.isManager(currentAuthentication())) {
            throw new AccessDeniedException("Manager role required");
        }
    }

    private void requireSelf(Long employeeId) {
        if (!expenseAccess.isSelf(currentAuthentication(), employeeId)) {
            throw new AccessDeniedException(
                    "You can only modify your own expenses");
        }
    }

    private void requireSelfOrManager(Long employeeId) {
        if (!expenseAccess.isSelfOrManager(
                currentAuthentication(), employeeId)) {
            throw new AccessDeniedException(
                    "You cannot access another employee's expenses");
        }
    }

    private Expense findExpense(Long id) {
        return expenseRepository.findById(id)
                .orElseThrow(() ->
                        new ExpenseNotFoundException(
                                "Expense not found with ID: " + id));
    }

    private Employee findEmployee(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() ->
                        new EmployeeNotFoundException("Employee not found with ID: " + id));
    }

    private void ensureSubmitted(Expense expense) {
        if (expense.getStatus() != ExpenseStatus.SUBMITTED) {
            throw new IllegalStateException(
                    "Only submitted expenses can be modified");
        }
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

    public List<ExpenseResponseDTO> getAllExpenses() {
        requireManager();

        return expenseRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public ExpenseResponseDTO getExpenseById(Long id) {
        Expense expense = findExpense(id);

        requireSelfOrManager(expense.getEmployee().getId());

        return mapToResponse(expense);
    }

    public List<ExpenseResponseDTO> getExpensesByEmployee(
            Long employeeId) {

        requireSelfOrManager(employeeId);
        findEmployee(employeeId);

        return expenseRepository.findByEmployeeId(employeeId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public ExpenseResponseDTO createExpense(
            ExpenseRequestDTO request) {

        // Never trust employeeId from the request body.
        requireSelf(request.getEmployeeId());

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

    public ExpenseResponseDTO updateExpense(
            Long id, ExpenseRequestDTO request) {

        Expense expense = findExpense(id);
        Long ownerId = expense.getEmployee().getId();

        requireSelf(ownerId);

        // Prevent changing ownership through the request body.
        if (!ownerId.equals(request.getEmployeeId())) {
            throw new AccessDeniedException(
                    "You cannot change the expense owner");
        }

        ensureSubmitted(expense);

        expense.setAmount(request.getAmount());
        expense.setCategory(request.getCategory());
        expense.setDescription(request.getDescription());
        expense.setExpenseDate(request.getExpenseDate());

        return mapToResponse(expenseRepository.save(expense));
    }

    public void deleteExpense(Long id) {
        Expense expense = findExpense(id);

        requireSelf(expense.getEmployee().getId());
        ensureSubmitted(expense);

        expenseRepository.delete(expense);
    }

    public ExpenseResponseDTO approveExpense(Long id) {
        requireManager();

        Expense expense = findExpense(id);

        if (expenseAccess.isSelf(
                currentAuthentication(),
                expense.getEmployee().getId())) {
            throw new AccessDeniedException(
                    "Managers cannot approve their own expenses");
        }

        ensureSubmitted(expense);

        expense.setStatus(ExpenseStatus.APPROVED);

        return mapToResponse(expenseRepository.save(expense));
    }

    public ExpenseResponseDTO rejectExpense(Long id) {
        requireManager();

        Expense expense = findExpense(id);

        if (expenseAccess.isSelf(
                currentAuthentication(),
                expense.getEmployee().getId())) {
            throw new AccessDeniedException(
                    "Managers cannot reject their own expenses");
        }

        ensureSubmitted(expense);

        expense.setStatus(ExpenseStatus.REJECTED);

        return mapToResponse(expenseRepository.save(expense));
    }
}
