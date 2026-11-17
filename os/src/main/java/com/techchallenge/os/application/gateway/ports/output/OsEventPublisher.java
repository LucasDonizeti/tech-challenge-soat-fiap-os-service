package com.techchallenge.os.application.gateway.ports.output;

import com.techchallenge.os.domain.events.OrcamentoProntoEvent;

public interface OsEventPublisher {
    void publishOrcamentoPronto(OrcamentoProntoEvent event);
}
