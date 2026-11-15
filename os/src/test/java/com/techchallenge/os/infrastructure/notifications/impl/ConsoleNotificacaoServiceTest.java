package com.techchallenge.os.infrastructure.notifications.impl;

import com.techchallenge.os.domain.exceptions.NotificacaoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de ConsoleNotificacaoService - Infrastructure Layer")
class ConsoleNotificacaoServiceTest {

    private final ConsoleNotificacaoService service = new ConsoleNotificacaoService();

    @Test
    @DisplayName("Deve notificar cliente com sucesso")
    void deveNotificarClienteComSucesso() throws NotificacaoException {
        // Arrange
        String email = "cliente@email.com";
        String nome = "João Silva";
        String assunto = "Orçamento Pronto";
        String mensagem = "Seu orçamento está pronto para aprovação.";

        // Act & Assert
        assertDoesNotThrow(() -> service.notificarCliente(email, nome, assunto, mensagem));
    }

    @Test
    @DisplayName("Deve lançar exceção quando email é null")
    void deveLancarExcecaoQuandoEmailENull() {
        // Arrange
        String email = null;
        String nome = "João Silva";
        String assunto = "Orçamento Pronto";
        String mensagem = "Seu orçamento está pronto para aprovação.";

        // Act & Assert
        NotificacaoException exception = assertThrows(
                NotificacaoException.class,
                () -> service.notificarCliente(email, nome, assunto, mensagem)
        );

        assertEquals("Email não pode ser nulo", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando nome é null")
    void deveLancarExcecaoQuandoNomeENull() {
        // Arrange
        String email = "cliente@email.com";
        String nome = null;
        String assunto = "Orçamento Pronto";
        String mensagem = "Seu orçamento está pronto para aprovação.";

        // Act & Assert
        NotificacaoException exception = assertThrows(
                NotificacaoException.class,
                () -> service.notificarCliente(email, nome, assunto, mensagem)
        );

        assertEquals("Nome não pode ser nulo", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando assunto é null")
    void deveLancarExcecaoQuandoAssuntoENull() {
        // Arrange
        String email = "cliente@email.com";
        String nome = "João Silva";
        String assunto = null;
        String mensagem = "Seu orçamento está pronto para aprovação.";

        // Act & Assert
        NotificacaoException exception = assertThrows(
                NotificacaoException.class,
                () -> service.notificarCliente(email, nome, assunto, mensagem)
        );

        assertEquals("Assunto não pode ser nulo", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando mensagem é null")
    void deveLancarExcecaoQuandoMensagemENull() {
        // Arrange
        String email = "cliente@email.com";
        String nome = "João Silva";
        String assunto = "Orçamento Pronto";
        String mensagem = null;

        // Act & Assert
        NotificacaoException exception = assertThrows(
                NotificacaoException.class,
                () -> service.notificarCliente(email, nome, assunto, mensagem)
        );

        assertEquals("Mensagem não pode ser nula", exception.getMessage());
    }

    @Test
    @DisplayName("Deve notificar com strings vazias")
    void deveNotificarComStringsVazias() throws NotificacaoException {
        // Arrange
        String email = "";
        String nome = "";
        String assunto = "";
        String mensagem = "";

        // Act & Assert
        assertDoesNotThrow(() -> service.notificarCliente(email, nome, assunto, mensagem));
    }

    @Test
    @DisplayName("Deve notificar com mensagem longa")
    void deveNotificarComMensagemLonga() throws NotificacaoException {
        // Arrange
        String email = "cliente@email.com";
        String nome = "João Silva";
        String assunto = "Orçamento Pronto";
        String mensagem = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. ".repeat(100);

        // Act & Assert
        assertDoesNotThrow(() -> service.notificarCliente(email, nome, assunto, mensagem));
    }

    @Test
    @DisplayName("Deve notificar com caracteres especiais")
    void deveNotificarComCaracteresEspeciais() throws NotificacaoException {
        // Arrange
        String email = "cliente+test@email.com";
        String nome = "João Silva";
        String assunto = "Orçamento Pronto - Áçêntós";
        String mensagem = "Mensagem com caracteres especiais: @#$%&*()_+-=[]{}|;:,.<>?/~`";

        // Act & Assert
        assertDoesNotThrow(() -> service.notificarCliente(email, nome, assunto, mensagem));
    }

    @Test
    @DisplayName("Deve notificar cliente jurídico")
    void deveNotificarClienteJuridico() throws NotificacaoException {
        // Arrange
        String email = "contato@empresa.com";
        String nome = "Empresa Ltda";
        String assunto = "Orçamento Pronto";
        String mensagem = "Seu orçamento está pronto para aprovação.";

        // Act & Assert
        assertDoesNotThrow(() -> service.notificarCliente(email, nome, assunto, mensagem));
    }

    @Test
    @DisplayName("Deve lançar exceção quando todos os parâmetros são null")
    void deveLancarExcecaoQuandoTodosOsParametrosSaoNull() {
        // Act & Assert
        NotificacaoException exception = assertThrows(
                NotificacaoException.class,
                () -> service.notificarCliente(null, null, null, null)
        );

        assertEquals("Email não pode ser nulo", exception.getMessage());
    }
}
