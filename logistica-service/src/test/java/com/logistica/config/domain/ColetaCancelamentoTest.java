package com.logistica.config.domain;

import com.logistica.domain.*;
import com.logistica.exception.ColetaNaoPodeSerCanceladaException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ColetaCancelamentoTest {

    private Coleta novaColeta() {
        return Coleta.registrar(
                new NotaFiscalId(UUID.randomUUID()),
                TransportadoraId.novo(),
                MotoristaId.novo(),
                CaminhaoId.novo(),
                "01310100", "Sao Paulo", "SP");
    }

    @Test
    void devePermitirCancelarUmaColetaAindaNaoEntregue() {
        Coleta coleta = novaColeta();

        coleta.cancelar();

        assertThat(coleta.getStatus()).isEqualTo(StatusColeta.CANCELADA);
    }

    @Test
    void naoDevePermitirCancelarUmaColetaJaEntregue() {
        Coleta coleta = novaColeta();
        coleta.marcarComoEntregue();

        assertThatThrownBy(coleta::cancelar)
                .isInstanceOf(ColetaNaoPodeSerCanceladaException.class);
    }

    @Test
    void cancelarUmaColetaJaCanceladaENoOpIdempotente() {
        Coleta coleta = novaColeta();
        coleta.cancelar();

        coleta.cancelar(); // não deve lançar exceção — idempotente

        assertThat(coleta.getStatus()).isEqualTo(StatusColeta.CANCELADA);
    }
}
