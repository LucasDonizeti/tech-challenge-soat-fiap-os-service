package com.techchallenge.os.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techchallenge.os.application.commands.RecusarOrcamentoCommand;
import com.techchallenge.os.application.gateway.ports.input.RecusarOrcamentoInput;
import com.techchallenge.os.application.usecases.NotificarClienteOrcamentoComPixUseCase;
import com.techchallenge.os.domain.events.SagaPaymentExpiredEvent;
import com.techchallenge.os.domain.events.SagaPaymentInitiatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SqsSagaEventListenerTest {

    @Mock
    private NotificarClienteOrcamentoComPixUseCase notificarClienteOrcamentoComPixUseCase;

    @Mock
    private RecusarOrcamentoInput recusarOrcamentoInput;

    private ObjectMapper objectMapper;
    private SqsSagaEventListener listener;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper().findAndRegisterModules();
        listener = new SqsSagaEventListener(
                objectMapper, notificarClienteOrcamentoComPixUseCase, recusarOrcamentoInput);
    }

    @Test
    void shouldProcessSagaInitiatedEventFromSnsEnvelope() throws Exception {
        UUID orderId = UUID.randomUUID();
        var event = new SagaPaymentInitiatedEvent(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), orderId, UUID.randomUUID(),
                "mp-payment-id", "pix", "base64", new BigDecimal("150.00"),
                LocalDateTime.now(), LocalDateTime.now());
        String message = objectMapper.writeValueAsString(event);
        String envelope = objectMapper.writeValueAsString(Map.of("Message", message));

        listener.receiveSagaPaymentInitiated(envelope);

        var eventCaptor = ArgumentCaptor.forClass(
                com.techchallenge.os.domain.events.CobrancaPixGeradaEvent.class);
        verify(notificarClienteOrcamentoComPixUseCase).execute(eventCaptor.capture());
        assertEquals(orderId, eventCaptor.getValue().ordemServicoId());
        assertEquals(event.getPagamentoId(), eventCaptor.getValue().pagamentoId());
    }

    @Test
    void shouldRejectOrderWhenSagaPaymentExpires() throws Exception {
        UUID orderId = UUID.randomUUID();
        LocalDateTime expiry = LocalDateTime.now().minusMinutes(1);
        var event = new SagaPaymentExpiredEvent(
                UUID.randomUUID(), UUID.randomUUID(), orderId, "mp-payment-id", expiry, LocalDateTime.now());

        listener.receiveSagaPaymentExpired(objectMapper.writeValueAsString(event));

        ArgumentCaptor<RecusarOrcamentoCommand> commandCaptor =
                ArgumentCaptor.forClass(RecusarOrcamentoCommand.class);
        verify(recusarOrcamentoInput).execute(commandCaptor.capture());
        assertEquals(orderId, commandCaptor.getValue().getOrdemServicoId());
        assertEquals("PIX expirou em " + expiry, commandCaptor.getValue().getMotivo());
    }
}
