package com.techchallenge.os.application.usecases;

import com.techchallenge.os.application.commands.CancelarServicoCommand;
import com.techchallenge.os.application.gateway.ports.output.OrdemServicoGateway;
import com.techchallenge.os.application.responses.OrdemServicoResponse;
import com.techchallenge.os.application.usecases.assemblers.OrdemServicoResponseAssembler;
import com.techchallenge.os.domain.exceptions.ItemServicoNaoEncontradoException;
import com.techchallenge.os.domain.exceptions.OrdemServicoNaoEncontradaException;
import com.techchallenge.os.domain.exceptions.ValidacaoEstoqueException;
import com.techchallenge.os.domain.models.ItemServico;
import com.techchallenge.os.domain.models.OrdemServico;
import com.techchallenge.os.domain.models.valueobjects.StatusItemServico;
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
@DisplayName("Testes do Use Case de Cancelar Serviço")
class CancelarServicoUseCaseTest {

    @Mock
    private OrdemServicoGateway ordemServicoGateway;

    @Mock
    private OrdemServicoResponseAssembler ordemServicoResponseAssembler;

    @InjectMocks
    private CancelarServicoUseCase useCase;

    private OrdemServico ordemServico;
    private ItemServico itemServico;
    private UUID ordemServicoId;
    private UUID itemServicoId;
    private OrdemServicoResponse response;

    @BeforeEach
    void setUp() {
        ordemServicoId = UUID.randomUUID();

        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();
        ordemServico = OrdemServico.criar(clienteId, veiculoId);

        itemServico = ItemServico.criarComDados(UUID.randomUUID(), "Troca de Óleo", "Troca completa", new BigDecimal("150.00"));
        ordemServico.adicionarItemServico(itemServico);
        itemServicoId = itemServico.getId();

        // Add a second item to test removal
        ItemServico itemServico2 = ItemServico.criarComDados(UUID.randomUUID(), "Troca de Filtro", "Troca de filtro de ar", new BigDecimal("50.00"));
        ordemServico.adicionarItemServico(itemServico2);

        ordemServico.enviarParaDiagnostico();
        ordemServico.enviarOrcamentoAoCliente();
        ordemServico.aprovarOrcamento();

        response = OrdemServicoResponse.builder()
                .id(ordemServicoId)
                .build();
    }

    @Test
    @DisplayName("Deve cancelar serviço com sucesso")
    void deveCancelarServicoComSucesso() {
        // Arrange
        CancelarServicoCommand command = CancelarServicoCommand.builder()
                .ordemServicoId(ordemServicoId)
                .itemServicoId(itemServicoId)
                .build();

        when(ordemServicoGateway.findById(ordemServicoId)).thenReturn(Optional.of(ordemServico));
        when(ordemServicoGateway.save(any(OrdemServico.class))).thenReturn(ordemServico);
        when(ordemServicoResponseAssembler.montar(any(OrdemServico.class))).thenReturn(response);

        // Act
        OrdemServicoResponse result = useCase.execute(command);

        // Assert
        assertEquals(StatusItemServico.CANCELADO, itemServico.getStatus());
        verify(ordemServicoGateway, times(1)).save(ordemServico);
        verify(ordemServicoResponseAssembler, times(1)).montar(any(OrdemServico.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando OS não encontrada")
    void deveLancarExcecaoQuandoOSNaoEncontrada() {
        // Arrange
        CancelarServicoCommand command = CancelarServicoCommand.builder()
                .ordemServicoId(ordemServicoId)
                .itemServicoId(itemServicoId)
                .build();

        when(ordemServicoGateway.findById(ordemServicoId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(OrdemServicoNaoEncontradaException.class, () -> useCase.execute(command));
        verify(ordemServicoGateway, never()).save(any());
        verify(ordemServicoResponseAssembler, never()).montar(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando ItemServico não encontrado")
    void deveLancarExcecaoQuandoItemServicoNaoEncontrado() {
        // Arrange
        UUID nonExistentItemId = UUID.randomUUID();

        CancelarServicoCommand command = CancelarServicoCommand.builder()
                .ordemServicoId(ordemServicoId)
                .itemServicoId(nonExistentItemId)
                .build();

        when(ordemServicoGateway.findById(ordemServicoId)).thenReturn(Optional.of(ordemServico));

        // Act & Assert
        assertThrows(ItemServicoNaoEncontradoException.class, () -> useCase.execute(command));
        verify(ordemServicoGateway, never()).save(any());
        verify(ordemServicoResponseAssembler, never()).montar(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando item já está CONCLUIDO")
    void deveLancarExcecaoQuandoItemJaEstaConcluido() {
        // Arrange
        itemServico.atualizarStatus(StatusItemServico.EM_ANDAMENTO);
        itemServico.atualizarStatus(StatusItemServico.CONCLUIDO);

        CancelarServicoCommand command = CancelarServicoCommand.builder()
                .ordemServicoId(ordemServicoId)
                .itemServicoId(itemServicoId)
                .build();

        when(ordemServicoGateway.findById(ordemServicoId)).thenReturn(Optional.of(ordemServico));

        // Act & Assert
        assertThrows(ValidacaoEstoqueException.class, () -> useCase.execute(command));
        verify(ordemServicoGateway, never()).save(any());
        verify(ordemServicoResponseAssembler, never()).montar(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando item já está CANCELADO")
    void deveLancarExcecaoQuandoItemJaEstaCancelado() {
        // Arrange
        itemServico.atualizarStatus(StatusItemServico.CANCELADO);

        CancelarServicoCommand command = CancelarServicoCommand.builder()
                .ordemServicoId(ordemServicoId)
                .itemServicoId(itemServicoId)
                .build();

        when(ordemServicoGateway.findById(ordemServicoId)).thenReturn(Optional.of(ordemServico));

        // Act & Assert
        assertThrows(ValidacaoEstoqueException.class, () -> useCase.execute(command));
        verify(ordemServicoGateway, never()).save(any());
        verify(ordemServicoResponseAssembler, never()).montar(any());
    }
}
