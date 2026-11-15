package com.techchallenge.os.application.usecases;

import com.techchallenge.os.application.gateway.ports.output.ClienteGateway;
import com.techchallenge.os.application.gateway.ports.output.NotificacaoOrcamentoComPixGateway;
import com.techchallenge.os.domain.events.CobrancaPixGeradaEvent;
import com.techchallenge.os.domain.exceptions.ValidacaoClienteException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class NotificarClienteOrcamentoComPixUseCase {

    private final ClienteGateway clienteGateway;
    private final NotificacaoOrcamentoComPixGateway notificacaoGateway;

    public void execute(CobrancaPixGeradaEvent event) {
        if (event == null || event.clienteId() == null || event.ordemServicoId() == null) {
            throw new ValidacaoClienteException(
                    "O evento de cobrança PIX precisa conter os identificadores do cliente e da ordem de serviço.");
        }
        if (event.qrCodePix() == null || event.qrCodePix().isBlank()) {
            throw new IllegalArgumentException("O evento de cobrança PIX não contém o código copia e cola.");
        }

        var cliente = clienteGateway.findById(event.clienteId())
                .orElseThrow(() -> new ValidacaoClienteException(
                        "Cliente não encontrado com ID: " + event.clienteId()));
        if (cliente.getEmail() == null || cliente.getEmail().isBlank()) {
            throw new ValidacaoClienteException(
                    "O cliente precisa ter um e-mail válido para receber a cobrança PIX.");
        }

        notificacaoGateway.notificar(cliente, event);
        log.info("Notificação de cobrança PIX enviada: ordemServicoId={}, clienteId={}",
                event.ordemServicoId(), event.clienteId());
    }
}
