package com.techchallenge.os.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techchallenge.os.application.commands.RecusarOrcamentoCommand;
import com.techchallenge.os.application.gateway.ports.input.RecusarOrcamentoInput;
import com.techchallenge.os.application.usecases.NotificarClienteOrcamentoComPixUseCase;
import com.techchallenge.os.domain.events.SagaPaymentInitiatedEvent;
import com.techchallenge.os.domain.events.SagaPaymentExpiredEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@DisplayName("Testes - RabbitSagaEventListener")
@ExtendWith(MockitoExtension.class)
class RabbitSagaEventListenerTest {

    @Mock
    private RecusarOrcamentoInput recusarOrcamentoInput;

    @Mock
    private NotificarClienteOrcamentoComPixUseCase notificarClienteOrcamentoComPixUseCase;

    private RabbitSagaEventListener listener;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper().findAndRegisterModules();
        listener = new RabbitSagaEventListener(
                objectMapper,
                recusarOrcamentoInput,
                notificarClienteOrcamentoComPixUseCase
        );
    }

    @Test
    @DisplayName("Deve notificar cliente quando PIX é gerado")
    void deveNotificarClienteQuandoPixGerado() throws Exception {
        // Arrange
        UUID sagaId = UUID.randomUUID();
        UUID ordemServicoId = UUID.randomUUID();
        UUID pagamentoId = UUID.randomUUID();
        UUID orcamentoId = UUID.randomUUID();
        UUID clienteId = UUID.randomUUID();
        
        SagaPaymentInitiatedEvent event = new SagaPaymentInitiatedEvent(
                sagaId,
                pagamentoId,
                orcamentoId,
                ordemServicoId,
                clienteId,
                "123456789",
                "00020126580014com.mercadolibre...",
                "iVBORw0KGgoAAAA...",
                BigDecimal.valueOf(150.50),
                LocalDateTime.now().plusMinutes(30),
                LocalDateTime.now()
        );
        
        String json = objectMapper.writeValueAsString(event);

        // Act
        listener.receiveSagaPaymentInitiated(json);

        // Assert
        verify(notificarClienteOrcamentoComPixUseCase).execute(any());
    }

    @Test
    @DisplayName("Deve recusar orçamento quando PIX expira")
    void deveRecusarOrcamentoQuandoPixExpira() throws Exception {
        // Arrange
        UUID sagaId = UUID.randomUUID();
        UUID ordemServicoId = UUID.randomUUID();
        LocalDateTime dataExpiracao = LocalDateTime.now().minusMinutes(5);
        
        SagaPaymentExpiredEvent event = new SagaPaymentExpiredEvent(
                sagaId,
                UUID.randomUUID(),
                ordemServicoId,
                "123456789",
                dataExpiracao,
                LocalDateTime.now()
        );
        
        String json = objectMapper.writeValueAsString(event);

        // Act
        listener.receiveSagaPaymentExpired(json);

        // Assert
        ArgumentCaptor<RecusarOrcamentoCommand> captor = 
                ArgumentCaptor.forClass(RecusarOrcamentoCommand.class);
        verify(recusarOrcamentoInput).execute(captor.capture());
        
        assertEquals(ordemServicoId, captor.getValue().getOrdemServicoId());
        assertEquals("PIX expirou em " + dataExpiracao, captor.getValue().getMotivo());
    }

    @Test
    @DisplayName("Deve lançar exceção se JSON for inválido para SagaPaymentInitiated")
    void deveLancarExcecaoQuandoJsonInvalidoParaSagaInitiated() {
        // Arrange
        String invalidJson = "invalid json";

        // Act & Assert
        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> {
            listener.receiveSagaPaymentInitiated(invalidJson);
        });
    }

    @Test
    @DisplayName("Deve lançar exceção se JSON for inválido para SagaPaymentExpired")
    void deveLancarExcecaoQuandoJsonInvalidoParaSagaExpired() {
        // Arrange
        String invalidJson = "invalid json";

        // Act & Assert
        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> {
            listener.receiveSagaPaymentExpired(invalidJson);
        });
    }
}
