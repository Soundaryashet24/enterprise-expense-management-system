
package com.soundarya.enterpriseexpensemanagementsystem.controller;

import com.soundarya.enterpriseexpensemanagementsystem.dto.ExpenseRequestDTO;
import com.soundarya.enterpriseexpensemanagementsystem.dto.ExpenseResponseDTO;
import com.soundarya.enterpriseexpensemanagementsystem.service.ExpenseService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping
    public ResponseEntity<List<ExpenseResponseDTO>> getAllExpenses() {

        return ResponseEntity.ok(expenseService.getAllExpenses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExpenseResponseDTO> getExpenseById(
            @PathVariable Long id) {

        return ResponseEntity.ok(expenseService.getExpenseById(id));
    }

    @PostMapping
    public ResponseEntity<ExpenseResponseDTO> createExpense(
            @Valid @RequestBody ExpenseRequestDTO request) {

        ExpenseResponseDTO created = expenseService.createExpense(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExpenseResponseDTO> updateExpense(
            @PathVariable Long id,
            @Valid @RequestBody ExpenseRequestDTO request) {

        return ResponseEntity.ok(
                expenseService.updateExpense(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(
            @PathVariable Long id) {

        expenseService.deleteExpense(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<ExpenseResponseDTO>>
    getExpensesByEmployee(@PathVariable Long employeeId) {

        return ResponseEntity.ok(
                expenseService.getExpensesByEmployee(employeeId));
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<ExpenseResponseDTO> approveExpense(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                expenseService.approveExpense(id));
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<ExpenseResponseDTO> rejectExpense(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                expenseService.rejectExpense(id));
    }
}