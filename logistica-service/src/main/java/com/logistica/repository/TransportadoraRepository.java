package com.logistica.repository;

import com.logistica.domain.Transportadora;
import com.logistica.domain.TransportadoraId;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface TransportadoraRepository extends ReactiveCrudRepository<Transportadora, TransportadoraId> {
}
