package com.logistica.service;

import com.logistica.domain.*;
import com.logistica.exception.CaminhaoNaoEncontradoException;
import com.logistica.exception.MotoristaNaoEncontradoException;
import com.logistica.exception.TransportadoraNaoEncontradaException;
import com.logistica.repository.CaminhaoRepository;
import com.logistica.repository.MotoristaRepository;
import com.logistica.repository.TransportadoraRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class FrotaService {

    private static final Logger log = LoggerFactory.getLogger(FrotaService.class);

    private final TransportadoraRepository transportadoraRepository;
    private final MotoristaRepository motoristaRepository;
    private final CaminhaoRepository caminhaoRepository;

    public FrotaService(TransportadoraRepository transportadoraRepository,
                        MotoristaRepository motoristaRepository,
                        CaminhaoRepository caminhaoRepository) {
        this.transportadoraRepository = transportadoraRepository;
        this.motoristaRepository = motoristaRepository;
        this.caminhaoRepository = caminhaoRepository;
    }
    public Mono<Transportadora> registrarTransportadora(String nome , String cnpj) {
        log.info("Registrando transportadora {}", nome);
        return transportadoraRepository.save(Transportadora.registrar(nome, cnpj));
    }

    public Mono<Motorista> registrarMotorista(String nome , String cnh, TransportadoraId transportadoraId) {
        return transportadoraRepository.findById(transportadoraId)
                .switchIfEmpty(Mono.error(new TransportadoraNaoEncontradaException(transportadoraId)))
                .flatMap(t -> {
                    log.info("Registrando motorista {} para transportadfora {} " , nome , transportadoraId);
                    return motoristaRepository.save(Motorista.registrar(nome, cnh, transportadoraId));
                });
    }

    public Mono<Caminhao> registrarCaminhao(String placa  , String modelo , TransportadoraId transportadoraId ) {
        return transportadoraRepository.findById(transportadoraId)
                .switchIfEmpty(Mono.error(new TransportadoraNaoEncontradaException(transportadoraId)))
                .flatMap(t -> {
                    log.info("Registrando caminhão {} para transportadora {} " , placa ,  transportadoraId);
                    return caminhaoRepository.save(Caminhao.registrar(placa, modelo, transportadoraId));
                });
    }
    public Mono<Motorista> buscarMotorista(MotoristaId id){
        return motoristaRepository.findById(id)
                .switchIfEmpty(Mono.error(new MotoristaNaoEncontradoException(id)));
    }

    public Mono<Caminhao> buscarCaminhao(CaminhaoId id){
        return caminhaoRepository.findById(id).switchIfEmpty(Mono.error(new CaminhaoNaoEncontradoException(id)));
    }

    public Mono<Transportadora> buscarTransportadora(TransportadoraId id){
        return transportadoraRepository.findById(id)
                .switchIfEmpty(Mono.error( new TransportadoraNaoEncontradaException( id)));
    }
}
