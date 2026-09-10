package com.logistica.repository;

import com.logistica.domain.Coleta;
import com.logistica.domain.ColetaId;
import com.logistica.domain.NotaFiscalId;
import org.springframework.boot.web.server.autoconfigure.ServerProperties;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface ColetaRepository extends ReactiveCrudRepository<Coleta, ColetaId> {
    // Query derivada do nome do metodo — o Spring Data monta o SQL sozinho
    // a partir de "findByNotaFiscalId". Usada pela compensacao: dado o id
    // de uma nota fiscal cancelada, achar a coleta correspondente (se
    // existir alguma).
    Mono<Coleta> findByNotaFiscalId(NotaFiscalId notaFiscalId);
}
