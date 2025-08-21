package com.challenge.pinapp.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record ClientResponseDTO(
        Long id,
        String firstName,
        String lastName,
        Integer age,
        LocalDate birthDate,
        LocalDate estimatedDeathDate
) {}
