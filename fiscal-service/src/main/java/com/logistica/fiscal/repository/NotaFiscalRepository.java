package com.logistica.fiscal.repository;

import com.logistica.fiscal.domain.NotaFiscal;
import com.logistica.fiscal.domain.NotaFiscalId;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface NotaFiscalRepository extends ReactiveCrudRepository<NotaFiscal, NotaFiscalId> {
}
