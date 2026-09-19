package com.logistica.web.dto;

import jakarta.validation.constraints.NotBlank;

public record RegistrarTransportadoraRequest(
        @NotBlank String nome,
        @NotBlank String cnpj
) {
}
