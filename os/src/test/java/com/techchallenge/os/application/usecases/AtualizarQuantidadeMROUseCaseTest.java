package com.techchallenge.os.application.usecases;

import com.techchallenge.os.application.commands.AtualizarQuantidadeMROCommand;
import com.techchallenge.os.application.gateway.ports.output.OrdemServicoGateway;
import com.techchallenge.os.application.responses.OrdemServicoResponse;
import com.techchallenge.os.application.usecases.assemblers.OrdemServicoResponseAssembler;
import com.techchallenge.os.domain.exceptions.ItemServicoNaoEncontradoException;
import com.techchallenge.os.domain.exceptions.OrdemServicoNaoEncontradaException;
import com.techchallenge.os.domain.exceptions.ValidacaoOrdemServicoException;
import com.techchallenge.os.domain.models.ItemMRO;
import com.techchallenge.os.domain.models.ItemServico;
import com.techchallenge.os.domain.models.OrdemServico;
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
@DisplayName("Testes de AtualizarQuantidadeMROUseCase - Application Layer")
class AtualizarQuantidadeMROUseCaseTest {

    @Mock
    private OrdemServicoGateway ordemServicoGateway;

    @Mock
    private OrdemServicoResponseAssembler ordemServicoResponseAssembler;

    @InjectMocks
    private AtualizarQuantidadeMROUseCase useCase;

    private UUID ordemServicoId;
    private UUID itemServicoId;
    private UUID itemMroId;
    private AtualizarQuantidadeMROCommand command;
    private OrdemServico ordemServico;
    private ItemServico itemServico;
    private ItemMRO itemMRO;
    private OrdemServicoResponse response;

    @BeforeEach
    void setUp() {
        ordemServicoId = UUID.randomUUID();
        itemServicoId = UUID.randomUUID();
        itemMroId = UUID.randomUUID();
        command = new AtualizarQuantidadeMROCommand(ordemServicoId, itemServicoId, itemMroId, 5);

        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();
        ordemServico = OrdemServico.criar(clienteId, veiculoId);

        itemServico = ItemServico.criarComDados(itemServicoId, "Troca de Óleo", "Troca completa", new BigDecimal("150.00"));
        ordemServico.adicionarItemServico(itemServico);

        itemServico.adicionarMRO(itemMroId, "Óleo Motor", "Óleo para motor", new BigDecimal("45.90"), 3);
        itemMRO = itemServico.getMrosServicos().get(0);

        response = OrdemServicoResponse.builder()
                .id(ordemServicoId)
                .build();
    }

