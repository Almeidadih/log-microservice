package com.logistica.cepclient;

/**
 * Espelha o JSON de resposta do endpoint GET /api/v1/ceps/{cep} do
 * cep-service. É uma cópia local, própria do logistica-service — o mesmo
 * princípio dos IDs tipados: cada serviço define os DTOs do que consome de
 * fora, em vez de importar uma classe do outro serviço.
 */
public record EnderecoExterno(
        String cep,
        String logradouro,
        String bairro,
        String cidade,
        String uf,
        double latitude,
        double longitude
) {
}
