package com.techchallenge.os.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techchallenge.os.application.commands.AprovarOrcamentoCommand;
import com.techchallenge.os.application.commands.RecusarOrcamentoCommand;
import com.techchallenge.os.application.gateway.ports.input.AprovarOrcamentoInput;
import com.techchallenge.os.application.gateway.ports.input.RecusarOrcamentoInput;
import com.techchallenge.os.application.usecases.NotificarClienteOrcamentoComPixUseCase;
import com.techchallenge.os.domain.events.CobrancaPixGeradaEvent;
import com.techchallenge.os.domain.events.PagamentoAprovadoEvent;
import com.techchallenge.os.domain.events.PagamentoRecusadoEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SqsBillingEventListenerTest {

    @Mock
    private NotificarClienteOrcamentoComPixUseCase notificarClienteOrcamentoComPixUseCase;

    @Mock
    private AprovarOrcamentoInput aprovarOrcamentoInput;

    @Mock
    private RecusarOrcamentoInput recusarOrcamentoInput;

    private ObjectMapper objectMapper;
    private SqsBillingEventListener listener;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper().findAndRegisterModules();
        listener = new SqsBillingEventListener(
                objectMapper,
                notificarClienteOrcamentoComPixUseCase,
                aprovarOrcamentoInput,
                recusarOrcamentoInput);
    }

    @Test
    void shouldProcessPixChargeEventFromSnsEnvelope() throws Exception {
        var event = new CobrancaPixGeradaEvent(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                "pix-copy-paste", "base64", new BigDecimal("150.00"),
                LocalDateTime.now(), "os-reference", LocalDateTime.now());
        String message = objectMapper.writeValueAsString(event);
        String envelope = objectMapper.writeValueAsString(java.util.Map.of("Message", message));

        listener.receiveCobrancaPixGerada(envelope);

        ArgumentCaptor<CobrancaPixGeradaEvent> eventCaptor =
                ArgumentCaptor.forClass(CobrancaPixGeradaEvent.class);
        verify(notificarClienteOrcamentoComPixUseCase).execute(eventCaptor.capture());
        assertEquals(event.pagamentoId(), eventCaptor.getValue().pagamentoId());
        assertEquals(event.ordemServicoId(), eventCaptor.getValue().ordemServicoId());
        assertEquals(0, event.valorCobrado().compareTo(eventCaptor.getValue().valorCobrado()));
    }

    @Test
    void shouldApproveOrderForApprovedPayment() throws Exception {
        var orderId = UUID.randomUUID();
        var event = new PagamentoAprovadoEvent(
                UUID.randomUUID(), UUID.randomUUID(), orderId, UUID.randomUUID(),
                "mp-payment-id", new BigDecimal("150.00"), LocalDateTime.now(), LocalDateTime.now());

        listener.receivePagamentoAprovado(objectMapper.writeValueAsString(event));

        ArgumentCaptor<AprovarOrcamentoCommand> commandCaptor =
                ArgumentCaptor.forClass(AprovarOrcamentoCommand.class);
        verify(aprovarOrcamentoInput).execute(commandCaptor.capture());
        assertEquals(orderId, commandCaptor.getValue().getOrdemServicoId());
    }

    @Test
    void shouldRejectOrderForRejectedPayment() throws Exception {
        var orderId = UUID.randomUUID();
        var event = new PagamentoRecusadoEvent(
                UUID.randomUUID(), UUID.randomUUID(), orderId, UUID.randomUUID(),
                "Pagamento expirado", "expired", LocalDateTime.now(), LocalDateTime.now());

        listener.receivePagamentoRecusado(objectMapper.writeValueAsString(event));

        ArgumentCaptor<RecusarOrcamentoCommand> commandCaptor =
                ArgumentCaptor.forClass(RecusarOrcamentoCommand.class);
        verify(recusarOrcamentoInput).execute(commandCaptor.capture());
        assertEquals(orderId, commandCaptor.getValue().getOrdemServicoId());
        assertEquals("Pagamento expirado", commandCaptor.getValue().getMotivo());
    }
}
