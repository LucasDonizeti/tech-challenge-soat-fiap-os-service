package com.techchallenge.os.domain.exceptions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de ClienteNaoEncontradoException - Domain Layer")
class ClienteNaoEncontradoExceptionTest {

    @Test
    @DisplayName("Deve criar exceção com mensagem")
    void deveCriarExcecaoComMensagem() {
        // Arrange
        String mensagem = "Cliente não encontrado";

        // Act
        ClienteNaoEncontradoException exception = new ClienteNaoEncontradoException(mensagem);

        // Assert
        assertNotNull(exception);
        assertEquals(mensagem, exception.getMessage());
        assertEquals("ClienteNaoEncontradoException", exception.getErrorCode());
    }

    @Test
    @DisplayName("Deve criar exceção com mensagem vazia")
    void deveCriarExcecaoComMensagemVazia() {
        // Arrange
        String mensagem = "";

        // Act
        ClienteNaoEncontradoException exception = new ClienteNaoEncontradoException(mensagem);

        // Assert
        assertNotNull(exception);
        assertEquals(mensagem, exception.getMessage());
        assertEquals("ClienteNaoEncontradoException", exception.getErrorCode());
    }

    @Test
    @DisplayName("Deve criar exceção com mensagem e código de erro customizado")
    void deveCriarExcecaoComMensagemEErrorCode() {
        // Arrange
        String mensagem = "Cliente não encontrado customizado";
        String errorCode = "CLIENTE_NOT_FOUND";

        // Act
        ClienteNaoEncontradoException exception = new ClienteNaoEncontradoException(mensagem, errorCode);

        // Assert
        assertNotNull(exception);
        assertEquals(mensagem, exception.getMessage());
        assertEquals(errorCode, exception.getErrorCode());
    }

    @Test
    @DisplayName("Deve lançar e capturar exceção")
    void deveLancarECapturarExcecao() {
        // Arrange
        String mensagem = "Cliente não encontrado";

        // Act & Assert
        ClienteNaoEncontradoException exception = assertThrows(
                ClienteNaoEncontradoException.class,
                () -> {
                    throw new ClienteNaoEncontradoException(mensagem);
                }
        );

        assertEquals(mensagem, exception.getMessage());
        assertEquals("ClienteNaoEncontradoException", exception.getErrorCode());
    }

    @Test
    @DisplayName("Deve ser do tipo DomainException")
    void deveSerTipoDomainException() {
        // Arrange
        String mensagem = "Cliente não encontrado";

        // Act
        ClienteNaoEncontradoException exception = new ClienteNaoEncontradoException(mensagem);

        // Assert
        assertTrue(exception instanceof DomainException);
    }

    @Test
    @DisplayName("Deve ser do tipo RuntimeException")
    void deveSerTipoRuntimeException() {
        // Arrange
        String mensagem = "Cliente não encontrado";

        // Act
        ClienteNaoEncontradoException exception = new ClienteNaoEncontradoException(mensagem);

        // Assert
        assertTrue(exception instanceof RuntimeException);
    }
}
