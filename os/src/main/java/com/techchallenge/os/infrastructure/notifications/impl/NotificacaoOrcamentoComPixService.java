package com.techchallenge.os.infrastructure.notifications.impl;

import com.techchallenge.os.application.gateway.ports.output.NotificacaoOrcamentoComPixGateway;
import com.techchallenge.os.application.gateway.ports.output.models.ClienteResumo;
import com.techchallenge.os.domain.events.CobrancaPixGeradaEvent;
import com.techchallenge.os.infrastructure.notifications.NotificacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class NotificacaoOrcamentoComPixService implements NotificacaoOrcamentoComPixGateway {

    private static final DateTimeFormatter EXPIRATION_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final NotificacaoService notificacaoService;

    @Override
    public void notificar(ClienteResumo cliente, CobrancaPixGeradaEvent event) {
        String expiration = event.dataExpiracao() == null
                ? "não informada"
                : event.dataExpiracao().format(EXPIRATION_FORMAT);
        String amount = event.valorCobrado() == null
                ? "não informado"
                : event.valorCobrado().toPlainString();
        StringBuilder message = new StringBuilder()
                .append("O orçamento da sua ordem de serviço ")
                .append(event.ordemServicoId())
                .append(" está disponível para pagamento via PIX.\n\n")
                .append("Valor: R$ ").append(amount).append('\n')
                .append("Vencimento: ").append(expiration).append('\n')
                .append("Código PIX copia e cola:\n").append(event.qrCodePix());
        if (event.qrCodeBase64() != null && !event.qrCodeBase64().isBlank()) {
            message.append("\n\nQR Code (Base64):\n").append(event.qrCodeBase64());
        }

        notificacaoService.notificarCliente(
                cliente.getEmail(),
                cliente.getNome(),
                "Orçamento da ordem de serviço - pagamento PIX",
                message.toString());
    }
}
