package com.logistica.avaliacao.repository;

import com.logistica.avaliacao.domain.ColetaId;
import com.logistica.avaliacao.domain.ColetaProjetada;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ColetaProjetadaRepository extends ReactiveCrudRepository<ColetaProjetada, ColetaId> {
}
