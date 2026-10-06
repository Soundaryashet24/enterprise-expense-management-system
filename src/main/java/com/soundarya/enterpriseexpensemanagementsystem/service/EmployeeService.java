package com.soundarya.enterpriseexpensemanagementsystem.service;

import com.soundarya.enterpriseexpensemanagementsystem.entity.Employee;
import com.soundarya.enterpriseexpensemanagementsystem.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id).orElse(null);
    }

    public Employee createEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    public Employee updateEmployee(Long id, Employee employeeDetails) {

        Employee employee = employeeRepository.findById(id).orElse(null);

        if (employee == null) {
            return null;
        }

        employee.setName(employeeDetails.getName());
        employee.setEmail(employeeDetails.getEmail());

        return employeeRepository.save(employee);
    }

    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }
}