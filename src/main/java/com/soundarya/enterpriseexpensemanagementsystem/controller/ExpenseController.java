
package com.soundarya.enterpriseexpensemanagementsystem.controller;

import com.soundarya.enterpriseexpensemanagementsystem.dto.ExpenseRequestDTO;
import com.soundarya.enterpriseexpensemanagementsystem.dto.ExpenseResponseDTO;
import com.soundarya.enterpriseexpensemanagementsystem.service.ExpenseService;
import com.soundarya.enterpriseexpensemanagementsystem.entity.ExpenseStatus;
import org.springframework.data.domain.Page;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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


    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping
    public ResponseEntity<Page<ExpenseResponseDTO>> getAllExpenses(
            @RequestParam(required = false) ExpenseStatus status,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Long employeeId,

            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,

            @RequestParam(defaultValue = "expenseDate") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Page<ExpenseResponseDTO> result =
                expenseService.searchExpenses(
                        status,
                        category,
                        employeeId,
                        page,
                        size,
                        sortBy,
                        direction
                );

        return ResponseEntity.ok(result);
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
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize(
            "@expenseAccess.isSelfOrManager(authentication, #employeeId)"
    )
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<ExpenseResponseDTO>> getExpensesByEmployee(
            @PathVariable("employeeId") Long employeeId) {

        return ResponseEntity.ok(
                expenseService.getExpensesByEmployee(employeeId));
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PatchMapping("/{id}/approve")
    public ResponseEntity<ExpenseResponseDTO> approveExpense(
            @PathVariable Long id) {

        return ResponseEntity.ok(expenseService.approveExpense(id));
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PatchMapping("/{id}/reject")
    public ResponseEntity<ExpenseResponseDTO> rejectExpense(
            @PathVariable Long id) {

        return ResponseEntity.ok(expenseService.rejectExpense(id));
    }
}
