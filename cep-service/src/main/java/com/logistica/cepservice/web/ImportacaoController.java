package com.logistica.cepservice.web;

import com.logistica.cepservice.service.CepImportService;
import com.logistica.cepservice.web.dto.ImportacaoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Tag(name = "Importação de CEPs" ,
        description = "Carga em lote da base de CEPs paralelizada com Virtual Threads")
@RestController
@RequestMapping("/api/v1/ceps")
public class ImportacaoController {

    private final CepImportService cepImportService;

    public ImportacaoController(CepImportService cepImportService) {
        this.cepImportService = cepImportService;
    }

    /**
     * Dispara a importação em lote (bloqueante por natureza — usa JDBC).
     * O Mono.fromCallable + subscribeOn(Schedulers.boundedElastic()) é o que
     * garante que essa chamada bloqueante não trave uma das poucas threads
     * do event loop do Netty/WebFlux — ela roda num scheduler feito
     * justamente para código bloqueante. Por dentro, o CepImportService ainda
     * paraleliza o trabalho pesado com virtual threads.
     */
    @Operation(summary = "Importa o CSV de exemplo da base de CEPs",
                description = "Divide o CSV em lotes e insere cada lote em paralelo usando "
                       + "Executors.newVirtualThreadPerTaskExecutor().")
    @PostMapping("/importar")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Mono<ImportacaoResponse> importar() {
        return Mono.fromCallable(cepImportService::importarDoCsvDeSeed)
                .subscribe(Schedulers.boundedElastic())
                .map(ImportacaoResponse::new);
    }
}
