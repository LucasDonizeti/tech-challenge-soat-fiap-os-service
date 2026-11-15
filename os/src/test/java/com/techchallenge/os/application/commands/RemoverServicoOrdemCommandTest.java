package com.techchallenge.os.application.commands;

import com.techchallenge.os.domain.exceptions.ValidacaoOrdemServicoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de RemoverServicoOrdemCommand - Application Layer")
class RemoverServicoOrdemCommandTest {

    @Test
    @DisplayName("Deve criar command com IDs válidos")
    void deveCriarCommandComIDsValidos() {
        // Arrange
        UUID ordemServicoId = UUID.randomUUID();
        UUID itemServicoId = UUID.randomUUID();

        // Act
        RemoverServicoOrdemCommand command = new RemoverServicoOrdemCommand(ordemServicoId, itemServicoId);

        // Assert
        assertNotNull(command);
        assertEquals(ordemServicoId, command.getOrdemServicoId());
        assertEquals(itemServicoId, command.getItemServicoId());
    }

    @Test
    @DisplayName("Deve lançar exceção quando ordemServicoId é null")
    void deveLancarExcecaoQuandoOrdemServicoIdENull() {
        // Arrange
        UUID itemServicoId = UUID.randomUUID();

        // Act & Assert
        ValidacaoOrdemServicoException exception = assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> new RemoverServicoOrdemCommand(null, itemServicoId)
        );

        assertEquals("Ordem de Serviço ID é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando itemServicoId é null")
    void deveLancarExcecaoQuandoItemServicoIdENull() {
        // Arrange
        UUID ordemServicoId = UUID.randomUUID();

        // Act & Assert
        ValidacaoOrdemServicoException exception = assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> new RemoverServicoOrdemCommand(ordemServicoId, null)
        );

        assertEquals("Item de Serviço ID é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando ambos os IDs são null")
    void deveLancarExcecaoQuandoAmbosOsIdsSaoNull() {
        // Act & Assert
        ValidacaoOrdemServicoException exception = assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> new RemoverServicoOrdemCommand(null, null)
        );

        assertEquals("Ordem de Serviço ID é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve retornar getters corretamente")
    void deveRetornarGettersCorretamente() {
        // Arrange
        UUID ordemServicoId = UUID.randomUUID();
        UUID itemServicoId = UUID.randomUUID();

        // Act
        RemoverServicoOrdemCommand command = new RemoverServicoOrdemCommand(ordemServicoId, itemServicoId);

        // Assert
        assertEquals(ordemServicoId, command.getOrdemServicoId());
        assertEquals(itemServicoId, command.getItemServicoId());
    }
}
