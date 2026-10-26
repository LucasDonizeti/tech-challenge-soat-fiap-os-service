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
class RabbitBillingEventListenerTest {

    @Mock
    private NotificarClienteOrcamentoComPixUseCase notificarCliente;

    @Mock
    private AprovarOrcamentoInput aprovarOrcamentoInput;

    @Mock
    private RecusarOrcamentoInput recusarOrcamentoInput;

    @Test
    void dispatchesIncomingRabbitEventsToExistingUseCases() throws Exception {
        var mapper = new ObjectMapper().findAndRegisterModules();
        var listener = new RabbitBillingEventListener(
                mapper, notificarCliente, aprovarOrcamentoInput, recusarOrcamentoInput);
        var osId = UUID.randomUUID();
        var pixEvent = new CobrancaPixGeradaEvent(
                UUID.randomUUID(), UUID.randomUUID(), osId, UUID.randomUUID(),
                "pix-code", "base64", new BigDecimal("10.00"),
                LocalDateTime.now(), osId.toString(), LocalDateTime.now());
        var approvedEvent = new PagamentoAprovadoEvent(
                UUID.randomUUID(), UUID.randomUUID(), osId, UUID.randomUUID(),
                "mp-123", new BigDecimal("10.00"), LocalDateTime.now(), LocalDateTime.now());
        var rejectedEvent = new PagamentoRecusadoEvent(
                UUID.randomUUID(), UUID.randomUUID(), osId, UUID.randomUUID(),
                "Recusado", "rejected", LocalDateTime.now(), LocalDateTime.now());

        listener.receiveCobrancaPixGerada(mapper.writeValueAsString(pixEvent));
        listener.receivePagamentoAprovado(mapper.writeValueAsString(approvedEvent));
        listener.receivePagamentoRecusado(mapper.writeValueAsString(rejectedEvent));

        verify(notificarCliente).execute(org.mockito.ArgumentMatchers.argThat(
                received -> received.ordemServicoId().equals(osId)));
        var approveCommand = ArgumentCaptor.forClass(AprovarOrcamentoCommand.class);
        verify(aprovarOrcamentoInput).execute(approveCommand.capture());
        assertEquals(osId, approveCommand.getValue().getOrdemServicoId());
        var rejectCommand = ArgumentCaptor.forClass(RecusarOrcamentoCommand.class);
        verify(recusarOrcamentoInput).execute(rejectCommand.capture());
        assertEquals(osId, rejectCommand.getValue().getOrdemServicoId());
        assertEquals("Recusado", rejectCommand.getValue().getMotivo());
    }
}
