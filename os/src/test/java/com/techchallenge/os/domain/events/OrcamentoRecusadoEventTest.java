package com.techchallenge.os.domain.events;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de OrcamentoRecusadoEvent - Domain Layer")
class OrcamentoRecusadoEventTest {

    @Test
    @DisplayName("Deve criar evento com todos os campos")
    void deveCriarEventoComTodosOsCampos() {
        // Arrange
        UUID ordemServicoId = UUID.randomUUID();
        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();
        String motivoRecusa = "Preço muito alto";

        // Act
        OrcamentoRecusadoEvent event = new OrcamentoRecusadoEvent(
                ordemServicoId,
                clienteId,
                veiculoId,
                motivoRecusa
        );

        // Assert
        assertNotNull(event);
        assertEquals(ordemServicoId, event.ordemServicoId());
        assertEquals(clienteId, event.clienteId());
        assertEquals(veiculoId, event.veiculoId());
        assertEquals(motivoRecusa, event.motivoRecusa());
    }

    @Test
    @DisplayName("Deve criar evento com motivo de recusa vazio")
    void deveCriarEventoComMotivoDeRecusaVazio() {
        // Arrange
        UUID ordemServicoId = UUID.randomUUID();
        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();
        String motivoRecusa = "";

        // Act
        OrcamentoRecusadoEvent event = new OrcamentoRecusadoEvent(
                ordemServicoId,
                clienteId,
                veiculoId,
                motivoRecusa
        );

        // Assert
        assertNotNull(event);
        assertEquals(ordemServicoId, event.ordemServicoId());
        assertEquals(clienteId, event.clienteId());
        assertEquals(veiculoId, event.veiculoId());
        assertEquals(motivoRecusa, event.motivoRecusa());
    }

    @Test
    @DisplayName("Deve criar evento com motivo de recusa null")
    void deveCriarEventoComMotivoDeRecusaNull() {
        // Arrange
        UUID ordemServicoId = UUID.randomUUID();
        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();

        // Act
        OrcamentoRecusadoEvent event = new OrcamentoRecusadoEvent(
                ordemServicoId,
                clienteId,
                veiculoId,
                null
        );

        // Assert
        assertNotNull(event);
        assertEquals(ordemServicoId, event.ordemServicoId());
        assertEquals(clienteId, event.clienteId());
        assertEquals(veiculoId, event.veiculoId());
        assertNull(event.motivoRecusa());
    }

    @Test
    @DisplayName("Deve acessar campos via métodos de record")
    void deveAcessarCamposViaMetodosDeRecord() {
        // Arrange
        UUID ordemServicoId = UUID.randomUUID();
        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();
        String motivoRecusa = "Não gostei do serviço";

        // Act
        OrcamentoRecusadoEvent event = new OrcamentoRecusadoEvent(
                ordemServicoId,
                clienteId,
                veiculoId,
                motivoRecusa
        );

        // Assert
        assertEquals(ordemServicoId, event.ordemServicoId());
        assertEquals(clienteId, event.clienteId());
        assertEquals(veiculoId, event.veiculoId());
        assertEquals(motivoRecusa, event.motivoRecusa());
    }

    @Test
    @DisplayName("Deve ter equals e hashCode funcionando corretamente")
    void deveTerEqualsEHashCodeFuncionandoCorretamente() {
        // Arrange
        UUID ordemServicoId = UUID.randomUUID();
        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();
        String motivoRecusa = "Teste";

        OrcamentoRecusadoEvent event1 = new OrcamentoRecusadoEvent(
                ordemServicoId,
                clienteId,
                veiculoId,
                motivoRecusa
        );

        OrcamentoRecusadoEvent event2 = new OrcamentoRecusadoEvent(
                ordemServicoId,
                clienteId,
                veiculoId,
                motivoRecusa
        );

        // Assert
        assertEquals(event1, event2);
        assertEquals(event1.hashCode(), event2.hashCode());
    }

    @Test
    @DisplayName("Deve ter toString funcionando")
    void deveTerToStringFuncionando() {
        // Arrange
        UUID ordemServicoId = UUID.randomUUID();
        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();
        String motivoRecusa = "Teste";

        OrcamentoRecusadoEvent event = new OrcamentoRecusadoEvent(
                ordemServicoId,
                clienteId,
                veiculoId,
                motivoRecusa
        );

        // Act
        String toString = event.toString();

        // Assert
        assertNotNull(toString);
        assertTrue(toString.contains("OrcamentoRecusadoEvent"));
        assertTrue(toString.contains(ordemServicoId.toString()));
    }
}
