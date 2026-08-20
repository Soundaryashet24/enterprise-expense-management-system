package com.soundarya.enterpriseexpensemanagementsystem.controller;

import com.soundarya.enterpriseexpensemanagementsystem.entity.Employee;
import com.soundarya.enterpriseexpensemanagementsystem.repository.EmployeeRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeRepository employeeRepository;

    public EmployeeController(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }
//get all employee
    @GetMapping
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }
//get one employee
    @GetMapping("/{id}")
    public Employee getEmployeeById(@PathVariable Long id) {
        return employeeRepository.findById(id).orElse(null);
    }
//create employee
    @PostMapping
    public Employee createEmployee(@RequestBody Employee employee) {
        return employeeRepository.save(employee);
    }
    //update employee
    @PutMapping("/{id}")
    public Employee updateEmployee(@PathVariable Long id,
                                   @RequestBody Employee employeeDetails) {

        Employee employee = employeeRepository.findById(id).orElse(null);

        if (employee == null) {
            return null;
        }

        employee.setName(employeeDetails.getName());
        employee.setEmail(employeeDetails.getEmail());

        return employeeRepository.save(employee);
    }
    //delete employee
    @DeleteMapping("/{id}")
    public void deleteEmployee(@PathVariable Long id) {
        employeeRepository.deleteById(id);
    }
}