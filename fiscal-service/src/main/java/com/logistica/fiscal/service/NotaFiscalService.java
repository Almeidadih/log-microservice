package com.logistica.fiscal.service;

import com.logistica.fiscal.domain.CustomerId;
import com.logistica.fiscal.domain.NotaFiscal;
import com.logistica.fiscal.domain.NotaFiscalId;
import com.logistica.fiscal.domain.StatusNotaFiscal;
import com.logistica.fiscal.exception.NotaFiscalNaoEncontradException;
import com.logistica.fiscal.outbox.OutboxEvent;
import com.logistica.fiscal.outbox.OutboxEventRepository;
import com.logistica.fiscal.repository.NotaFiscalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
@Service
public class NotaFiscalService {

    private static final Logger log =  LoggerFactory.getLogger(NotaFiscalService.class);

    private final NotaFiscalRepository notaFiscalRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final TransactionalOperator transactionalOperator;

    public NotaFiscalService(NotaFiscalRepository notaFiscalRepository, OutboxEventRepository outboxEventRepository, TransactionalOperator transactionalOperator) {
        this.notaFiscalRepository = notaFiscalRepository;
        this.outboxEventRepository = outboxEventRepository;
        this.transactionalOperator = transactionalOperator;
    }
    public Mono<NotaFiscal> registrar(CustomerId customerId, String numero, BigDecimal valor) {
        NotaFiscal notaFiscal = NotaFiscal.registrar(customerId, numero, valor);
        return comContexto(notaFiscal.getId(), () -> {
            log.info("Registrando nota fiscal {} para cliente {}", notaFiscal.getId(), customerId);
            return salvarComEvento(notaFiscal, "NOTA_FISCAL_REGISTRADA");
        });
    }

    public Mono<NotaFiscal> alterarStatus(NotaFiscalId id, StatusNotaFiscal novoStatus) {
        return notaFiscalRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotaFiscalNaoEncontradException(id)))
                .flatMap(notaFiscal -> comContexto(id, () -> {
                    notaFiscal.alterarStatus(novoStatus);
                    log.info("Nota fiscal {} alterada para status {}", id, novoStatus);
                    return salvarComEvento(notaFiscal, "NOTA_FISCAL_STATUS_ALTERADO");
                }));
    }

    public Mono<NotaFiscal> buscarPorId(NotaFiscalId id) {
        return notaFiscalRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotaFiscalNaoEncontradException(id)));
    }

    /**
     * Salva a nota fiscal e grava o evento correspondente na tabela outbox_event
     * dentro da MESMA transação — o OutboxPublisher cuida de publicar no Kafka
     * depois, de forma assíncrona e resiliente (padrão transactional outbox).
     */
    private Mono<NotaFiscal> salvarComEvento(NotaFiscal notaFiscal, String tipoEvento) {
        Mono<NotaFiscal> operacao = notaFiscalRepository.save(notaFiscal)
                .flatMap(salva -> {
                    String payload = """
                            {"notaFiscalId":"%s","customerId":"%s","status":"%s"}"""
                            .formatted(salva.getId(), salva.getCustomerId(), salva.getStatus());
                    OutboxEvent evento = OutboxEvent.de(salva.getId().value(), tipoEvento, payload);
                    return outboxEventRepository.save(evento).thenReturn(salva);
                });
        return operacao.as(transactionalOperator::transactional);
    }

    /**
     * Injeta o NotaFiscalId no MDC do SLF4J para que todo log emitido durante
     * esta operação saia correlacionado — essencial para rastrear a nota fiscal
     * através dos microsserviços a partir dos logs.
     *
     * NOTA: como o MDC é baseado em ThreadLocal e o Reactor troca de thread entre
     * operadores, esta versão simples só garante a correlação nos logs síncronos
     * emitidos antes do primeiro operador assíncrono. Para propagação completa do
     * MDC ao longo de toda a cadeia reativa (inclusive após trocas de thread),
     * vale evoluir para a biblioteca io.micrometer:context-propagation.
     */
    private <T> Mono<T> comContexto(NotaFiscalId id, java.util.function.Supplier<Mono<T>> operacao) {
        return Mono.deferContextual(ctx -> {
            MDC.put("notaFiscalId", id.toString());
            try {
                return operacao.get();
            } finally {
                MDC.remove("notaFiscalId");
            }
        });
    }
}
