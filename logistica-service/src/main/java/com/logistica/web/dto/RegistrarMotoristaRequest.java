package com.logistica.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record RegistrarMotoristaRequest(
        @NotBlank String nome,
        @NotBlank String cnh,
        @NotNull UUID transportadoraId
        ) {
}
