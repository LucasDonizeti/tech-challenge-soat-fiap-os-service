package com.techchallenge.os.application.usecases;

import com.techchallenge.os.application.commands.AdicionarMROServicoCommand;
import com.techchallenge.os.application.gateway.ports.output.MroGateway;
import com.techchallenge.os.application.gateway.ports.output.OrdemServicoGateway;
import com.techchallenge.os.application.gateway.ports.output.models.MROResumo;
import com.techchallenge.os.application.responses.OrdemServicoResponse;
import com.techchallenge.os.application.usecases.assemblers.OrdemServicoResponseAssembler;
import com.techchallenge.os.domain.exceptions.ItemServicoNaoEncontradoException;
import com.techchallenge.os.domain.exceptions.OrdemServicoNaoEncontradaException;
import com.techchallenge.os.domain.exceptions.OrdemServicoStatusInvalidoException;
import com.techchallenge.os.domain.exceptions.ValidacaoMROException;
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
@DisplayName("Testes de AdicionarMROServicoUseCase - Application Layer")
class AdicionarMROServicoUseCaseTest {

    @Mock
    private OrdemServicoGateway ordemServicoGateway;

    @Mock
    private MroGateway mroGateway;

    @Mock
    private OrdemServicoResponseAssembler ordemServicoResponseAssembler;

    @InjectMocks
    private AdicionarMROServicoUseCase useCase;

    private AdicionarMROServicoCommand command;
    private OrdemServico ordemServico;
    private ItemServico itemServico;
    private MROResumo mroResumo;
    private OrdemServicoResponse response;

    @BeforeEach
    void setUp() {
        UUID servicoId = UUID.randomUUID();
        itemServico = ItemServico.criarComDados(servicoId, "Troca de Óleo", "Troca completa de óleo", new BigDecimal("150.00"));

        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();
        ordemServico = OrdemServico.criar(clienteId, veiculoId);
        ordemServico.adicionarItemServico(itemServico);

        UUID ordemServicoId = ordemServico.getId();
        UUID itemServicoId = itemServico.getId();
        UUID mroId = UUID.randomUUID();

        command = new AdicionarMROServicoCommand(ordemServicoId, itemServicoId, mroId, 5);

        mroResumo = MROResumo.builder()
                .id(mroId)
                .nome("Óleo Motor")
                .descricao("Óleo para motor")
                .precoUnitario(new BigDecimal("45.90"))
                .build();

        response = OrdemServicoResponse.builder()
                .id(ordemServicoId)
                .status(StatusOS.RECEBIDA.name())
                .build();
    }

    @Test
    @DisplayName("Deve adicionar MRO ao serviço com sucesso quando status é RECEBIDA")
    void deveAdicionarMROAoServicoComSucesso() {
        // Arrange
        when(ordemServicoGateway.findById(command.getOrdemServicoId())).thenReturn(Optional.of(ordemServico));
        when(mroGateway.findById(command.getMroId())).thenReturn(Optional.of(mroResumo));
        when(ordemServicoGateway.save(any(OrdemServico.class))).thenReturn(ordemServico);
        when(ordemServicoResponseAssembler.montar(any(OrdemServico.class))).thenReturn(response);

        // Act
        OrdemServicoResponse result = useCase.execute(command);

        // Assert
        assertNotNull(result);
        assertEquals(ordemServico.getId(), result.getId());

        verify(ordemServicoGateway, times(1)).findById(command.getOrdemServicoId());
        verify(mroGateway, times(1)).findById(command.getMroId());
        verify(ordemServicoGateway, times(1)).save(any(OrdemServico.class));
        verify(ordemServicoResponseAssembler, times(1)).montar(any(OrdemServico.class));
    }

