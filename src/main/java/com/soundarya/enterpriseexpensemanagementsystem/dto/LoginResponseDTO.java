package com.soundarya.enterpriseexpensemanagementsystem.dto;

public record LoginResponseDTO(
        String accessToken,
        String tokenType,
        long expiresIn
) {}