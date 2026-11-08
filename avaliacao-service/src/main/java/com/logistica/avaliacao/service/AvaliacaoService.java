package com.logistica.avaliacao.service;

import com.logistica.avaliacao.repository.ColetaProjetadaRepository;
import org.springframework.stereotype.Service;

@Service
public class AvaliacaoService {

    private final ColetaProjetadaRepository coletaProjetadaRepository;
    private final AvaliacaoRankingService avaliacaoRankingService;

    public AvaliacaoService(ColetaProjetadaRepository coletaProjetadaRepository,
                            AvaliacaoRankingService avaliacaoRankingService) {
        this.coletaProjetadaRepository = coletaProjetadaRepository;
        this.avaliacaoRankingService = avaliacaoRankingService;
    }
}
