package com.logistica.config.domain;

import com.logistica.domain.*;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class ColetaTest {
    @Test
    void deveRegistrarColetaComTodosOsIds() {
        NotaFiscalId notaFiscalId = new NotaFiscalId(UUID.randomUUID());
        TransportadoraId transportadoraId = TransportadoraId.novo();
        MotoristaId motoristaId = MotoristaId.novo();
        CaminhaoId caminhaoId = CaminhaoId.novo();

        Coleta coleta = Coleta.registrar(notaFiscalId, transportadoraId, motoristaId, caminhaoId,
                "01310100", "Sao Paulo", "SP");

        assertThat(coleta.getId()).isNotNull();
        assertThat(coleta.getNotaFiscalId()).isEqualTo(notaFiscalId);
        assertThat(coleta.getTransportadoraId()).isEqualTo(transportadoraId);
        assertThat(coleta.getMotoristaId()).isEqualTo(motoristaId);
        assertThat(coleta.getCaminhaoId()).isEqualTo(caminhaoId);
        assertThat(coleta.getColetadaEm()).isNotNull();
    }

    @Test
    void doisTransportadoraIdComMesmoValorDevemSerIguais() {
        UUID valor = UUID.randomUUID();

        assertThat(new TransportadoraId(valor)).isEqualTo(new TransportadoraId(valor));
    }

}
