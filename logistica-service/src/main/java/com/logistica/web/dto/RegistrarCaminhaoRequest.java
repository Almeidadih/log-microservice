package com.logistica.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record RegistrarCaminhaoRequest(
        @NotBlank String placa,
        @NotBlank String modelo,
        @NotNull  UUID transportadoraId
        ) {
}
