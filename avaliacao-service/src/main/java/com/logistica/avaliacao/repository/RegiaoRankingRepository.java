package com.logistica.avaliacao.repository;

import com.logistica.avaliacao.domain.RegiaoRanking;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface RegiaoRankingRepository extends ReactiveCrudRepository<RegiaoRanking , Long> {

    // A ordenacao por media (soma/contagem) e feita no RankingService, na
    // leitura — nao da para expressar "ORDER BY soma/contagem" de forma
    // limpa via query method, e o volume de linhas aqui (uma por
    // transportadora+regiao) e pequeno o suficiente para ordenar em memoria.
}
