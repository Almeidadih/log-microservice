package com.logistica.cepservice.web;


import com.logistica.cepservice.domain.Cep;
import com.logistica.cepservice.service.CepLookupService;
import com.logistica.cepservice.web.dto.EnderecoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;


@Tag(name = "CEPs" , description = "Consulta de endereços por CEP , com cache em Redis")
@RestController
@RequestMapping("/api/v1/ceps")
public class CepController {

    private final CepLookupService cepLookupService;

    public CepController(CepLookupService cepLookupService) {
        this.cepLookupService = cepLookupService;
    }

    @Operation(summary = "Busca o endereço de um CEP" ,
                description = "Consulta primeiro o Redis (cache-aside); em cado de miss , busca no Postgres e "
                           +  "grava de volta no cache (TTL de 24h).")
    @GetMapping("/{cep}")
    public Mono<EnderecoResponse> buscar (@PathVariable String cep) {
        return cepLookupService.buscar(Cep.de(cep)).map(EnderecoResponse::de);
    }
}
