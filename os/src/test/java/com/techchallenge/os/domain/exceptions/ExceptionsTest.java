package com.techchallenge.os.domain.exceptions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de Exceções - Domain Layer")
class ExceptionsTest {

    @Test
    @DisplayName("Deve criar NotificacaoException com mensagem")
    void deveCriarNotificacaoExceptionComMensagem() {
        // Act
        NotificacaoException exception = new NotificacaoException("Erro de notificação");

        // Assert
        assertEquals("Erro de notificação", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("Deve criar NotificacaoException com mensagem e causa")
    void deveCriarNotificacaoExceptionComMensagemECausa() {
        // Arrange
        Throwable cause = new RuntimeException("Causa raiz");

        // Act
        NotificacaoException exception = new NotificacaoException("Erro de notificação", cause);

        // Assert
        assertEquals("Erro de notificação", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    @DisplayName("Deve criar ValidacaoVeiculoException com mensagem")
    void deveCriarValidacaoVeiculoExceptionComMensagem() {
        // Act
        ValidacaoVeiculoException exception = new ValidacaoVeiculoException("Veículo inválido");

        // Assert
        assertEquals("Veículo inválido", exception.getMessage());
        assertEquals("VALIDACAO_VEICULO", exception.getErrorCode());
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("Deve criar ValidacaoVeiculoException com mensagem e causa")
    void deveCriarValidacaoVeiculoExceptionComMensagemECausa() {
        // Arrange
        Throwable cause = new RuntimeException("Causa raiz");

        // Act
        ValidacaoVeiculoException exception = new ValidacaoVeiculoException("Veículo inválido", cause);

        // Assert
        assertEquals("Veículo inválido", exception.getMessage());
        assertEquals("VALIDACAO_VEICULO", exception.getErrorCode());
        assertEquals(cause, exception.getCause());
    }

    @Test
    @DisplayName("Deve criar ValidacaoServicoException com mensagem")
    void deveCriarValidacaoServicoExceptionComMensagem() {
        // Act
        ValidacaoServicoException exception = new ValidacaoServicoException("Serviço inválido");

        // Assert
        assertEquals("Serviço inválido", exception.getMessage());
        assertEquals("VALIDACAO_SERVICO", exception.getErrorCode());
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("Deve criar ValidacaoServicoException com mensagem e causa")
    void deveCriarValidacaoServicoExceptionComMensagemECausa() {
        // Arrange
        Throwable cause = new RuntimeException("Causa raiz");

        // Act
        ValidacaoServicoException exception = new ValidacaoServicoException("Serviço inválido", cause);

        // Assert
        assertEquals("Serviço inválido", exception.getMessage());
        assertEquals("VALIDACAO_SERVICO", exception.getErrorCode());
        assertEquals(cause, exception.getCause());
    }

    @Test
    @DisplayName("Deve criar ValidacaoOrdemServicoException com mensagem")
    void deveCriarValidacaoOrdemServicoExceptionComMensagem() {
        // Act
        ValidacaoOrdemServicoException exception = new ValidacaoOrdemServicoException("OS inválida");

        // Assert
        assertEquals("OS inválida", exception.getMessage());
        assertEquals("VALIDACAO_ORDEM_SERVICO", exception.getErrorCode());
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("Deve criar ValidacaoOrdemServicoException com mensagem e causa")
    void deveCriarValidacaoOrdemServicoExceptionComMensagemECausa() {
        // Arrange
        Throwable cause = new RuntimeException("Causa raiz");

        // Act
        ValidacaoOrdemServicoException exception = new ValidacaoOrdemServicoException("OS inválida", cause);

        // Assert
        assertEquals("OS inválida", exception.getMessage());
        assertEquals("VALIDACAO_ORDEM_SERVICO", exception.getErrorCode());
        assertEquals(cause, exception.getCause());
    }

    @Test
    @DisplayName("Deve criar ValidacaoMROException com mensagem")
    void deveCriarValidacaoMROExceptionComMensagem() {
        // Act
        ValidacaoMROException exception = new ValidacaoMROException("MRO inválido");

        // Assert
        assertEquals("MRO inválido", exception.getMessage());
        assertEquals("VALIDACAO_MRO", exception.getErrorCode());
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("Deve criar ValidacaoMROException com mensagem e causa")
    void deveCriarValidacaoMROExceptionComMensagemECausa() {
        // Arrange
        Throwable cause = new RuntimeException("Causa raiz");

        // Act
        ValidacaoMROException exception = new ValidacaoMROException("MRO inválido", cause);

        // Assert
        assertEquals("MRO inválido", exception.getMessage());
        assertEquals("VALIDACAO_MRO", exception.getErrorCode());
        assertEquals(cause, exception.getCause());
    }

    @Test
    @DisplayName("Deve criar ValidacaoEstoqueException com mensagem")
    void deveCriarValidacaoEstoqueExceptionComMensagem() {
        // Act
        ValidacaoEstoqueException exception = new ValidacaoEstoqueException("Estoque insuficiente");

        // Assert
        assertEquals("Estoque insuficiente", exception.getMessage());
        assertEquals("VALIDACAO_ESTOQUE", exception.getErrorCode());
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("Deve criar ValidacaoEstoqueException com mensagem e causa")
    void deveCriarValidacaoEstoqueExceptionComMensagemECausa() {
        // Arrange
        Throwable cause = new RuntimeException("Causa raiz");

        // Act
        ValidacaoEstoqueException exception = new ValidacaoEstoqueException("Estoque insuficiente", cause);

        // Assert
        assertEquals("Estoque insuficiente", exception.getMessage());
        assertEquals("VALIDACAO_ESTOQUE", exception.getErrorCode());
        assertEquals(cause, exception.getCause());
    }

    @Test
    @DisplayName("Deve criar ValidacaoClienteException com mensagem")
    void deveCriarValidacaoClienteExceptionComMensagem() {
        // Act
        ValidacaoClienteException exception = new ValidacaoClienteException("Cliente inválido");

        // Assert
        assertEquals("Cliente inválido", exception.getMessage());
        assertEquals("VALIDACAO_CLIENTE", exception.getErrorCode());
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("Deve criar ValidacaoClienteException com mensagem e causa")
    void deveCriarValidacaoClienteExceptionComMensagemECausa() {
        // Arrange
        Throwable cause = new RuntimeException("Causa raiz");

        // Act
        ValidacaoClienteException exception = new ValidacaoClienteException("Cliente inválido", cause);

        // Assert
        assertEquals("Cliente inválido", exception.getMessage());
        assertEquals("VALIDACAO_CLIENTE", exception.getErrorCode());
        assertEquals(cause, exception.getCause());
    }
}
