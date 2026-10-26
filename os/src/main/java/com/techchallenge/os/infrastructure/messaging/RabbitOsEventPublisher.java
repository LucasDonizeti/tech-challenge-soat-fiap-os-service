package com.techchallenge.os.infrastructure.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techchallenge.os.application.gateway.ports.output.OsEventPublisher;
import com.techchallenge.os.domain.events.OrcamentoProntoEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "messaging.type", havingValue = "rabbit")
public class RabbitOsEventPublisher implements OsEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @Value("${messaging.rabbit.exchange}")
    private String exchange;

    @Value("${messaging.rabbit.routing-keys.orcamento-pronto}")
    private String routingKey;

    @Override
    public void publishOrcamentoPronto(OrcamentoProntoEvent event) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    publish(event);
                }
            });
            return;
        }
        publish(event);
    }

    private void publish(OrcamentoProntoEvent event) {
        try {
            rabbitTemplate.convertAndSend(exchange, routingKey, objectMapper.writeValueAsString(event));
            log.info("OrcamentoProntoEvent publicado no RabbitMQ: ordemServicoId={}", event.ordemServicoId());
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Não foi possível serializar OrcamentoProntoEvent.", exception);
        }
    }
}
