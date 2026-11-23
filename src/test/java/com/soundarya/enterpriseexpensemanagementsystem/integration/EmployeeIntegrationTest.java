
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
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser(username = "testuser", roles = "USER")
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
    void createEmployee_shouldBeForbidden() throws Exception {

        mockMvc.perform(post("/employees")
                        .with(jwt().authorities(
                                new SimpleGrantedAuthority("ROLE_MANAGER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                    "name": "Test Employee",
                    "email": "test@example.com"
                }
                """))
                .andExpect(status().isForbidden());
    }

    // TEST 2: GET all employees
    @Test
    void getAllEmployees_shouldReturn200() throws Exception {

        mockMvc.perform(get("/employees")
                        .with(jwt().authorities(
                                new SimpleGrantedAuthority("ROLE_MANAGER"))))
                .andExpect(status().isOk());
    }

    // TEST 3: GET nonexistent employee
    @Test
    void getNonexistentEmployee_shouldReturn404() throws Exception {

        mockMvc.perform(get("/employees/999999")
                        .with(jwt().authorities(
                                new SimpleGrantedAuthority("ROLE_MANAGER"))))
                .andExpect(status().isNotFound());
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
