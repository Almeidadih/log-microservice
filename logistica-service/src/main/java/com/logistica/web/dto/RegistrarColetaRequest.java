package com.logistica.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record RegistrarColetaRequest(
     @NotNull UUID notaFiscalId,
     @NotNull UUID transportadoraId,
     @NotNull UUID motoristaId,
     @NotNull UUID caminhaoId,
     @NotBlank String cepOrigem
) {
}
