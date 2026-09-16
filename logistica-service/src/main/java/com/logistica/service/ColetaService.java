package com.logistica.service;

import com.logistica.cepclient.CepClient;
import com.logistica.cepclient.EnderecoExterno;
import com.logistica.domain.*;
import com.logistica.exception.*;
import com.logistica.outbox.OutboxEvent;
import com.logistica.outbox.OutboxEventRepository;
import com.logistica.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ColetaService {

    private static  final Logger log = LoggerFactory.getLogger(ColetaService.class);

    private final ColetaRepository coletaRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final NotaFiscalRegistradaRepository notaFiscalRegistradaRepository;
    private final TransportadoraRepository transportadoraRepository;
    private final MotoristaRepository motoristaRepository;
    private final CaminhaoRepository caminhaoRepository;
    private final CepClient cepClient;


    public ColetaService(ColetaRepository coletaRepository,
                         OutboxEventRepository outboxEventRepository,
                         NotaFiscalRegistradaRepository notaFiscalRegistradaRepository,
                         TransportadoraRepository transportadoraRepository,
                         MotoristaRepository motoristaRepository,
                         CaminhaoRepository caminhaoRepository,
                         CepClient cepClient) {
        this.coletaRepository = coletaRepository;
        this.outboxEventRepository = outboxEventRepository;
        this.notaFiscalRegistradaRepository = notaFiscalRegistradaRepository;
        this.transportadoraRepository = transportadoraRepository;
        this.motoristaRepository = motoristaRepository;
        this.caminhaoRepository = caminhaoRepository;
        this.cepClient = cepClient;
    }

    /**
     * Fluxo completo de registro de uma coleta:
     *  1) valida que a nota fiscal, a transportadora, o motorista e o
     *     caminhão existem;
     *  2) chama o cep-service (HTTP, via CepClient) para resolver a
     *     cidade/uf do CEP de origem — ANTES de qualquer escrita no banco,
     *     porque é uma chamada de rede e não deve ficar dentro da transação;
     *  3) com a região já em mãos, salva a Coleta e o evento do outbox na
     *     MESMA transação (mesmo padrão do fiscal-service), incluindo a
     *     região no payload — é isso que vai alimentar o avaliacao-service,
     *     que precisa saber "de qual região" foi cada coleta.
     */
    public Mono<Coleta> registrar(NotaFiscalId notaFiscalId, TransportadoraId transportadoraId,
                                  MotoristaId motoristaId, CaminhaoId caminhaoId, String cepOrigem) {
        return validarPrerequisitos(notaFiscalId, transportadoraId, motoristaId, caminhaoId)
                .then(cepClient.resolver(cepOrigem))
                .flatMap(endereco -> salvarComEvento(notaFiscalId, transportadoraId, motoristaId, caminhaoId,
                        cepOrigem, endereco));
    }

    /**
     * Marca a coleta como entregue e publica o evento ENTREGA_CONCLUIDA
     * (mesmo padrão de outbox transacional do registrar() acima — muda só
     * o tipo de evento e o payload). É esse evento que o avaliacao-service
     * vai consumir para calcular automaticamente a nota de agilidade, com
     * base no tempo entre a coleta e a entrega.
     */
    public Mono<Coleta> entregar(ColetaId coletaId) {
        return coletaRepository.findById(coletaId)
                .switchIfEmpty(Mono.error(new ColetaNaoEncontradaException(coletaId)))
                .flatMap(coleta -> {
                    coleta.marcarComoEntregue();
                    log.info("Coleta {} marcada como entregue às {}", coletaId, coleta.getEntregueEm());

                    Mono<Coleta> operacao = coletaRepository.save(coleta)
                            .flatMap(salva -> {
                                String payload = """
                                        {"coletaId":"%s","transportadoraId":"%s","cidade":"%s","uf":"%s","coletadaEm":"%s","entregueEm":"%s"}"""
                                        .formatted(salva.getId(), salva.getTransportadoraId(), salva.getCidade(),
                                                salva.getUf(), salva.getColetadaEm(), salva.getEntregueEm());
                                OutboxEvent evento = OutboxEvent.de(salva.getId().value(), "ENTREGA_CONCLUIDA", payload);
                                return outboxEventRepository.save(evento).thenReturn(salva);
                            });

                    return operacao.as(transactionalOperator::transactional);
                });
    }

    /**
     * Reage a um cancelamento de nota fiscal, tentando compensar a coleta
     * correspondente — se existir uma.
     *
     * Três desfechos possíveis, e cada um conta uma parte diferente da
     * história de "compensação em sistemas distribuídos":
     *
     *  1) Não existe coleta para essa nota fiscal ainda (ninguém pegou a
     *     mercadoria) — não há nada a compensar. Log e segue, sem erro.
     *  2) A coleta existe e ainda está COLETADA (a caminho) — cancelamos
     *     de verdade, e publicamos COLETA_CANCELADA para o avaliacao-service
     *     também reagir (ver AvaliacaoRankingService.compensar lá).
     *  3) A coleta já foi ENTREGUE — a compensação para aqui. Não existe
     *     jeito de desfazer uma entrega física por software; isso vira um
     *     log de alerta pedindo intervenção humana, não uma exceção que
     *     derruba o processamento do evento.
     */
    public Mono<Void> cancelarPorNotaFiscal(NotaFiscalId notaFiscalId) {
        return coletaRepository.findByNotaFiscalId(notaFiscalId)
                .flatMap(coleta -> {
                    if (coleta.getStatus() == StatusColeta.ENTREGUE) {
                        log.warn("Nota fiscal {} foi cancelada, mas a coleta {} já foi ENTREGUE — "
                                        + "compensação automática não é possível, intervenção manual necessária",
                                notaFiscalId, coleta.getId());
                        return Mono.empty();
                    }
                    if (coleta.getStatus() == StatusColeta.CANCELADA) {
                        log.info("Coleta {} já estava cancelada (mensagem duplicada, ignorando)", coleta.getId());
                        return Mono.empty();
                    }

                    coleta.cancelar();
                    log.info("Compensando: coleta {} cancelada em reação ao cancelamento da nota fiscal {}",
                            coleta.getId(), notaFiscalId);

                    Mono<Void> operacao = coletaRepository.save(coleta)
                            .flatMap(salva -> {
                                String payload = """
                                        {"coletaId":"%s","notaFiscalId":"%s","transportadoraId":"%s"}"""
                                        .formatted(salva.getId(), salva.getNotaFiscalId(), salva.getTransportadoraId());
                                OutboxEvent evento = OutboxEvent.de(salva.getId().value(), "COLETA_CANCELADA", payload);
                                return outboxEventRepository.save(evento);
                            })
                            .then();

                    return operacao.as(transactionalOperator::transactional);
                })
                .switchIfEmpty(Mono.fromRunnable(() ->
                        log.debug("Nota fiscal {} cancelada, mas nenhuma coleta associada foi encontrada — nada a compensar",
                                notaFiscalId)))
                .then();
    }

    private Mono<Void> validarPrerequisitos(NotaFiscalId notaFiscalId, TransportadoraId transportadoraId,
                                            MotoristaId motoristaId, CaminhaoId caminhaoId) {
        return notaFiscalRegistradaRepository.findById(notaFiscalId)
                .switchIfEmpty(Mono.error(new NotaFiscalDesconhecidaException(notaFiscalId)))
                .then(transportadoraRepository.findById(transportadoraId))
                .switchIfEmpty(Mono.error(new TransportadoraNaoEncontradaException(transportadoraId)))
                .then(motoristaRepository.findById(motoristaId))
                .switchIfEmpty(Mono.error(new MotoristaNaoEncontradoException(motoristaId)))
                .then(caminhaoRepository.findById(caminhaoId))
                .switchIfEmpty(Mono.error(new CaminhaoNaoEncontradoException(caminhaoId)))
                .then();
    }

    private Mono<Coleta> salvarComEvento(NotaFiscalId notaFiscalId, TransportadoraId transportadoraId,
                                         MotoristaId motoristaId, CaminhaoId caminhaoId, String cepOrigem,
                                         EnderecoExterno endereco) {
        log.info("Registrando coleta da nota fiscal {} pela transportadora {} em {}/{}",
                notaFiscalId, transportadoraId, endereco.cidade(), endereco.uf());

        Coleta coleta = Coleta.registrar(notaFiscalId, transportadoraId, motoristaId, caminhaoId,
                cepOrigem, endereco.cidade(), endereco.uf());

        Mono<Coleta> operacao = coletaRepository.save(coleta)
                .flatMap(salva -> {
                    // coletadaEm entra no payload porque o avaliacao-service precisa
                    // dela depois, quando a entrega for concluída, para calcular
                    // quanto tempo a transportadora levou (nota de agilidade).
                    String payload = """
                            {"coletaId":"%s","notaFiscalId":"%s","transportadoraId":"%s","motoristaId":"%s","caminhaoId":"%s","cidade":"%s","uf":"%s","coletadaEm":"%s"}"""
                            .formatted(salva.getId(), salva.getNotaFiscalId(), salva.getTransportadoraId(),
                                    salva.getMotoristaId(), salva.getCaminhaoId(), salva.getCidade(), salva.getUf(),
                                    salva.getColetadaEm());
                    OutboxEvent evento = OutboxEvent.de(salva.getId().value(), "COLETA_REGISTRADA", payload);
                    return outboxEventRepository.save(evento).thenReturn(salva);
                });

        return operacao.as(transactionalOperator::transactional);
    }
}
