package com.logistica.repository;

import com.logistica.domain.Caminhao;
import com.logistica.domain.CaminhaoId;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface CaminhaoRepository extends ReactiveCrudRepository<Caminhao, CaminhaoId> {
}
