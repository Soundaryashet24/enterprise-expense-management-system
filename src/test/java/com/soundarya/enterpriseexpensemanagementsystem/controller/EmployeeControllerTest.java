
package com.soundarya.enterpriseexpensemanagementsystem.controller;
import com.soundarya.enterpriseexpensemanagementsystem.exception.GlobalExceptionHandler;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.soundarya.enterpriseexpensemanagementsystem.dto.EmployeeResponseDTO;
import com.soundarya.enterpriseexpensemanagementsystem.exception.EmployeeNotFoundException;
import com.soundarya.enterpriseexpensemanagementsystem.service.EmployeeService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EmployeeControllerTest {

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeController employeeController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {

        LocalValidatorFactoryBean validator =
                new LocalValidatorFactoryBean();

        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(employeeController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
    }

    // TEST 1: GET all employees
    @Test
    void getAllEmployees_shouldReturn200() throws Exception {

        when(employeeService.getAllEmployees())
                .thenReturn(List.of());

        mockMvc.perform(get("/employees"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(employeeService).getAllEmployees();
    }

    // TEST 2: GET employee by ID
    @Test
    void getEmployeeById_shouldReturn200() throws Exception {

        EmployeeResponseDTO employee =
                new EmployeeResponseDTO(
                        1L, "Rahul", "rahul@example.com");

        when(employeeService.getEmployeeById(1L))
                .thenReturn(employee);

        mockMvc.perform(get("/employees/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Rahul"))
                .andExpect(jsonPath("$.email")
                        .value("rahul@example.com"));

        verify(employeeService).getEmployeeById(1L);
    }

    // TEST 3: POST create employee
    @Test
    void createEmployee_shouldReturn201() throws Exception {

        EmployeeResponseDTO created =
                new EmployeeResponseDTO(
                        1L, "Rahul", "rahul@example.com");

        when(employeeService.createEmployee(any()))
                .thenReturn(created);

        String requestJson = """
                {
                  "name": "Rahul",
                  "email": "rahul@example.com"
                }
                """;

        mockMvc.perform(post("/employees")
                        .contentType("application/json")
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(header().string(
                        "Location", "http://localhost/employees/1"))
                .andExpect(jsonPath("$.name").value("Rahul"));

        verify(employeeService).createEmployee(any());
    }

    // TEST 4: PUT update employee
    @Test
    void updateEmployee_shouldReturn200() throws Exception {

        EmployeeResponseDTO updated =
                new EmployeeResponseDTO(
                        1L, "Updated Rahul",
                        "updated@example.com");

        when(employeeService.updateEmployee(eq(1L), any()))
                .thenReturn(updated);

        String requestJson = """
                {
                  "name": "Updated Rahul",
                  "email": "updated@example.com"
                }
                """;

        mockMvc.perform(put("/employees/1")
                        .contentType("application/json")
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name")
                        .value("Updated Rahul"))
                .andExpect(jsonPath("$.email")
                        .value("updated@example.com"));

        verify(employeeService).updateEmployee(eq(1L), any());
    }

    // TEST 5: DELETE employee
    @Test
    void deleteEmployee_shouldReturn204() throws Exception {

        mockMvc.perform(delete("/employees/1"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(employeeService).deleteEmployee(1L);
    }

    // TEST 6: GET nonexistent employee -> 404
    @Test
    void getEmployeeById_shouldReturn404() throws Exception {

        when(employeeService.getEmployeeById(99L))
                .thenThrow(new EmployeeNotFoundException(
                        "Employee not found with id: 99"));

        mockMvc.perform(get("/employees/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value("Employee not found with id: 99"));

        verify(employeeService).getEmployeeById(99L);
    }

    // TEST 7: POST invalid email -> 400
    @Test
    void createEmployee_withInvalidEmail_shouldReturn400()
            throws Exception {

        String invalidJson = """
                {
                  "name": "Rahul",
                  "email": "invalid-email"
                }
                """;

        mockMvc.perform(post("/employees")
                        .contentType("application/json")
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("Request validation failed"))
                .andExpect(jsonPath("$.fieldErrors.email")
                        .value("Please provide a valid email address"));

        verify(employeeService, never())
                .createEmployee(any());
    }

    // TEST 8: PUT nonexistent employee -> 404
    @Test
    void updateEmployee_shouldReturn404() throws Exception {

        when(employeeService.updateEmployee(eq(99L), any()))
                .thenThrow(new EmployeeNotFoundException(
                        "Employee not found with id: 99"));

        String requestJson = """
                {
                  "name": "Updated Rahul",
                  "email": "rahul@example.com"
                }
                """;

        mockMvc.perform(put("/employees/99")
                        .contentType("application/json")
                        .content(requestJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value("Employee not found with id: 99"));

        verify(employeeService).updateEmployee(eq(99L), any());
    }

    // TEST 9: DELETE nonexistent employee -> 404
    @Test
    void deleteEmployee_shouldReturn404() throws Exception {

        doThrow(new EmployeeNotFoundException(
                "Employee not found with id: 99"))
                .when(employeeService).deleteEmployee(99L);

        mockMvc.perform(delete("/employees/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value("Employee not found with id: 99"));

        verify(employeeService).deleteEmployee(99L);
    }

}
