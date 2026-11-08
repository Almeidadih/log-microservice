package com.logistica.avaliacao.web.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record RegistrarAvaliacaoRequest(
        @NotNull UUID coletaId,
        @Min(1) @Max(5) int notaQualidade ,
        @Min(1) @Max(5) int notaAgilidade
        ) {
}
