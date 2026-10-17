package com.techchallenge.os.domain.services;

import com.techchallenge.os.application.responses.*;
import com.techchallenge.os.domain.exceptions.NotificacaoException;
import com.techchallenge.os.infrastructure.notifications.NotificacaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de NotificacaoOSService - Domain Layer")
class NotificacaoOSServiceTest {

    @Mock
    private NotificacaoService notificacaoService;

    private NotificacaoOSService notificacaoOSService;

    private OrdemServicoResponse ordemServicoResponse;

    @BeforeEach
    void setUp() {
        notificacaoOSService = new NotificacaoOSService(notificacaoService);

        UUID osId = UUID.randomUUID();
        ordemServicoResponse = OrdemServicoResponse.builder()
                .id(osId)
                .status("AGUARDANDO_APROVACAO")
                .valorTotal(new BigDecimal("500.00"))
                .cliente(ClienteResponse.builder()
                        .id(UUID.randomUUID())
                        .nome("João Silva")
                        .email("joao@teste.com")
                        .build())
                .veiculo(VeiculoResponse.builder()
                        .id(UUID.randomUUID())
                        .placa("ABC1234")
                        .marca("Toyota")
                        .modelo("Corolla")
                        .build())
                .itensServico(List.of(
                        ItemServicoResponse.builder()
                                .id(UUID.randomUUID())
                                .servicoNome("Troca de Óleo")
                                .valorServico(new BigDecimal("150.00"))
                                .build()
                ))
                .build();
    }

    @Test
    @DisplayName("Deve notificar orçamento pronto com sucesso")
    void deveNotificarOrcamentoProntoComSucesso() {
        // Act
        notificacaoOSService.notificarOrcamentoPronto(ordemServicoResponse);

        // Assert
        verify(notificacaoService, times(1)).notificarCliente(
                eq("joao@teste.com"),
                eq("João Silva"),
                eq("Orçamento Pronto - Sua Oficina"),
                any(String.class)
        );
    }

    @Test
    @DisplayName("Deve usar email padrão quando cliente não tem email")
    void deveUsarEmailPadraoQuandoClienteNaoTemEmail() {
        // Arrange
        ordemServicoResponse.setCliente(ClienteResponse.builder()
                .id(UUID.randomUUID())
                .nome("Cliente")
                .email(null)
                .build());

        // Act
        notificacaoOSService.notificarOrcamentoPronto(ordemServicoResponse);

        // Assert
        verify(notificacaoService, times(1)).notificarCliente(
                eq("email-nao-informado@oficina.com"),
                eq("Cliente"),
                eq("Orçamento Pronto - Sua Oficina"),
                any(String.class)
        );
    }

    @Test
    @DisplayName("Deve usar nome padrão quando cliente não tem nome")
    void deveUsarNomePadraoQuandoClienteNaoTemNome() {
        // Arrange
        ordemServicoResponse.setCliente(ClienteResponse.builder()
                .id(UUID.randomUUID())
                .nome(null)
                .email("cliente@teste.com")
                .build());

        // Act
        notificacaoOSService.notificarOrcamentoPronto(ordemServicoResponse);

        // Assert
        verify(notificacaoService, times(1)).notificarCliente(
                eq("cliente@teste.com"),
                eq("Cliente"),
                eq("Orçamento Pronto - Sua Oficina"),
                any(String.class)
        );
    }

    @Test
    @DisplayName("Deve usar valores padrão quando cliente é nulo")
    void deveUsarValoresPadraoQuandoClienteENulo() {
        // Arrange
        ordemServicoResponse.setCliente(null);

        // Act
        notificacaoOSService.notificarOrcamentoPronto(ordemServicoResponse);

        // Assert
        verify(notificacaoService, times(1)).notificarCliente(
                eq("email-nao-informado@oficina.com"),
                eq("Cliente"),
                eq("Orçamento Pronto - Sua Oficina"),
                any(String.class)
        );
    }

    @Test
    @DisplayName("Deve lidar com erro de notificação sem lançar exceção")
    void deveLidarComErroDeNotificacaoSemLancarExcecao() {
        // Arrange
        doThrow(new NotificacaoException("Erro ao enviar email"))
                .when(notificacaoService).notificarCliente(any(), any(), any(), any());

        // Act & Assert - não deve lançar exceção
        assertDoesNotThrow(() -> notificacaoOSService.notificarOrcamentoPronto(ordemServicoResponse));

        verify(notificacaoService, times(1)).notificarCliente(any(), any(), any(), any());
    }

