package com.techchallenge.os.application.usecases;

import com.techchallenge.os.application.gateway.ports.output.ClienteGateway;
import com.techchallenge.os.application.gateway.ports.output.NotificacaoOrcamentoComPixGateway;
import com.techchallenge.os.application.gateway.ports.output.models.ClienteResumo;
import com.techchallenge.os.domain.events.CobrancaPixGeradaEvent;
import com.techchallenge.os.domain.exceptions.ValidacaoClienteException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificarClienteOrcamentoComPixUseCaseTest {

    @Mock
    private ClienteGateway clienteGateway;

    @Mock
    private NotificacaoOrcamentoComPixGateway notificacaoGateway;

    @Test
    void shouldNotifyCustomerWhenPixChargeIsGenerated() {
        UUID clienteId = UUID.randomUUID();
        var cliente = ClienteResumo.builder()
                .id(clienteId)
                .nome("Cliente")
                .email("cliente@example.com")
                .build();
        var event = new CobrancaPixGeradaEvent(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), clienteId,
                "pix-copy-paste", "base64", new BigDecimal("150.00"),
                LocalDateTime.now(), "os-reference", LocalDateTime.now());
        var useCase = new NotificarClienteOrcamentoComPixUseCase(clienteGateway, notificacaoGateway);
        when(clienteGateway.findById(clienteId)).thenReturn(Optional.of(cliente));

        useCase.execute(event);

        verify(notificacaoGateway).notificar(cliente, event);
    }

    @Test
    void shouldFailWhenCustomerDoesNotExist() {
        UUID clienteId = UUID.randomUUID();
        var event = new CobrancaPixGeradaEvent(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), clienteId,
                "pix-copy-paste", null, BigDecimal.ONE, LocalDateTime.now(), null, LocalDateTime.now());
        var useCase = new NotificarClienteOrcamentoComPixUseCase(clienteGateway, notificacaoGateway);
        when(clienteGateway.findById(clienteId)).thenReturn(Optional.empty());

        assertThrows(ValidacaoClienteException.class, () -> useCase.execute(event));
        verifyNoInteractions(notificacaoGateway);
    }
}
