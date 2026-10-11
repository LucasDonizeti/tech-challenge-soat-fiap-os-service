package com.techchallenge.os.application.usecases;

import com.techchallenge.os.application.commands.AtualizarObservacoesServicoCommand;
import com.techchallenge.os.application.gateway.ports.output.OrdemServicoGateway;
import com.techchallenge.os.application.responses.OrdemServicoResponse;
import com.techchallenge.os.application.usecases.assemblers.OrdemServicoResponseAssembler;
import com.techchallenge.os.domain.exceptions.ItemServicoNaoEncontradoException;
import com.techchallenge.os.domain.exceptions.OrdemServicoNaoEncontradaException;
import com.techchallenge.os.domain.exceptions.OrdemServicoStatusInvalidoException;
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
@DisplayName("Testes de AtualizarObservacoesServicoUseCase - Application Layer")
class AtualizarObservacoesServicoUseCaseTest {

    @Mock
    private OrdemServicoGateway ordemServicoGateway;

    @Mock
    private OrdemServicoResponseAssembler ordemServicoResponseAssembler;

    @InjectMocks
    private AtualizarObservacoesServicoUseCase useCase;

    private UUID ordemServicoId;
    private UUID itemServicoId;
    private AtualizarObservacoesServicoCommand command;
    private OrdemServico ordemServico;
    private ItemServico itemServico;
    private OrdemServicoResponse response;

    @BeforeEach
    void setUp() {
        ordemServicoId = UUID.randomUUID();
        itemServicoId = UUID.randomUUID();
        command = new AtualizarObservacoesServicoCommand(ordemServicoId, itemServicoId, "Nova observação");

        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();
        ordemServico = OrdemServico.criar(clienteId, veiculoId);

        itemServico = ItemServico.criarComDados(itemServicoId, "Troca de Óleo", "Troca completa", new BigDecimal("150.00"));
        ordemServico.adicionarItemServico(itemServico);

        response = OrdemServicoResponse.builder()
                .id(ordemServicoId)
                .status(StatusOS.RECEBIDA.name())
                .build();
    }

    @Test
    @DisplayName("Deve atualizar observações com sucesso quando status é RECEBIDA")
    void deveAtualizarObservacoesComSucessoQuandoStatusERecebida() {
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
    @DisplayName("Deve atualizar observações com sucesso quando status é EM_DIAGNOSTICO")
    void deveAtualizarObservacoesComSucessoQuandoStatusEEmDiagnostico() {
        // Arrange
        ordemServico.enviarParaDiagnostico();
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

        assertTrue(exception.getMessage().contains(ordemServicoId.toString()));
        verify(ordemServicoGateway, times(1)).findById(ordemServicoId);
        verify(ordemServicoGateway, never()).save(any());
        verify(ordemServicoResponseAssembler, never()).montar(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando status é inválido")
    void deveLancarExcecaoQuandoStatusEInvalido() {
        // Arrange
        ordemServico.atualizarStatus(StatusOS.FINALIZADA);
        when(ordemServicoGateway.findById(ordemServicoId)).thenReturn(Optional.of(ordemServico));

        // Act & Assert
        OrdemServicoStatusInvalidoException exception = assertThrows(
                OrdemServicoStatusInvalidoException.class,
                () -> useCase.execute(command)
        );

        assertTrue(exception.getMessage().contains("Só é possível atualizar observações quando a Ordem de Serviço está nos status RECEBIDA ou EM_DIAGNOSTICO"));
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

        assertTrue(exception.getMessage().contains(itemServicoId.toString()));
        verify(ordemServicoGateway, times(1)).findById(ordemServicoId);
        verify(ordemServicoGateway, never()).save(any());
        verify(ordemServicoResponseAssembler, never()).montar(any());
    }

    @Test
    @DisplayName("Deve atualizar observações com valor nulo")
    void deveAtualizarObservacoesComValorNulo() {
        // Arrange
        AtualizarObservacoesServicoCommand commandNull = new AtualizarObservacoesServicoCommand(ordemServicoId, itemServicoId, null);
        when(ordemServicoGateway.findById(ordemServicoId)).thenReturn(Optional.of(ordemServico));
        when(ordemServicoGateway.save(any(OrdemServico.class))).thenReturn(ordemServico);
        when(ordemServicoResponseAssembler.montar(any(OrdemServico.class))).thenReturn(response);

        // Act
        OrdemServicoResponse result = useCase.execute(commandNull);

        // Assert
        assertNotNull(result);
        verify(ordemServicoGateway, times(1)).save(any(OrdemServico.class));
        verify(ordemServicoResponseAssembler, times(1)).montar(any(OrdemServico.class));
    }

    @Test
    @DisplayName("Deve atualizar observações com valor vazio")
    void deveAtualizarObservacoesComValorVazio() {
        // Arrange
        AtualizarObservacoesServicoCommand commandEmpty = new AtualizarObservacoesServicoCommand(ordemServicoId, itemServicoId, "");
        ordemServico.enviarParaDiagnostico();
        when(ordemServicoGateway.findById(ordemServicoId)).thenReturn(Optional.of(ordemServico));
        when(ordemServicoGateway.save(any(OrdemServico.class))).thenReturn(ordemServico);
        when(ordemServicoResponseAssembler.montar(any(OrdemServico.class))).thenReturn(response);

        // Act
        OrdemServicoResponse result = useCase.execute(commandEmpty);

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
