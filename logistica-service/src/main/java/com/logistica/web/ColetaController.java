package com.logistica.web;

import com.logistica.domain.*;
import com.logistica.service.ColetaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;


@Tag(name = "Coletas" , description = "Registro de coletas e confirmação de entregas ")
@RestController
@RequestMapping("/api/v1/coletas")
public class ColetaController {

    private final ColetaService  coletaService;

    public ColetaController(ColetaService coletaService) {
        this.coletaService = coletaService;
    }

    @Operation(summary = "Registra uma coleta",
            description = "Antes de salvar, consulta o cep-service (HTTP) para resolver a cidade/uf do "
                    + "CEP de origem, e valida que a nota fiscal, transportadora, motorista e caminhão já são "
                    + "conhecidos. Publica COLETA_REGISTRADA (via outbox) para o Kafka.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<EntityModel<ColetaResponse>> registrar(@Valid @RequestBody RegistrarColetaRequest request) {
        return coletaService.registrar(
                        new NotaFiscalId(request.notaFiscalId()),
                        new TransportadoraId(request.transportadoraId()),
                        new MotoristaId(request.motoristaId()),
                        new CaminhaoId(request.caminhaoId()),
                        request.cepOrigem())
                .map(this::montarModel);
    }


    /**
     * Marca a coleta como entregue — é essa chamada que dispara o evento
     * ENTREGA_CONCLUIDA (via outbox) consumido pelo avaliacao-service para
     * calcular a nota de agilidade automaticamente.
     */
    @Operation(summary = "Marca a coleta como entregue",
            description = "Publica ENTREGA_CONCLUIDA (via outbox) para o Kafka — é isso que o avaliacao-service "
                    + "consome para calcular a nota de agilidade automaticamente.")
    @PatchMapping("/{id}/entregar")
    public Mono<EntityModel<ColetaResponse>> entregar(@PathVariable String id) {
        return coletaService.entregar(ColetaId.de(id)).map(this::montarModel);
    }
    @GetMapping("/{id}")
    public Mono<EntityModel<ColetaResponse>> buscarPorId(@PathVariable String id) {
        // Implementação de busca fica como próximo passo (mesma ideia do
        // buscarPorId do fiscal-service); aqui o método existe principalmente
        // para o linkTo/methodOn conseguir montar a URL do self-link acima.
        return Mono.error(new UnsupportedOperationException("Ainda não implementado"));
    }

    private EntityModel<ColetaResponse> montarModel(Coleta coleta) {
        ColetaResponse response = new ColetaResponse(
                coleta.getId().value(), coleta.getNotaFiscalId().value(),
                coleta.getTransportadoraId().value(), coleta.getMotoristaId().value(),
                coleta.getCaminhaoId().value() , coleta.getCidade(), coleta.getUf(),
                coleta.getStatus(),coleta.getColetadaEm(),coleta.getEntregueEm());

        String id  = coleta.getId().toString();
        List<Link> links = new ArrayList<>();
        links.add(linkTo(methodOn(ColetaController.class).buscarPorId(id)).withSelfRel());

        // Só oferece o link de "entregar" se a coleta ainda não foi entregue
        // — mesma ideia de HATEOAS dirigido por estado usada no fiscal-service.
        if (coleta.getStatus() == StatusColeta.COLETADA) {
            links.add(linkTo(methodOn(ColetaController.class).entregar(id)).withRel("entregar"));
        }

        return EntityModel.of(response,links);
    }

    public record ColetaResponse(UUID id, UUID notaFiscalId, UUID transportadoraId, UUID motoristaId,
                                 UUID caminhaoId, String cidade, String uf, StatusColeta status,
                                 OffsetDateTime coletadaEm, OffsetDateTime entregueEm) {

    }

}
