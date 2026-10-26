package com.techchallenge.os.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techchallenge.os.domain.events.OrcamentoProntoEvent;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class RabbitOsEventPublisherTest {

    @Test
    void publishesQuoteReadyEventToBillingQueueRoute() {
        var rabbitTemplate = mock(RabbitTemplate.class);
        var publisher = new RabbitOsEventPublisher(rabbitTemplate, new ObjectMapper().findAndRegisterModules());
        ReflectionTestUtils.setField(publisher, "exchange", "billing.events");
        ReflectionTestUtils.setField(publisher, "routingKey", "orcamento.pronto");
        var event = new OrcamentoProntoEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                new BigDecimal("42.00"),
                "Serviço",
                LocalDateTime.now(),
                "cliente@example.com",
                "CPF",
                "12345678901"
        );

        publisher.publishOrcamentoPronto(event);

        try {
            verify(rabbitTemplate).convertAndSend(
                    eq("billing.events"),
                    eq("orcamento.pronto"),
                    eq(new ObjectMapper().findAndRegisterModules().writeValueAsString(event))
            );
        } catch (Exception exception) {
            throw new AssertionError(exception);
        }
    }
}