    @Test
    @DisplayName("Deve notificar serviço iniciado com sucesso")
    void deveNotificarServicoIniciadoComSucesso() {
        // Act
        notificacaoOSService.notificarServicoIniciado(ordemServicoResponse);

        // Assert
        verify(notificacaoService, times(1)).notificarCliente(
                eq("joao@teste.com"),
                eq("João Silva"),
                eq("Serviços Iniciados - Sua Oficina"),
                any(String.class)
        );
    }

    @Test
    @DisplayName("Deve notificar serviço finalizado com sucesso")
    void deveNotificarServicoFinalizadoComSucesso() {
        // Act
        notificacaoOSService.notificarServicoFinalizado(ordemServicoResponse);

        // Assert
        verify(notificacaoService, times(1)).notificarCliente(
                eq("joao@teste.com"),
                eq("João Silva"),
                eq("Serviço Finalizado - Sua Oficina"),
                any(String.class)
        );
    }

    @Test
    @DisplayName("Deve notificar orçamento recusado com sucesso")
    void deveNotificarOrcamentoRecusadoComSucesso() {
        // Arrange
        String motivo = "Preço muito alto";

        // Act
        notificacaoOSService.notificarOrcamentoRecusado(ordemServicoResponse, motivo);

        // Assert
        verify(notificacaoService, times(1)).notificarCliente(
                eq("joao@teste.com"),
                eq("João Silva"),
                eq("Orçamento Recusado - Sua Oficina"),
                any(String.class)
        );
    }

    @Test
    @DisplayName("Deve notificar veículo entregue com sucesso")
    void deveNotificarVeiculoEntregueComSucesso() {
        // Act
        notificacaoOSService.notificarVeiculoEntregue(ordemServicoResponse);

        // Assert
        verify(notificacaoService, times(1)).notificarCliente(
                eq("joao@teste.com"),
                eq("João Silva"),
                eq("Veículo Entregue - Sua Oficina"),
                any(String.class)
        );
    }

    @Test
    @DisplayName("Deve incluir dados do veículo na mensagem quando disponível")
    void deveIncluirDadosDoVeiculoNaMensagemQuandoDisponivel() {
        // Act
        notificacaoOSService.notificarOrcamentoPronto(ordemServicoResponse);

        // Assert
        verify(notificacaoService, times(1)).notificarCliente(
                any(),
                any(),
                any(),
                argThat(mensagem -> mensagem.contains("Toyota") && mensagem.contains("Corolla") && mensagem.contains("ABC1234"))
        );
    }

    @Test
    @DisplayName("Deve gerar mensagem sem dados do veículo quando veículo é nulo")
    void deveGerarMensagemSemDadosDoVeiculoQuandoVeiculoENulo() {
        // Arrange
        ordemServicoResponse.setVeiculo(null);

        // Act
        notificacaoOSService.notificarOrcamentoPronto(ordemServicoResponse);

        // Assert
        verify(notificacaoService, times(1)).notificarCliente(
                any(),
                any(),
                any(),
                argThat(mensagem -> mensagem.contains("Olá João Silva") && !mensagem.contains("Toyota"))
        );
    }

    @Test
    @DisplayName("Deve incluir valor total formatado na mensagem de orçamento")
    void deveIncluirValorTotalFormatadoNaMensagemDeOrcamento() {
        // Act
        notificacaoOSService.notificarOrcamentoPronto(ordemServicoResponse);

        // Assert
        verify(notificacaoService, times(1)).notificarCliente(
                any(),
                any(),
                any(),
                argThat(mensagem -> mensagem.contains("R$") && mensagem.contains("500"))
        );
    }

    @Test
    @DisplayName("Deve incluir motivo na mensagem de orçamento recusado")
    void deveIncluirMotivoNaMensagemDeOrcamentoRecusado() {
        // Arrange
        String motivo = "Preço muito alto";

        // Act
        notificacaoOSService.notificarOrcamentoRecusado(ordemServicoResponse, motivo);

        // Assert
        verify(notificacaoService, times(1)).notificarCliente(
                any(),
                any(),
                any(),
                argThat(mensagem -> mensagem.contains(motivo))
        );
    }
}
