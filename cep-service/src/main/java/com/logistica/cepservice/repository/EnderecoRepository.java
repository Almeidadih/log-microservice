package com.logistica.cepservice.repository;

import com.logistica.cepservice.domain.Cep;
import com.logistica.cepservice.domain.Endereco;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface EnderecoRepository extends ReactiveCrudRepository<Endereco, Cep> {
}