    @Test
    @DisplayName("Deve atualizar quantidade de MRO com sucesso")
    void deveAtualizarQuantidadeMROComSucesso() {
        // Arrange
        when(ordemServicoGateway.findById(ordemServicoId)).thenReturn(Optional.of(ordemServico));
        when(ordemServicoGateway.save(any(OrdemServico.class))).thenReturn(ordemServico);
        when(ordemServicoResponseAssembler.montar(any(OrdemServico.class))).thenReturn(response);

        // Act
        OrdemServicoResponse result = useCase.execute(command);

        // Assert
        assertNotNull(result);
        verify(ordemServicoGateway, times(1)).findById(ordemServicoId);
        verify(ordemServicoGateway, times(1)).save(any(OrdemServico.class));
        verify(ordemServicoResponseAssembler, times(1)).montar(any(OrdemServico.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando ordem de serviço não encontrada")
    void deveLancarExcecaoQuandoOrdemServicoNaoEncontrada() {
        // Arrange
        when(ordemServicoGateway.findById(ordemServicoId)).thenReturn(Optional.empty());

        // Act & Assert
        OrdemServicoNaoEncontradaException exception = assertThrows(
                OrdemServicoNaoEncontradaException.class,
                () -> useCase.execute(command)
        );

        assertTrue(exception.getMessage().contains("Ordem de Serviço não encontrada"));
        verify(ordemServicoGateway, times(1)).findById(ordemServicoId);
        verify(ordemServicoGateway, never()).save(any());
        verify(ordemServicoResponseAssembler, never()).montar(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando item de serviço não encontrado")
    void deveLancarExcecaoQuandoItemServicoNaoEncontrado() {
        // Arrange
        UUID clienteId2 = UUID.randomUUID();
        UUID veiculoId2 = UUID.randomUUID();
        OrdemServico ordemServicoSemItem = OrdemServico.criar(clienteId2, veiculoId2);
        when(ordemServicoGateway.findById(ordemServicoId)).thenReturn(Optional.of(ordemServicoSemItem));

        // Act & Assert
        ItemServicoNaoEncontradoException exception = assertThrows(
                ItemServicoNaoEncontradoException.class,
                () -> useCase.execute(command)
        );

        assertTrue(exception.getMessage().contains("Item de Serviço não encontrado"));
        verify(ordemServicoGateway, times(1)).findById(ordemServicoId);
        verify(ordemServicoGateway, never()).save(any());
        verify(ordemServicoResponseAssembler, never()).montar(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando item de MRO não encontrado")
    void deveLancarExcecaoQuandoItemMRONaoEncontrado() {
        // Arrange
        UUID itemMroId2 = UUID.randomUUID();
        AtualizarQuantidadeMROCommand commandInvalid = new AtualizarQuantidadeMROCommand(ordemServicoId, itemServicoId, itemMroId2, 5);
        when(ordemServicoGateway.findById(ordemServicoId)).thenReturn(Optional.of(ordemServico));

        // Act & Assert
        ValidacaoOrdemServicoException exception = assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> useCase.execute(commandInvalid)
        );

        assertTrue(exception.getMessage().contains("Item de MRO não encontrado com ID"));
        verify(ordemServicoGateway, times(1)).findById(ordemServicoId);
        verify(ordemServicoGateway, never()).save(any());
        verify(ordemServicoResponseAssembler, never()).montar(any());
    }

    @Test
    @DisplayName("Deve atualizar quantidade com valor 1")
    void deveAtualizarQuantidadeComValor1() {
        // Arrange
        AtualizarQuantidadeMROCommand command1 = new AtualizarQuantidadeMROCommand(ordemServicoId, itemServicoId, itemMroId, 1);
        when(ordemServicoGateway.findById(ordemServicoId)).thenReturn(Optional.of(ordemServico));
        when(ordemServicoGateway.save(any(OrdemServico.class))).thenReturn(ordemServico);
        when(ordemServicoResponseAssembler.montar(any(OrdemServico.class))).thenReturn(response);

        // Act
        OrdemServicoResponse result = useCase.execute(command1);

        // Assert
        assertNotNull(result);
        verify(ordemServicoGateway, times(1)).save(any(OrdemServico.class));
        verify(ordemServicoResponseAssembler, times(1)).montar(any(OrdemServico.class));
    }

    @Test
    @DisplayName("Deve atualizar quantidade com valor grande")
    void deveAtualizarQuantidadeComValorGrande() {
        // Arrange
        AtualizarQuantidadeMROCommand commandLarge = new AtualizarQuantidadeMROCommand(ordemServicoId, itemServicoId, itemMroId, 100);
        when(ordemServicoGateway.findById(ordemServicoId)).thenReturn(Optional.of(ordemServico));
        when(ordemServicoGateway.save(any(OrdemServico.class))).thenReturn(ordemServico);
        when(ordemServicoResponseAssembler.montar(any(OrdemServico.class))).thenReturn(response);

        // Act
        OrdemServicoResponse result = useCase.execute(commandLarge);

        // Assert
        assertNotNull(result);
        verify(ordemServicoGateway, times(1)).save(any(OrdemServico.class));
        verify(ordemServicoResponseAssembler, times(1)).montar(any(OrdemServico.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando comando é nulo")
    void deveLancarExcecaoQuandoComandoENulo() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> useCase.execute(null));

        verify(ordemServicoGateway, never()).findById(any());
        verify(ordemServicoGateway, never()).save(any());
        verify(ordemServicoResponseAssembler, never()).montar(any());
    }

    @Test
    @DisplayName("Deve propagar exceção quando repository falha ao salvar")
    void devePropagarExcecaoQuandoRepositoryFalhaAoSalvar() {
        // Arrange
        when(ordemServicoGateway.findById(ordemServicoId)).thenReturn(Optional.of(ordemServico));
        when(ordemServicoGateway.save(any(OrdemServico.class)))
                .thenThrow(new RuntimeException("Erro ao salvar no banco"));

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> useCase.execute(command)
        );

        assertEquals("Erro ao salvar no banco", exception.getMessage());
        verify(ordemServicoGateway, times(1)).findById(ordemServicoId);
        verify(ordemServicoGateway, times(1)).save(any(OrdemServico.class));
        verify(ordemServicoResponseAssembler, never()).montar(any());
    }
}
