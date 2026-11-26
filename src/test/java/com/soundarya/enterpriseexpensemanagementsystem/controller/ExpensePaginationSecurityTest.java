
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ExpensePaginationSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void managerCanAccessPaginatedExpenses() throws Exception {
        mockMvc.perform(get("/expenses")
                        .param("page", "0")
                        .param("size", "5")
                        .with(jwt().authorities(
                                new SimpleGrantedAuthority("ROLE_MANAGER"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void employeeCannotAccessPaginatedExpenses() throws Exception {
        mockMvc.perform(get("/expenses")
                        .param("page", "0")
                        .param("size", "5")
                        .with(jwt().authorities(
                                new SimpleGrantedAuthority("ROLE_EMPLOYEE"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void unauthenticatedUserCannotAccessExpenses() throws Exception {
        mockMvc.perform(get("/expenses"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void managerCanFilterByStatus() throws Exception {
        mockMvc.perform(get("/expenses")
                        .param("status", "SUBMITTED")
                        .with(jwt().authorities(
                                new SimpleGrantedAuthority("ROLE_MANAGER"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }
}
