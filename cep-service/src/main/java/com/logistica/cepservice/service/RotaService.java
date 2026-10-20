package com.logistica.cepservice.service;

import com.logistica.cepservice.domain.Cep;
import com.logistica.cepservice.domain.Endereco;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class RotaService {

    private static final Logger log =  LoggerFactory.getLogger(RotaService.class);
    private static final double RAIO_TERRA_KM = 6371.0;

    private final CepLookupService cepLookupService;

    public RotaService(CepLookupService cepLookupService) {
        this.cepLookupService = cepLookupService;
    }
    /**
     * Busca origem e destino EM PARALELO (Mono.zip dispara os dois ao mesmo
     * tempo, não um depois do outro) e calcula a distância em linha reta
     * entre eles pela fórmula de Haversine. É uma aproximação — não é a
     * distância real de estrada — mas já dá uma noção de rota para o
     * dashboard, e é a base sobre a qual dá para plugar depois um serviço de
     * roteamento de verdade (OSRM, Google Directions, etc).
     */
    public Mono<ResultadoRota> calcular(Cep origemCep, Cep destinoCep) {
        return Mono.zip(cepLookupService.buscar(origemCep), cepLookupService.buscar(destinoCep))
                .map(tupla -> {
                    Endereco origem = tupla.getT1();
                    Endereco destino = tupla.getT2();
                    double distanciaKm = haversine(origem, destino);
                    log.info("Rota calculada de {} para {}: {} km",
                            origem.getCep().formatado(), destino.getCep().formatado(), distanciaKm);
                    return new ResultadoRota(origem, destino, distanciaKm);
                });
    }
    private double haversine(Endereco origem, Endereco destino) {
        double lat1 = Math.toRadians(origem.getLatitude());
        double lat2 = Math.toRadians(destino.getLatitude());
        double deltaLat = Math.toRadians(destino.getLatitude() - origem.getLatitude());
        double deltaLon = Math.toRadians(destino.getLongitude() - origem.getLongitude());

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                + Math.cos(lat1) * Math.cos(lat2) * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return Math.round(RAIO_TERRA_KM * c * 100.0) / 100.0;
    }

    public record ResultadoRota(Endereco origem,
                                Endereco destino,
                                double distanciaKm) {

    }
}
