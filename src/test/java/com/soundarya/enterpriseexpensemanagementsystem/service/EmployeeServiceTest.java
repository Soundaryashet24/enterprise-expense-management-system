
package com.soundarya.enterpriseexpensemanagementsystem.service;

import com.soundarya.enterpriseexpensemanagementsystem.dto.EmployeeRequestDTO;
import com.soundarya.enterpriseexpensemanagementsystem.dto.EmployeeResponseDTO;
import com.soundarya.enterpriseexpensemanagementsystem.entity.Employee;
import com.soundarya.enterpriseexpensemanagementsystem.exception.EmployeeNotFoundException;
import com.soundarya.enterpriseexpensemanagementsystem.repository.EmployeeRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;

    // Helper method to create test employees
    private Employee createEmployee(
            Long id, String name, String email) {

        Employee employee = new Employee();
        ReflectionTestUtils.setField(employee, "id", id);
        employee.setName(name);
        employee.setEmail(email);

        return employee;
    }

    // TEST 1: Get all employees
    @Test
    void getAllEmployees_shouldReturnEmployees() {

        Employee e1 = createEmployee(
                1L, "Rahul", "rahul@example.com");

        Employee e2 = createEmployee(
                2L, "Priya", "priya@example.com");

        when(employeeRepository.findAll())
                .thenReturn(List.of(e1, e2));

        List<EmployeeResponseDTO> result =
                employeeService.getAllEmployees();

        assertEquals(2, result.size());
        assertEquals("Rahul", result.get(0).getName());
        assertEquals("Priya", result.get(1).getName());

        verify(employeeRepository).findAll();
    }

    // TEST 2: Get all employees when database is empty
    @Test
    void getAllEmployees_whenEmpty_shouldReturnEmptyList() {

        when(employeeRepository.findAll())
                .thenReturn(List.of());

        List<EmployeeResponseDTO> result =
                employeeService.getAllEmployees();

        assertTrue(result.isEmpty());

        verify(employeeRepository).findAll();
    }

    // TEST 3: Get employee by valid ID
    @Test
    void getEmployeeById_shouldReturnEmployee() {

        Employee employee = createEmployee(
                1L, "Rahul", "rahul@example.com");

        when(employeeRepository.findById(1L))
                .thenReturn(Optional.of(employee));

        EmployeeResponseDTO result =
                employeeService.getEmployeeById(1L);

        assertEquals(1L, result.getId());
        assertEquals("Rahul", result.getName());
        assertEquals("rahul@example.com", result.getEmail());

        verify(employeeRepository).findById(1L);
    }

    // TEST 4: Get employee with invalid ID
    @Test
    void getEmployeeById_whenMissing_shouldThrowException() {

        when(employeeRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                EmployeeNotFoundException.class,
                () -> employeeService.getEmployeeById(99L)
        );

        verify(employeeRepository).findById(99L);
    }

    // TEST 5: Create employee
    @Test
    void createEmployee_shouldSaveEmployee() {

        EmployeeRequestDTO request =
                new EmployeeRequestDTO();

        request.setName("Rahul");
        request.setEmail("rahul@example.com");

        when(employeeRepository.save(any(Employee.class)))
                .thenAnswer(invocation -> {

                    Employee employee =
                            invocation.getArgument(0);

                    ReflectionTestUtils.setField(
                            employee, "id", 1L);

                    return employee;
                });

        EmployeeResponseDTO result =
                employeeService.createEmployee(request);

        assertEquals(1L, result.getId());
        assertEquals("Rahul", result.getName());
        assertEquals("rahul@example.com", result.getEmail());

        verify(employeeRepository).save(any(Employee.class));
    }

    // TEST 6: Update existing employee
    @Test
    void updateEmployee_shouldUpdateEmployee() {

        Employee existing = createEmployee(
                1L, "Old Name", "old@example.com");

        EmployeeRequestDTO request =
                new EmployeeRequestDTO();

        request.setName("New Name");
        request.setEmail("new@example.com");

        when(employeeRepository.findById(1L))
                .thenReturn(Optional.of(existing));

        when(employeeRepository.save(any(Employee.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        EmployeeResponseDTO result =
                employeeService.updateEmployee(1L, request);

        assertEquals("New Name", result.getName());
        assertEquals("new@example.com", result.getEmail());

        verify(employeeRepository).findById(1L);
        verify(employeeRepository).save(existing);
    }

    // TEST 7: Update employee with invalid ID
    @Test
    void updateEmployee_whenMissing_shouldThrowException() {

        EmployeeRequestDTO request =
                new EmployeeRequestDTO();

        request.setName("Rahul");
        request.setEmail("rahul@example.com");

        when(employeeRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                EmployeeNotFoundException.class,
                () -> employeeService.updateEmployee(99L, request)
        );

        verify(employeeRepository).findById(99L);
        verify(employeeRepository, never())
                .save(any(Employee.class));
    }

    // TEST 8: Delete existing employee
    @Test
    void deleteEmployee_shouldDeleteEmployee() {

        Employee employee = createEmployee(
                1L, "Rahul", "rahul@example.com");

        when(employeeRepository.findById(1L))
                .thenReturn(Optional.of(employee));

        employeeService.deleteEmployee(1L);

        verify(employeeRepository).findById(1L);
        verify(employeeRepository).delete(employee);
    }

    // TEST 9: Delete employee with invalid ID
    @Test
    void deleteEmployee_whenMissing_shouldThrowException() {

        when(employeeRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                EmployeeNotFoundException.class,
                () -> employeeService.deleteEmployee(99L)
        );

        verify(employeeRepository).findById(99L);
        verify(employeeRepository, never())
                .delete(any(Employee.class));
    }
}
