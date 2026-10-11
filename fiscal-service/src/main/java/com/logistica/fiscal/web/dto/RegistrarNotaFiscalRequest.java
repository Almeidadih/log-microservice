package com.logistica.fiscal.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record RegistrarNotaFiscalRequest(
        @NotNull UUID customerId,
        @NotBlank String numero,
        @NotNull @DecimalMin("0.01") BigDecimal valor
) {
}
