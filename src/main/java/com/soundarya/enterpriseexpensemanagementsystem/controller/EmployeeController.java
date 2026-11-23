
package com.soundarya.enterpriseexpensemanagementsystem.controller;

import com.soundarya.enterpriseexpensemanagementsystem.dto.EmployeeRequestDTO;
import com.soundarya.enterpriseexpensemanagementsystem.dto.EmployeeResponseDTO;
import com.soundarya.enterpriseexpensemanagementsystem.dto.ExpenseResponseDTO;
import com.soundarya.enterpriseexpensemanagementsystem.service.EmployeeService;
import com.soundarya.enterpriseexpensemanagementsystem.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.security.access.prepost.PreAuthorize;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final ExpenseService expenseService;
    public EmployeeController(
            EmployeeService employeeService,
            ExpenseService expenseService) {

        this.employeeService = employeeService;
        this.expenseService = expenseService;
    }

    @GetMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<List<EmployeeResponseDTO>> getAllEmployees() {

        return ResponseEntity.ok(
                employeeService.getAllEmployees()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<EmployeeResponseDTO> getEmployeeById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                employeeService.getEmployeeById(id)
        );
    }

    @PostMapping
    @PreAuthorize("denyAll()")
    public ResponseEntity<EmployeeResponseDTO> createEmployee(
            @Valid @RequestBody EmployeeRequestDTO request) {

        EmployeeResponseDTO createdEmployee =
                employeeService.createEmployee(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdEmployee.getId())
                .toUri();

        return ResponseEntity.created(location)
                .body(createdEmployee);
    }

    @PutMapping("/{id}")
    @PreAuthorize("denyAll()")
    public ResponseEntity<EmployeeResponseDTO> updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeRequestDTO request) {

        EmployeeResponseDTO updatedEmployee =
                employeeService.updateEmployee(id, request);

        return ResponseEntity.ok(updatedEmployee);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("denyAll()")
    public ResponseEntity<Void> deleteEmployee(
            @PathVariable Long id) {

        employeeService.deleteEmployee(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/expenses")
    @PreAuthorize(
            "@expenseAccess.isSelfOrManager(authentication, #id)"
    )
    public ResponseEntity<List<ExpenseResponseDTO>> getEmployeeExpenses(
            @PathVariable("id") Long id) {

        return ResponseEntity.ok(
                expenseService.getExpensesByEmployee(id));
    }
}
