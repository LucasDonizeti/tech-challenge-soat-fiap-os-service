package com.techchallenge.os.domain.exceptions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de ValorInvalidoException - Domain Layer")
class ValorInvalidoExceptionTest {

    @Test
    @DisplayName("Deve criar exceção com mensagem")
    void deveCriarExcecaoComMensagem() {
        // Arrange
        String mensagem = "Valor inválido";

        // Act
        ValorInvalidoException exception = new ValorInvalidoException(mensagem);

        // Assert
        assertNotNull(exception);
        assertEquals(mensagem, exception.getMessage());
        assertEquals("ValorInvalidoException", exception.getErrorCode());
    }

    @Test
    @DisplayName("Deve criar exceção com mensagem vazia")
    void deveCriarExcecaoComMensagemVazia() {
        // Arrange
        String mensagem = "";

        // Act
        ValorInvalidoException exception = new ValorInvalidoException(mensagem);

        // Assert
        assertNotNull(exception);
        assertEquals(mensagem, exception.getMessage());
        assertEquals("ValorInvalidoException", exception.getErrorCode());
    }

    @Test
    @DisplayName("Deve criar exceção com mensagem e código de erro customizado")
    void deveCriarExcecaoComMensagemEErrorCode() {
        // Arrange
        String mensagem = "Valor inválido customizado";
        String errorCode = "INVALID_VALUE";

        // Act
        ValorInvalidoException exception = new ValorInvalidoException(mensagem, errorCode);

        // Assert
        assertNotNull(exception);
        assertEquals(mensagem, exception.getMessage());
        assertEquals(errorCode, exception.getErrorCode());
    }

    @Test
    @DisplayName("Deve lançar e capturar exceção")
    void deveLancarECapturarExcecao() {
        // Arrange
        String mensagem = "Valor inválido";

        // Act & Assert
        ValorInvalidoException exception = assertThrows(
                ValorInvalidoException.class,
                () -> {
                    throw new ValorInvalidoException(mensagem);
                }
        );

        assertEquals(mensagem, exception.getMessage());
        assertEquals("ValorInvalidoException", exception.getErrorCode());
    }

    @Test
    @DisplayName("Deve ser do tipo DomainException")
    void deveSerTipoDomainException() {
        // Arrange
        String mensagem = "Valor inválido";

        // Act
        ValorInvalidoException exception = new ValorInvalidoException(mensagem);

        // Assert
        assertTrue(exception instanceof DomainException);
    }

    @Test
    @DisplayName("Deve ser do tipo RuntimeException")
    void deveSerTipoRuntimeException() {
        // Arrange
        String mensagem = "Valor inválido";

        // Act
        ValorInvalidoException exception = new ValorInvalidoException(mensagem);

        // Assert
        assertTrue(exception instanceof RuntimeException);
    }
}
