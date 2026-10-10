package com.soundarya.enterpriseexpensemanagementsystem.service;

import com.soundarya.enterpriseexpensemanagementsystem.dto.EmployeeRequestDTO;
import com.soundarya.enterpriseexpensemanagementsystem.dto.EmployeeResponseDTO;
import com.soundarya.enterpriseexpensemanagementsystem.entity.Employee;
import com.soundarya.enterpriseexpensemanagementsystem.exception.EmployeeNotFoundException;
import com.soundarya.enterpriseexpensemanagementsystem.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public List<EmployeeResponseDTO> getAllEmployees() {

        return employeeRepository.findAll()
                .stream()
                .map(employee -> new EmployeeResponseDTO(
                        employee.getId(),
                        employee.getName(),
                        employee.getEmail()
                ))
                .toList();
    }

    public EmployeeResponseDTO getEmployeeById(Long id) {

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() ->
                        new EmployeeNotFoundException(
                                "Employee not found with id: " + id
                        )
                );

        return new EmployeeResponseDTO(
                employee.getId(),
                employee.getName(),
                employee.getEmail()
        );
    }

    public EmployeeResponseDTO createEmployee(EmployeeRequestDTO request) {

        Employee employee = new Employee();

        employee.setName(request.getName());
        employee.setEmail(request.getEmail());

        Employee savedEmployee = employeeRepository.save(employee);

        return new EmployeeResponseDTO(
                savedEmployee.getId(),
                savedEmployee.getName(),
                savedEmployee.getEmail()
        );
    }

    public EmployeeResponseDTO updateEmployee(
            Long id,
            EmployeeRequestDTO request) {

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() ->
                        new EmployeeNotFoundException(
                                "Employee not found with id: " + id
                        )
                );

        employee.setName(request.getName());
        employee.setEmail(request.getEmail());

        Employee updatedEmployee = employeeRepository.save(employee);

        return new EmployeeResponseDTO(
                updatedEmployee.getId(),
                updatedEmployee.getName(),
                updatedEmployee.getEmail()
        );
    }

    public void deleteEmployee(Long id) {

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() ->
                        new EmployeeNotFoundException(
                                "Employee not found with id: " + id
                        )
                );

        employeeRepository.delete(employee);
    }
}