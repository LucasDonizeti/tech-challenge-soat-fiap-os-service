package com.techchallenge.os.domain.events;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de VeiculoEntregueEvent - Domain Layer")
class VeiculoEntregueEventTest {

    @Test
    @DisplayName("Deve criar evento com dados válidos")
    void deveCriarEventoComDadosValidos() {
        // Arrange
        UUID ordemServicoId = UUID.randomUUID();
        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();

        // Act
        VeiculoEntregueEvent event = new VeiculoEntregueEvent(ordemServicoId, clienteId, veiculoId);

        // Assert
        assertNotNull(event);
        assertEquals(ordemServicoId, event.ordemServicoId());
        assertEquals(clienteId, event.clienteId());
        assertEquals(veiculoId, event.veiculoId());
    }

    @Test
    @DisplayName("Deve criar evento com IDs diferentes")
    void deveCriarEventoComIdsDiferentes() {
        // Arrange
        UUID ordemServicoId = UUID.randomUUID();
        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();

        // Act
        VeiculoEntregueEvent event = new VeiculoEntregueEvent(ordemServicoId, clienteId, veiculoId);

        // Assert
        assertNotNull(event);
        assertNotEquals(ordemServicoId, clienteId);
        assertNotEquals(ordemServicoId, veiculoId);
        assertNotEquals(clienteId, veiculoId);
    }

    @Test
    @DisplayName("Deve criar evento com UUIDs nulos")
    void deveCriarEventoComUUIDsNulos() {
        // Act
        VeiculoEntregueEvent event = new VeiculoEntregueEvent(null, null, null);

        // Assert
        assertNotNull(event);
        assertNull(event.ordemServicoId());
        assertNull(event.clienteId());
        assertNull(event.veiculoId());
    }

    @Test
    @DisplayName("Deve manter igualdade entre eventos com mesmos IDs")
    void deveManterIgualdadeEntreEventosComMesmosIds() {
        // Arrange
        UUID ordemServicoId = UUID.randomUUID();
        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();

        // Act
        VeiculoEntregueEvent event1 = new VeiculoEntregueEvent(ordemServicoId, clienteId, veiculoId);
        VeiculoEntregueEvent event2 = new VeiculoEntregueEvent(ordemServicoId, clienteId, veiculoId);

        // Assert
        assertEquals(event1, event2);
        assertEquals(event1.hashCode(), event2.hashCode());
    }

    @Test
    @DisplayName("Deve ter método toString funcional")
    void deveTerMetodoToStringFuncional() {
        // Arrange
        UUID ordemServicoId = UUID.randomUUID();
        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();
        VeiculoEntregueEvent event = new VeiculoEntregueEvent(ordemServicoId, clienteId, veiculoId);

        // Act
        String toString = event.toString();

        // Assert
        assertNotNull(toString);
        assertTrue(toString.contains(ordemServicoId.toString()));
        assertTrue(toString.contains(clienteId.toString()));
        assertTrue(toString.contains(veiculoId.toString()));
    }
}
