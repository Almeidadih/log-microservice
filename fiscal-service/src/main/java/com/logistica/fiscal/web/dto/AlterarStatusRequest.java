package com.logistica.fiscal.web.dto;

import com.logistica.fiscal.domain.StatusNotaFiscal;
import jakarta.validation.constraints.NotNull;

public record AlterarStatusRequest (
         @NotNull StatusNotaFiscal status
){
}
