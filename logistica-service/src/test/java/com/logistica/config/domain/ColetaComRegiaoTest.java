package com.logistica.config.domain;

import com.logistica.domain.*;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class ColetaComRegiaoTest {
    @Test
    void deveRegistrarColetaComCidadeEUfResolvidos() {
        Coleta coleta = Coleta.registrar(
                new NotaFiscalId(UUID.randomUUID()),
                TransportadoraId.novo(),
                MotoristaId.novo(),
                CaminhaoId.novo(),
                "01310100",
                "Sao Paulo",
                "SP");

        assertThat(coleta.getCepOrigem()).isEqualTo("01310100");
        assertThat(coleta.getCidade()).isEqualTo("Sao Paulo");
        assertThat(coleta.getUf()).isEqualTo("SP");
    }
}
