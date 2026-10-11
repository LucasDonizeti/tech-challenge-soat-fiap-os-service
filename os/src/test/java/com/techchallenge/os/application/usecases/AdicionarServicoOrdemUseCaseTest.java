package com.techchallenge.os.application.usecases;

import com.techchallenge.os.application.commands.AdicionarServicoOrdemCommand;
import com.techchallenge.os.application.gateway.ports.output.OrdemServicoGateway;
import com.techchallenge.os.application.gateway.ports.output.ServicoGateway;
import com.techchallenge.os.application.gateway.ports.output.models.ServicoResumo;
import com.techchallenge.os.application.responses.OrdemServicoResponse;
import com.techchallenge.os.application.usecases.assemblers.OrdemServicoResponseAssembler;
import com.techchallenge.os.domain.exceptions.OrdemServicoNaoEncontradaException;
import com.techchallenge.os.domain.exceptions.ValidacaoOrdemServicoException;
import com.techchallenge.os.domain.exceptions.ValidacaoServicoException;
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
@DisplayName("Testes de AdicionarServicoOrdemUseCase - Application Layer")
class AdicionarServicoOrdemUseCaseTest {

    @Mock
    private OrdemServicoGateway ordemServicoGateway;

    @Mock
    private ServicoGateway servicoGateway;

    @Mock
    private OrdemServicoResponseAssembler ordemServicoResponseAssembler;

    @InjectMocks
    private AdicionarServicoOrdemUseCase useCase;

    private AdicionarServicoOrdemCommand command;
    private OrdemServico ordemServico;
    private ServicoResumo servicoResumo;
    private OrdemServicoResponse response;

    @BeforeEach
    void setUp() {
        UUID ordemServicoId = UUID.randomUUID();
        UUID servicoId = UUID.randomUUID();
        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();

        command = new AdicionarServicoOrdemCommand(ordemServicoId, servicoId);

        servicoResumo = ServicoResumo.builder()
                .id(servicoId)
                .nome("Troca de Óleo")
                .descricao("Troca completa de óleo do motor")
                .preco(new BigDecimal("150.00"))
                .build();

        ordemServico = OrdemServico.criar(clienteId, veiculoId);

        response = OrdemServicoResponse.builder()
                .id(ordemServicoId)
                .status(StatusOS.RECEBIDA.name())
                .build();
    }

    @Test
    @DisplayName("Deve adicionar serviço à ordem de serviço com sucesso quando status é RECEBIDA")
    void deveAdicionarServicoAOrdemServicoComSucesso() {
        // Arrange
        when(ordemServicoGateway.findById(command.getOrdemServicoId())).thenReturn(Optional.of(ordemServico));
        when(servicoGateway.findById(command.getServicoId())).thenReturn(Optional.of(servicoResumo));
        when(ordemServicoGateway.save(any(OrdemServico.class))).thenReturn(ordemServico);
        when(ordemServicoResponseAssembler.montar(any(OrdemServico.class))).thenReturn(response);

        // Act
        OrdemServicoResponse result = useCase.execute(command);

        // Assert
        assertNotNull(result);
        assertEquals(ordemServico.getId(), result.getId());

        verify(ordemServicoGateway, times(1)).findById(command.getOrdemServicoId());
        verify(servicoGateway, times(1)).findById(command.getServicoId());
        verify(ordemServicoGateway, times(1)).save(any(OrdemServico.class));
        verify(ordemServicoResponseAssembler, times(1)).montar(any(OrdemServico.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando ordem de serviço não está no status RECEBIDA")
    void deveLancarExcecaoQuandoOrdemServicoNaoEstaRecebida() {
        // Arrange
        ordemServico.atualizarStatus(StatusOS.EM_DIAGNOSTICO);
        when(ordemServicoGateway.findById(command.getOrdemServicoId())).thenReturn(Optional.of(ordemServico));
        when(servicoGateway.findById(command.getServicoId())).thenReturn(Optional.of(servicoResumo));

        // Act & Assert
        ValidacaoOrdemServicoException exception = assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> useCase.execute(command)
        );

        assertTrue(exception.getMessage().contains("Não é permitido adicionar ou remover serviços quando a OS está no status"));

        verify(ordemServicoGateway, times(1)).findById(command.getOrdemServicoId());
        verify(servicoGateway, times(1)).findById(command.getServicoId());
        verify(ordemServicoGateway, never()).save(any());
        verify(ordemServicoResponseAssembler, never()).montar(any());
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
        verify(servicoGateway, never()).findById(any());
        verify(ordemServicoGateway, never()).save(any());
        verify(ordemServicoResponseAssembler, never()).montar(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando serviço não encontrado")
    void deveLancarExcecaoQuandoServicoNaoEncontrado() {
        // Arrange
        when(ordemServicoGateway.findById(command.getOrdemServicoId())).thenReturn(Optional.of(ordemServico));
        when(servicoGateway.findById(command.getServicoId())).thenReturn(Optional.empty());

        // Act & Assert
        ValidacaoServicoException exception = assertThrows(
                ValidacaoServicoException.class,
                () -> useCase.execute(command)
        );

        assertTrue(exception.getMessage().contains("MRO não encontrado"));

        verify(ordemServicoGateway, times(1)).findById(command.getOrdemServicoId());
        verify(servicoGateway, times(1)).findById(command.getServicoId());
        verify(ordemServicoGateway, never()).save(any());
        verify(ordemServicoResponseAssembler, never()).montar(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando comando é nulo")
    void deveLancarExcecaoQuandoComandoNulo() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> useCase.execute(null));

        verify(ordemServicoGateway, never()).findById(any());
        verify(servicoGateway, never()).findById(any());
        verify(ordemServicoGateway, never()).save(any());
        verify(ordemServicoResponseAssembler, never()).montar(any());
    }

    @Test
    @DisplayName("Deve propagar exceção quando repository falha")
    void devePropagarExcecaoQuandoRepositoryFalha() {
        // Arrange
        when(ordemServicoGateway.findById(command.getOrdemServicoId())).thenReturn(Optional.of(ordemServico));
        when(servicoGateway.findById(command.getServicoId())).thenReturn(Optional.of(servicoResumo));
        when(ordemServicoGateway.save(any(OrdemServico.class))).thenThrow(new RuntimeException("Erro ao salvar no banco"));

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> useCase.execute(command)
        );

        assertEquals("Erro ao salvar no banco", exception.getMessage());

        verify(ordemServicoGateway, times(1)).findById(command.getOrdemServicoId());
        verify(servicoGateway, times(1)).findById(command.getServicoId());
        verify(ordemServicoGateway, times(1)).save(any(OrdemServico.class));
        verify(ordemServicoResponseAssembler, never()).montar(any());
    }
}
