
package com.soundarya.enterpriseexpensemanagementsystem.controller;

import com.soundarya.enterpriseexpensemanagementsystem.dto.LoginRequestDTO;
import com.soundarya.enterpriseexpensemanagementsystem.dto.LoginResponseDTO;
import com.soundarya.enterpriseexpensemanagementsystem.service.AuthService;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO request) {

        return ResponseEntity.ok(authService.login(request));
    }
    @GetMapping("/me")
    public Map<String, Object> getCurrentUser(
            @AuthenticationPrincipal Jwt jwt) {

        return Map.of(
                "username", jwt.getSubject(),
                "role", jwt.getClaimAsString("role"),
                "employeeId", jwt.getClaim("employeeId")
        );
    }
}
