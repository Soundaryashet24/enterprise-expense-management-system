
package com.soundarya.enterpriseexpensemanagementsystem.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ExpenseControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void employeeCannotViewAllExpenses() throws Exception {
        mockMvc.perform(get("/expenses")
                        .with(jwt()
                                .jwt(token -> token
                                        .subject("employee1")
                                        .claim("role", "EMPLOYEE")
                                        .claim("employeeId", 1L))
                                .authorities(
                                        new SimpleGrantedAuthority("ROLE_EMPLOYEE"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void managerCanViewAllExpenses() throws Exception {
        mockMvc.perform(get("/expenses")
                        .with(jwt()
                                .jwt(token -> token
                                        .subject("manager1")
                                        .claim("role", "MANAGER")
                                        .claim("employeeId", 2L))
                                .authorities(
                                        new SimpleGrantedAuthority("ROLE_MANAGER"))))
                .andExpect(status().isOk());
    }

    @Test
    void unauthenticatedUserCannotViewExpenses() throws Exception {
        mockMvc.perform(get("/expenses"))
                .andExpect(status().isUnauthorized());
    }
}
