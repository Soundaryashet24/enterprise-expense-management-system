
package com.soundarya.enterpriseexpensemanagementsystem.integration;

import com.soundarya.enterpriseexpensemanagementsystem.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EmployeeIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmployeeRepository employeeRepository;

    @BeforeEach
    void cleanDatabase() {
        employeeRepository.deleteAll();
    }

    // TEST 1: Create employee in the actual test database
    @Test
    void createEmployee_shouldSaveToDatabase() throws Exception {

        String requestJson = """
                {
                  "name": "Rahul",
                  "email": "rahul@example.com"
                }
                """;

        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Rahul"))
                .andExpect(jsonPath("$.email")
                        .value("rahul@example.com"));

        // Confirm data was actually saved
        org.junit.jupiter.api.Assertions.assertTrue(
                employeeRepository.findAll()
                        .stream()
                        .anyMatch(employee ->
                                employee.getEmail()
                                        .equals("rahul@example.com"))
        );
    }

    // TEST 2: GET all employees
    @Test
    void getAllEmployees_shouldReturn200() throws Exception {

        mockMvc.perform(get("/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    // TEST 3: GET nonexistent employee
    @Test
    void getNonexistentEmployee_shouldReturn404() throws Exception {

        mockMvc.perform(get("/employees/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    // TEST 4: POST invalid email
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
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }
}
