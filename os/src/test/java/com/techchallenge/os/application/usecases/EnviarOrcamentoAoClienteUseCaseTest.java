package com.techchallenge.os.application.usecases;

import com.techchallenge.os.application.commands.EnviarOrcamentoAoClienteCommand;
import com.techchallenge.os.application.gateway.ports.output.OrdemServicoGateway;
import com.techchallenge.os.application.responses.OrdemServicoResponse;
import com.techchallenge.os.application.usecases.assemblers.OrdemServicoResponseAssembler;
import com.techchallenge.os.domain.exceptions.OrdemServicoNaoEncontradaException;
import com.techchallenge.os.domain.exceptions.ValidacaoOrdemServicoException;
import com.techchallenge.os.domain.models.ItemServico;
import com.techchallenge.os.domain.models.OrdemServico;
import com.techchallenge.os.domain.models.valueobjects.StatusOS;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de EnviarOrcamentoAoClienteUseCase - Application Layer")
class EnviarOrcamentoAoClienteUseCaseTest {

    @Mock
    private OrdemServicoGateway ordemServicoGateway;

    @Mock
    private OrdemServicoResponseAssembler ordemServicoResponseAssembler;

    @InjectMocks
    private EnviarOrcamentoAoClienteUseCase useCase;

    private EnviarOrcamentoAoClienteCommand command;
    private OrdemServico ordemServico;
    private ItemServico itemServico;
    private OrdemServicoResponse response;

    @BeforeEach
    void setUp() {
        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();
        ordemServico = OrdemServico.criar(clienteId, veiculoId);

        UUID servicoId = UUID.randomUUID();
        itemServico = ItemServico.criarComDados(servicoId, "Troca de Óleo", "Troca completa de óleo", new BigDecimal("150.00"));
        ordemServico.adicionarItemServico(itemServico);

        ordemServico.enviarParaDiagnostico();

        UUID ordemServicoId = ordemServico.getId();
        command = new EnviarOrcamentoAoClienteCommand(ordemServicoId);

        response = OrdemServicoResponse.builder()
                .id(ordemServicoId)
                .status(StatusOS.AGUARDANDO_APROVACAO.name())
                .build();
    }

    @Test
    @DisplayName("Deve enviar orçamento ao cliente com sucesso")
    void deveEnviarOrcamentoAoClienteComSucesso() {
        // Arrange
        when(ordemServicoGateway.findById(command.getOrdemServicoId())).thenReturn(Optional.of(ordemServico));
        when(ordemServicoGateway.save(any(OrdemServico.class))).thenReturn(ordemServico);
        when(ordemServicoResponseAssembler.montar(any(OrdemServico.class))).thenReturn(response);

        // Act
        OrdemServicoResponse result = useCase.execute(command);

        // Assert
        assertNotNull(result);
        assertEquals(ordemServico.getId(), result.getId());
        assertEquals(StatusOS.AGUARDANDO_APROVACAO.name(), result.getStatus());

        verify(ordemServicoGateway, times(1)).findById(command.getOrdemServicoId());
        verify(ordemServicoGateway, times(1)).save(any(OrdemServico.class));
        verify(ordemServicoResponseAssembler, times(1)).montar(any(OrdemServico.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando ordem de serviço não encontrada")
    void deveLancarExcecaoQuandoOrdemServicoNaoEncontrada() {
        // Arrange
        when(ordemServicoGateway.findById(command.getOrdemServicoId())).thenReturn(Optional.empty());

        // Act & Assert
        OrdemServicoNaoEncontradaException exception = assertThrows(
                OrdemServicoNaoEncontradaException.class,
                () -> useCase.execute(command)
        );

        assertTrue(exception.getMessage().contains("Ordem de Serviço não encontrada"));

        verify(ordemServicoGateway, times(1)).findById(command.getOrdemServicoId());
        verify(ordemServicoGateway, never()).save(any());
        verify(ordemServicoResponseAssembler, never()).montar(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando ordem de serviço não está em EM_DIAGNOSTICO")
    void deveLancarExcecaoQuandoOrdemServicoNaoEstaEmDiagnostico() {
        // Arrange
        UUID clienteId2 = UUID.randomUUID();
        UUID veiculoId2 = UUID.randomUUID();
        OrdemServico ordemServicoRecebida = OrdemServico.criar(clienteId2, veiculoId2);
        when(ordemServicoGateway.findById(command.getOrdemServicoId())).thenReturn(Optional.of(ordemServicoRecebida));

        // Act & Assert
        ValidacaoOrdemServicoException exception = assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> useCase.execute(command)
        );

        assertTrue(exception.getMessage().contains("Só é possível enviar orçamento ao cliente quando a OS está no status EM_DIAGNOSTICO"));

        verify(ordemServicoGateway, times(1)).findById(command.getOrdemServicoId());
        verify(ordemServicoGateway, never()).save(any());
        verify(ordemServicoResponseAssembler, never()).montar(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando ordem de serviço não tem serviços")
    void deveLancarExcecaoQuandoOrdemServicoNaoTemServicos() {
        // Arrange
        UUID clienteId2 = UUID.randomUUID();
        UUID veiculoId2 = UUID.randomUUID();
        OrdemServico ordemServicoSemServicos = OrdemServico.criar(clienteId2, veiculoId2);
        // Simula que a OS já está em EM_DIAGNOSTICO mas sem serviços (caso edge)
        ordemServicoSemServicos.atualizarStatus(StatusOS.EM_DIAGNOSTICO);

        when(ordemServicoGateway.findById(command.getOrdemServicoId())).thenReturn(Optional.of(ordemServicoSemServicos));

        // Act & Assert
        ValidacaoOrdemServicoException exception = assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> useCase.execute(command)
        );

        assertTrue(exception.getMessage().contains("OS deve ter pelo menos um serviço para enviar orçamento ao cliente"));

        verify(ordemServicoGateway, times(1)).findById(command.getOrdemServicoId());
        verify(ordemServicoGateway, never()).save(any());
        verify(ordemServicoResponseAssembler, never()).montar(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando comando é nulo")
    void deveLancarExcecaoQuandoComandoNulo() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> useCase.execute(null));

        verify(ordemServicoGateway, never()).findById(any());
        verify(ordemServicoGateway, never()).save(any());
        verify(ordemServicoResponseAssembler, never()).montar(any());
    }
}
