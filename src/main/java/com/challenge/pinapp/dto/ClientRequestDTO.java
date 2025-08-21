package com.challenge.pinapp.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record ClientRequestDTO(
        @NotBlank(message = "El nombre es obligatorio")
        String firstName,

        @NotBlank(message = "El apellido es obligatorio")
        String lastName,

        @NotNull(message = "La edad es obligatorio")
        @Min(value = 0, message = "Debe ser Mayor a 0")
        int age,

        @NotNull(message = "Fecha de nacimiento obligatorio")
        @Past(message = "La fecha de nacimiento debe ser inferior")
        LocalDate birthDate
) {}
