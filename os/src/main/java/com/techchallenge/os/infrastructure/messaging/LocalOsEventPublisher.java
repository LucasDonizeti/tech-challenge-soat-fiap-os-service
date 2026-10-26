package com.techchallenge.os.infrastructure.messaging;

import com.techchallenge.os.application.gateway.ports.output.OsEventPublisher;
import com.techchallenge.os.domain.events.OrcamentoProntoEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LocalOsEventPublisher implements OsEventPublisher {

    @Override
    public void publishOrcamentoPronto(OrcamentoProntoEvent event) {
        log.info("Evento OrcamentoProntoEvent registrado localmente: ordemServicoId={}, clienteId={}, valorTotal={}",
                event.ordemServicoId(), event.clienteId(), event.valorTotal());
    }
}
