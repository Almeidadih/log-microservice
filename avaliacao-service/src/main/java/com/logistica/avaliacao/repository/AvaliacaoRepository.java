package com.logistica.avaliacao.repository;

import com.logistica.avaliacao.domain.AvaliacaoId;
import com.logistica.avaliacao.domain.ColetaProjetada;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface AvaliacaoRepository extends ReactiveCrudRepository<ColetaProjetada, AvaliacaoId> {
}
