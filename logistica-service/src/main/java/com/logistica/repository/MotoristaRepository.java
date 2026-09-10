package com.logistica.repository;

import com.logistica.domain.Motorista;
import com.logistica.domain.MotoristaId;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface MotoristaRepository extends ReactiveCrudRepository<Motorista, MotoristaId> {
}
