
package com.soundarya.enterpriseexpensemanagementsystem.service;

import com.soundarya.enterpriseexpensemanagementsystem.dto.LoginRequestDTO;
import com.soundarya.enterpriseexpensemanagementsystem.dto.LoginResponseDTO;
import com.soundarya.enterpriseexpensemanagementsystem.repository.EmployeeRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;
    private final EmployeeRepository employeeRepository;

    @Value("${app.auth.employee.username}")
    private String employeeUsername;

    @Value("${app.auth.employee.id}")
    private Long employeeId;

    @Value("${app.auth.manager.username}")
    private String managerUsername;

    @Value("${app.auth.manager.id}")
    private Long managerId;

    @Value("${app.jwt.expiration-minutes}")
    private long expirationMinutes;

    public AuthService(
            AuthenticationManager authenticationManager,
            JwtEncoder jwtEncoder,
            EmployeeRepository employeeRepository) {

        this.authenticationManager = authenticationManager;
        this.jwtEncoder = jwtEncoder;
        this.employeeRepository = employeeRepository;
    }

    public LoginResponseDTO login(LoginRequestDTO request) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.username(),
                                request.password()
                        )
                );

        String username = authentication.getName();

        String role;
        Long linkedEmployeeId;

        if (username.equals(employeeUsername)) {
            role = "EMPLOYEE";
            linkedEmployeeId = employeeId;
        } else if (username.equals(managerUsername)) {
            role = "MANAGER";
            linkedEmployeeId = managerId;
        } else {
            throw new IllegalStateException(
                    "Authenticated user is not configured");
        }

        if (!employeeRepository.existsById(linkedEmployeeId)) {
            throw new IllegalStateException(
                    "Configured employee record does not exist");
        }

        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(expirationMinutes * 60);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("enterprise-expense-management")
                .subject(username)
                .issuedAt(now)
                .expiresAt(expiry)
                .claim("role", role)
                .claim("employeeId", linkedEmployeeId)
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256)
                .build();

        String token = jwtEncoder.encode(
                JwtEncoderParameters.from(header, claims)
        ).getTokenValue();

        return new LoginResponseDTO(
                token,
                "Bearer",
                expirationMinutes * 60
        );
    }
}
