package com.logistica.fiscal.web.assembler;

import com.logistica.fiscal.domain.NotaFiscal;
import com.logistica.fiscal.domain.StatusNotaFiscal;
import com.logistica.fiscal.web.NotaFiscalController;
import com.logistica.fiscal.web.dto.NotaFiscalResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


/**
 * Monta a resposta HATEOAS da nota fiscal, expondo apenas os links das
 * transições de status que são válidas a partir do estado atual — o cliente
 * da API não precisa conhecer a máquina de estados de antemão.
 */
@Component
public class NotaFiscalModelAssembler {

    public EntityModel<NotaFiscalResponse> toModel(NotaFiscal notaFiscal) {
        NotaFiscalResponse response = NotaFiscalResponse.de(notaFiscal);
        String id = notaFiscal.getId().toString();

        List<Link> links = new ArrayList<>();
        links.add(linkTo(methodOn(NotaFiscalController.class).buscarPorId(id)).withSelfRel());

        switch (notaFiscal.getStatus()) {
            case PENDENTE -> {
                links.add(linkTo(methodOn(NotaFiscalController.class)
                        .alterarStatus(id, new com.logistica.fiscal.web.dto.AlterarStatusRequest(StatusNotaFiscal.CONFERIDA)))
                        .withRel("conferir"));
                links.add(linkTo(methodOn(NotaFiscalController.class)
                        .alterarStatus(id, new com.logistica.fiscal.web.dto.AlterarStatusRequest(StatusNotaFiscal.NAO_CONFERIDA)))
                        .withRel("marcar-nao-conferida"));
                links.add(linkTo(methodOn(NotaFiscalController.class)
                        .alterarStatus(id, new com.logistica.fiscal.web.dto.AlterarStatusRequest(StatusNotaFiscal.CANCELADA)))
                        .withRel("cancelar"));
            }
            case CONFERIDA, NAO_CONFERIDA -> {
                links.add(linkTo(methodOn(NotaFiscalController.class)
                        .alterarStatus(id, new com.logistica.fiscal.web.dto.AlterarStatusRequest(StatusNotaFiscal.DEVOLUCAO)))
                        .withRel("registrar-devolucao"));
                // Cancelar ainda é permitido aqui — mas repare que isso NÃO
                // desfaz sozinho uma coleta que já tenha sido ENTREGUE do
                // outro lado (logistica-service); a compensação tem um
                // limite real, não é mágica (ver NotaFiscalEventConsumer).
                links.add(linkTo(methodOn(NotaFiscalController.class)
                        .alterarStatus(id, new com.logistica.fiscal.web.dto.AlterarStatusRequest(StatusNotaFiscal.CANCELADA)))
                        .withRel("cancelar"));
            }
            case DEVOLUCAO, CANCELADA -> {
                // estados finais, nenhuma transição adicional disponível
            }
        }

        return EntityModel.of(response, links);
    }
}
