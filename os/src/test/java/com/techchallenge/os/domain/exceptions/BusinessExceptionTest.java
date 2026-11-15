package com.techchallenge.os.domain.exceptions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de BusinessException - Domain Layer")
class BusinessExceptionTest {

    @Test
    @DisplayName("Deve criar exceção com mensagem")
    void deveCriarExcecaoComMensagem() {
        // Arrange
        String mensagem = "Erro de negócio";

        // Act
        BusinessException exception = new BusinessException(mensagem);

        // Assert
        assertNotNull(exception);
        assertEquals(mensagem, exception.getMessage());
        assertEquals("BusinessException", exception.getErrorCode());
    }

    @Test
    @DisplayName("Deve criar exceção com mensagem vazia")
    void deveCriarExcecaoComMensagemVazia() {
        // Arrange
        String mensagem = "";

        // Act
        BusinessException exception = new BusinessException(mensagem);

        // Assert
        assertNotNull(exception);
        assertEquals(mensagem, exception.getMessage());
        assertEquals("BusinessException", exception.getErrorCode());
    }

    @Test
    @DisplayName("Deve criar exceção com mensagem e código de erro customizado")
    void deveCriarExcecaoComMensagemEErrorCode() {
        // Arrange
        String mensagem = "Erro de negócio customizado";
        String errorCode = "CUSTOM_ERROR";

        // Act
        BusinessException exception = new BusinessException(mensagem, errorCode);

        // Assert
        assertNotNull(exception);
        assertEquals(mensagem, exception.getMessage());
        assertEquals(errorCode, exception.getErrorCode());
    }

    @Test
    @DisplayName("Deve lançar e capturar exceção")
    void deveLancarECapturarExcecao() {
        // Arrange
        String mensagem = "Erro de negócio";

        // Act & Assert
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> {
                    throw new BusinessException(mensagem);
                }
        );

        assertEquals(mensagem, exception.getMessage());
        assertEquals("BusinessException", exception.getErrorCode());
    }

    @Test
    @DisplayName("Deve ser do tipo DomainException")
    void deveSerTipoDomainException() {
        // Arrange
        String mensagem = "Erro de negócio";

        // Act
        BusinessException exception = new BusinessException(mensagem);

        // Assert
        assertTrue(exception instanceof DomainException);
    }

    @Test
    @DisplayName("Deve ser do tipo RuntimeException")
    void deveSerTipoRuntimeException() {
        // Arrange
        String mensagem = "Erro de negócio";

        // Act
        BusinessException exception = new BusinessException(mensagem);

        // Assert
        assertTrue(exception instanceof RuntimeException);
    }
}
