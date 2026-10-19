package com.logistica.cepservice.service;

import com.logistica.cepservice.domain.Cep;
import com.logistica.cepservice.domain.Endereco;
import com.logistica.cepservice.exception.CepNaoEncontradoException;
import com.logistica.cepservice.repository.EnderecoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CepLookupService {

    private static final Logger log =  LoggerFactory.getLogger(CepLookupService.class);

    private final CepCacheService cacheService;
    private final EnderecoRepository enderecoRepository;

    public CepLookupService(CepCacheService cacheService, EnderecoRepository enderecoRepository) {
        this.cacheService = cacheService;
        this.enderecoRepository = enderecoRepository;
    }

    /**
     * Busca o endereço de um CEP seguindo a ordem: Redis -> Postgres.
     * Em caso de miss no cache mas hit no banco, grava de volta no Redis
     * ("cache-aside pattern") para que a próxima consulta do mesmo CEP nem
     * precise tocar no Postgres.
     */
    public Mono<Endereco> buscar(Cep cep){
        return cacheService.buscar(cep)
                .switchIfEmpty(buscarNoBancoEPopularCache(cep));
    }

    private Mono<Endereco> buscarNoBancoEPopularCache(Cep cep){
        log.debug("Cache MISS para o CEP {}", cep.formatado());
        return enderecoRepository.findById(cep)
                .switchIfEmpty(Mono.error(new CepNaoEncontradoException(cep)))
                .flatMap(endereco -> cacheService.salvar(endereco).thenReturn(endereco));
    }
}
