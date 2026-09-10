package com.logistica.repository;

import com.logistica.domain.NotaFiscalId;
import com.logistica.domain.NotaFiscalRegistrada;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface NotaFiscalRegistradaRepository extends ReactiveCrudRepository<NotaFiscalRegistrada, NotaFiscalId> {
}
