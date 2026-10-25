package com.logistica.cepservice.web.dto;

import com.logistica.cepservice.service.RotaService;

public record RotaResponse(
        EnderecoResponse origem,
        EnderecoResponse destino,
        double distanciaKm
) {

    public static RotaResponse de(RotaService.ResultadoRota resultado) {
        return new RotaResponse(
                EnderecoResponse.de(resultado.origem()),
                EnderecoResponse.de(resultado.destino()),
                resultado.distanciaKm()
        );

    }
}
