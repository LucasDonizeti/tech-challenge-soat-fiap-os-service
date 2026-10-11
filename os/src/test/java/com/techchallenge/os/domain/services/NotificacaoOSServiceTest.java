package com.techchallenge.os.domain.services;

import com.techchallenge.os.application.responses.ClienteResponse;
import com.techchallenge.os.application.responses.ItemServicoResponse;
import com.techchallenge.os.application.responses.OrdemServicoResponse;
import com.techchallenge.os.application.responses.VeiculoResponse;
import com.techchallenge.os.domain.exceptions.NotificacaoException;
import com.techchallenge.os.domain.models.valueobjects.StatusItemServico;
import com.techchallenge.os.infrastructure.notifications.NotificacaoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do serviço de notificação da OS")
class NotificacaoOSServiceTest {

    @Mock
    private NotificacaoService notificacaoService;

    @InjectMocks
    private NotificacaoOSService notificacaoOSService;

    @Test
    @DisplayName("Deve notificar orçamento pronto")
    void notificarOrcamentoPronto_deveChamarNotificacaoService() throws NotificacaoException {
        OrdemServicoResponse ordemServico = criarOrdemServicoResponse();

        notificacaoOSService.notificarOrcamentoPronto(ordemServico);

        verify(notificacaoService).notificarCliente(
                eq("joao@email.com"),
                eq("João Silva"),
                eq("Orçamento Pronto - Sua Oficina"),
                argThat(mensagem -> mensagem.contains("João Silva")
                        && mensagem.contains("Fiat")
                        && mensagem.contains("Uno")
                        && mensagem.contains("ABC1234")
                        && mensagem.contains("Troca de Óleo"))
        );
    }

    @Test
    @DisplayName("Deve tratar exceção de notificação sem lançar erro")
    void notificarOrcamentoPronto_deveTratarExcecaoSemLancar() {
        OrdemServicoResponse ordemServico = criarOrdemServicoResponse();
        doThrow(new NotificacaoException("Erro de teste"))
                .when(notificacaoService).notificarCliente(anyString(), anyString(), anyString(), anyString());

        assertDoesNotThrow(() -> notificacaoOSService.notificarOrcamentoPronto(ordemServico));
    }

    @Test
    @DisplayName("Deve notificar serviço iniciado")
    void notificarServicoIniciado_deveChamarNotificacaoService() throws NotificacaoException {
        OrdemServicoResponse ordemServico = criarOrdemServicoResponse();

        notificacaoOSService.notificarServicoIniciado(ordemServico);

        verify(notificacaoService).notificarCliente(
                eq("joao@email.com"),
                eq("João Silva"),
                eq("Serviços Iniciados - Sua Oficina"),
                argThat(mensagem -> mensagem.contains("Iniciamos os serviços")
                        && mensagem.contains("Fiat")
                        && mensagem.contains("Uno"))
        );
    }

    @Test
    @DisplayName("Deve notificar serviço finalizado")
    void notificarServicoFinalizado_deveChamarNotificacaoService() throws NotificacaoException {
        OrdemServicoResponse ordemServico = criarOrdemServicoResponse();

        notificacaoOSService.notificarServicoFinalizado(ordemServico);

        verify(notificacaoService).notificarCliente(
                eq("joao@email.com"),
                eq("João Silva"),
                eq("Serviço Finalizado - Sua Oficina"),
                argThat(mensagem -> mensagem.contains("foi finalizado")
                        && mensagem.contains("Valor total"))
        );
    }

    @Test
    @DisplayName("Deve notificar veículo entregue")
    void notificarVeiculoEntregue_deveChamarNotificacaoService() throws NotificacaoException {
        OrdemServicoResponse ordemServico = criarOrdemServicoResponse();

        notificacaoOSService.notificarVeiculoEntregue(ordemServico);

        verify(notificacaoService).notificarCliente(
                eq("joao@email.com"),
                eq("João Silva"),
                eq("Veículo Entregue - Sua Oficina"),
                argThat(mensagem -> mensagem.contains("foi entregue")
                        && mensagem.contains("Fiat"))
        );
    }

    @Test
    @DisplayName("Deve notificar orçamento recusado")
    void notificarOrcamentoRecusado_deveChamarNotificacaoService() throws NotificacaoException {
        OrdemServicoResponse ordemServico = criarOrdemServicoResponse();
        String motivo = "Preço muito alto";

        notificacaoOSService.notificarOrcamentoRecusado(ordemServico, motivo);

        verify(notificacaoService).notificarCliente(
                eq("joao@email.com"),
                eq("João Silva"),
                eq("Orçamento Recusado - Sua Oficina"),
                argThat(mensagem -> mensagem.contains(motivo)
                        && mensagem.contains("foi cancelada"))
        );
    }

    private OrdemServicoResponse criarOrdemServicoResponse() {
        UUID ordemId = UUID.randomUUID();

        ClienteResponse cliente = ClienteResponse.builder()
                .id(UUID.randomUUID())
                .nome("João Silva")
                .cpf("52998224725")
                .email("joao@email.com")
                .build();

        VeiculoResponse veiculo = VeiculoResponse.builder()
                .id(UUID.randomUUID())
                .placa("ABC1234")
                .marca("Fiat")
                .modelo("Uno")
                .ano(2020)
                .cor("Branco")
                .build();

        ItemServicoResponse item = ItemServicoResponse.builder()
                .id(UUID.randomUUID())
                .servicoId(UUID.randomUUID())
                .servicoNome("Troca de Óleo")
                .servicoDescricao("Troca completa de óleo")
                .status(StatusItemServico.PENDENTE)
                .valorServico(new BigDecimal("100.00"))
                .valorMro(BigDecimal.ZERO)
                .valorTotal(new BigDecimal("100.00"))
                .dataInicioExecucao(LocalDateTime.now())
                .dataFinalizacao(null)
                .build();

        return OrdemServicoResponse.builder()
                .id(ordemId)
                .cliente(cliente)
                .veiculo(veiculo)
                .status("EM_EXECUCAO")
                .valorTotal(new BigDecimal("100.00"))
                .itensServico(List.of(item))
                .build();
    }
}
