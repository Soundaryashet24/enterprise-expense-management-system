
package com.soundarya.enterpriseexpensemanagementsystem.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EmployeeSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void withoutAuthentication_shouldReturn401()
            throws Exception {

        mockMvc.perform(get("/employees"))
                .andExpect(status().isUnauthorized());
    }


    @Test
    void withValidJwt_shouldReturn200() throws Exception {

        mockMvc.perform(get("/employees")
                        .with(jwt().authorities(
                                new SimpleGrantedAuthority("ROLE_MANAGER"))))
                .andExpect(status().isOk());
    }
    @Test
    void withWrongPassword_shouldReturn401()
            throws Exception {

        mockMvc.perform(get("/employees")
                        .with(httpBasic(
                                "testuser",
                                "wrong-password")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createEmployeeWithoutAuthentication_shouldReturn401()
            throws Exception {

        String requestJson = """
                {
                    "name": "Rahul",
                    "email": "rahul@example.com"
                }
                """;

        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isUnauthorized());
    }
}