    @Test
    @DisplayName("Deve adicionar MRO ao serviço com sucesso quando status é EM_DIAGNOSTICO")
    void deveAdicionarMROAoServicoComSucessoEmDiagnostico() {
        // Arrange
        ordemServico.enviarParaDiagnostico();
        when(ordemServicoGateway.findById(command.getOrdemServicoId())).thenReturn(Optional.of(ordemServico));
        when(mroGateway.findById(command.getMroId())).thenReturn(Optional.of(mroResumo));
        when(ordemServicoGateway.save(any(OrdemServico.class))).thenReturn(ordemServico);
        when(ordemServicoResponseAssembler.montar(any(OrdemServico.class))).thenReturn(response);

        // Act
        OrdemServicoResponse result = useCase.execute(command);

        // Assert
        assertNotNull(result);
        assertEquals(ordemServico.getId(), result.getId());

        verify(ordemServicoGateway, times(1)).findById(command.getOrdemServicoId());
        verify(mroGateway, times(1)).findById(command.getMroId());
        verify(ordemServicoGateway, times(1)).save(any(OrdemServico.class));
        verify(ordemServicoResponseAssembler, times(1)).montar(any(OrdemServico.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando ordem de serviço não está no status RECEBIDA ou EM_DIAGNOSTICO")
    void deveLancarExcecaoQuandoOrdemServicoNaoEstaRecebida() {
        // Arrange
        ordemServico.atualizarStatus(StatusOS.EM_EXECUCAO);
        when(ordemServicoGateway.findById(command.getOrdemServicoId())).thenReturn(Optional.of(ordemServico));

        // Act & Assert
        OrdemServicoStatusInvalidoException exception = assertThrows(
                OrdemServicoStatusInvalidoException.class,
                () -> useCase.execute(command)
        );

        assertTrue(exception.getMessage().contains("Só é possível adicionar MROs quando a Ordem de Serviço está nos status RECEBIDA ou EM_DIAGNOSTICO"));

        verify(ordemServicoGateway, times(1)).findById(command.getOrdemServicoId());
        verify(mroGateway, never()).findById(any());
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
        verify(mroGateway, never()).findById(any());
        verify(ordemServicoGateway, never()).save(any());
        verify(ordemServicoResponseAssembler, never()).montar(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando item de serviço não encontrado")
    void deveLancarExcecaoQuandoItemServicoNaoEncontrado() {
        // Arrange
        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();
        OrdemServico ordemServicoSemItem = OrdemServico.criar(clienteId, veiculoId);

        when(ordemServicoGateway.findById(command.getOrdemServicoId())).thenReturn(Optional.of(ordemServicoSemItem));

        // Act & Assert
        ItemServicoNaoEncontradoException exception = assertThrows(
                ItemServicoNaoEncontradoException.class,
                () -> useCase.execute(command)
        );

        assertTrue(exception.getMessage().contains("Item de Serviço não encontrado"));

        verify(ordemServicoGateway, times(1)).findById(command.getOrdemServicoId());
        verify(mroGateway, never()).findById(any());
        verify(ordemServicoGateway, never()).save(any());
        verify(ordemServicoResponseAssembler, never()).montar(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando MRO não encontrado")
    void deveLancarExcecaoQuandoMRONaoEncontrado() {
        // Arrange
        when(ordemServicoGateway.findById(command.getOrdemServicoId())).thenReturn(Optional.of(ordemServico));
        when(mroGateway.findById(command.getMroId())).thenReturn(Optional.empty());

        // Act & Assert
        ValidacaoMROException exception = assertThrows(
                ValidacaoMROException.class,
                () -> useCase.execute(command)
        );

        assertTrue(exception.getMessage().contains("MRO não encontrado"));

        verify(ordemServicoGateway, times(1)).findById(command.getOrdemServicoId());
        verify(mroGateway, times(1)).findById(command.getMroId());
        verify(ordemServicoGateway, never()).save(any());
        verify(ordemServicoResponseAssembler, never()).montar(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando comando é nulo")
    void deveLancarExcecaoQuandoComandoNulo() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> useCase.execute(null));

        verify(ordemServicoGateway, never()).findById(any());
        verify(mroGateway, never()).findById(any());
        verify(ordemServicoGateway, never()).save(any());
        verify(ordemServicoResponseAssembler, never()).montar(any());
    }

}
