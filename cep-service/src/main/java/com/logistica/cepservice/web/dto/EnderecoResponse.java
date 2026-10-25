package com.logistica.cepservice.web.dto;

import com.logistica.cepservice.domain.Endereco;

public record EnderecoResponse(
        String cep,
        String logradouro,
        String bairro,
        String cidade ,
        String uf,
        double latitude,
        double longitude
) {

    public static EnderecoResponse de(Endereco endereco) {
        return new EnderecoResponse(
                endereco.getCep().formatado(),
                endereco.getLogradouro(),
                endereco.getBairro(),
                endereco.getCidade(),
                endereco.getUf(),
                endereco.getLatitude(),
                endereco.getLongitude()
        );
    }
}
