package com.logistica.web;


import com.logistica.domain.TransportadoraId;
import com.logistica.service.FrotaService;
import com.logistica.web.dto.RegistrarCaminhaoRequest;
import com.logistica.web.dto.RegistrarMotoristaRequest;
import com.logistica.web.dto.RegistrarTransportadoraRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Tag(name = "Frota " , description = "Cadastro de transportadoras , motoristas e caminhões ")
@RestController
@RequestMapping("/api/v1")
public class FrotaController {

    private final FrotaService frotaService;

    public FrotaController(FrotaService frotaService) {
        this.frotaService = frotaService;
    }

    @Operation(summary = "Registra uma transportadora")
    @PostMapping("/transportadoras")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<TransportadoraResponse> registrarTransportadora(@Valid @RequestBody RegistrarTransportadoraRequest request) {
        return frotaService.registrarTransportadora(request.nome(), request.cnpj())
                .map(t -> new TransportadoraResponse(t.getId().value(), t.getNome(), t.getCnpj()));
    }

    @Operation(summary = "Registra um motorista vinculado a uma transportadora",
            description = "Retorna 404 se a transportadora informada não existir.")
    @PostMapping("/motoristas")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<MotoristaResponse> registrarMotorista(@Valid @RequestBody RegistrarMotoristaRequest request) {
        return frotaService.registrarMotorista(request.nome(), request.cnh(), new TransportadoraId(request.transportadoraId()))
                .map(m -> new MotoristaResponse(m.getId().value(), m.getNome(), m.getCnh(), m.getTransportadoraId().value()));
    }

    @Operation(summary = "Registra um caminhão vinculado a uma transportadora",
            description = "Retorna 404 se a transportadora informada não existir.")
    @PostMapping("/caminhoes")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<CaminhaoResponse> registrarCaminhao(@Valid @RequestBody RegistrarCaminhaoRequest request) {
        return frotaService.registrarCaminhao(request.placa(), request.modelo(), new TransportadoraId(request.transportadoraId()))
                .map(c -> new CaminhaoResponse(c.getId().value(), c.getPlaca(), c.getModelo(), c.getTransportadoraId().value()));
    }



    public record TransportadoraResponse(UUID id, String nome, String cnpj) {

    }
    public record MotoristaResponse(UUID id, String nome, String cnh, java.util.UUID transportadoraId) {

    }
    public record CaminhaoResponse(UUID id, String placa, String modelo, java.util.UUID transportadoraId) {

    }
}
