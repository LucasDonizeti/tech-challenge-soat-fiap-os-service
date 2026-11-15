package com.techchallenge.os.application.gateway.ports.output;

import com.techchallenge.os.application.gateway.ports.output.models.ClienteResumo;
import com.techchallenge.os.domain.events.CobrancaPixGeradaEvent;

public interface NotificacaoOrcamentoComPixGateway {
    void notificar(ClienteResumo cliente, CobrancaPixGeradaEvent event);
}
