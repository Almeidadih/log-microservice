package com.logistica.fiscal.web;

import com.logistica.fiscal.domain.CustomerId;
import com.logistica.fiscal.domain.NotaFiscalId;
import com.logistica.fiscal.service.NotaFiscalService;
import com.logistica.fiscal.web.assembler.NotaFiscalModelAssembler;
import com.logistica.fiscal.web.dto.AlterarStatusRequest;
import com.logistica.fiscal.web.dto.NotaFiscalResponse;
import com.logistica.fiscal.web.dto.RegistrarNotaFiscalRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Tag(name = "Notas Fiscais", description = "Ciclo de vida da nota fiscal: registro, conferência e devolução")
@RestController
@RequestMapping("/api/v1/notas-fiscais")
public class NotaFiscalController {

    private final NotaFiscalService notaFiscalService;
    private final NotaFiscalModelAssembler assembler;

    public NotaFiscalController(NotaFiscalService notaFiscalService, NotaFiscalModelAssembler assembler) {
        this.notaFiscalService = notaFiscalService;
        this.assembler = assembler;
    }

    @Operation(summary = "Registra uma nova nota fiscal",
            description = "A nota nasce com status PENDENTE. Publica o evento NOTA_FISCAL_REGISTRADA "
                    + "(via outbox transacional) para o Kafka.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<EntityModel<NotaFiscalResponse>> registrar(@Valid @RequestBody RegistrarNotaFiscalRequest request) {
        return notaFiscalService.registrar(new CustomerId(request.customerId()), request.numero(), request.valor())
                .map(assembler::toModel);
    }

    @Operation(summary = "Busca uma nota fiscal pelo id",
            description = "A resposta inclui links HATEOAS com as transições de status válidas para o estado atual.")
    @GetMapping("/{id}")
    public Mono<EntityModel<NotaFiscalResponse>> buscarPorId(@PathVariable String id) {
        return notaFiscalService.buscarPorId(NotaFiscalId.de(id))
                .map(assembler::toModel);
    }

    @Operation(summary = "Altera o status da nota fiscal",
            description = "Só aceita transições válidas segundo a máquina de estados "
                    + "(PENDENTE → CONFERIDA/NAO_CONFERIDA → DEVOLUCAO; CANCELADA a partir de qualquer "
                    + "estado não-terminal). Transições inválidas retornam 422.")
    @PatchMapping("/{id}/status")
    public Mono<EntityModel<NotaFiscalResponse>> alterarStatus(@PathVariable String id,
                                                               @Valid @RequestBody AlterarStatusRequest request) {
        return notaFiscalService.alterarStatus(NotaFiscalId.de(id), request.status())
                .map(assembler::toModel);
    }
}
