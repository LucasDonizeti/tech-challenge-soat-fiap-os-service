package com.techchallenge.os.application.usecases;

import com.techchallenge.os.application.commands.EntregarOrdemServicoCommand;
import com.techchallenge.os.application.gateway.ports.output.OrdemServicoGateway;
import com.techchallenge.os.application.responses.OrdemServicoResponse;
import com.techchallenge.os.application.usecases.assemblers.OrdemServicoResponseAssembler;
import com.techchallenge.os.domain.exceptions.OrdemServicoNaoEncontradaException;
import com.techchallenge.os.domain.models.ItemServico;
import com.techchallenge.os.domain.models.OrdemServico;
import com.techchallenge.os.domain.models.valueobjects.StatusItemServico;
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
@DisplayName("Testes de EntregarOrdemServicoUseCase - Application Layer")
class EntregarOrdemServicoUseCaseTest {

    @Mock
    private OrdemServicoGateway ordemServicoGateway;

    @Mock
    private OrdemServicoResponseAssembler ordemServicoResponseAssembler;

    @InjectMocks
    private EntregarOrdemServicoUseCase useCase;

    private UUID ordemServicoId;
    private EntregarOrdemServicoCommand command;
    private OrdemServico ordemServico;
    private OrdemServicoResponse response;

    @BeforeEach
    void setUp() {
        ordemServicoId = UUID.randomUUID();
        command = new EntregarOrdemServicoCommand(ordemServicoId);
        
        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();
        ordemServico = OrdemServico.criar(clienteId, veiculoId);
        
        UUID servicoId = UUID.randomUUID();
        ItemServico itemServico = ItemServico.criarComDados(servicoId, "Troca de Óleo", "Troca completa", new BigDecimal("150.00"));
        ordemServico.adicionarItemServico(itemServico);
        
        ordemServico.enviarParaDiagnostico();
        ordemServico.enviarOrcamentoAoCliente();
        ordemServico.aprovarOrcamento();
        
        itemServico.atualizarStatus(StatusItemServico.CONCLUIDO);
        ordemServico.finalizar();
        
        response = OrdemServicoResponse.builder()
                .id(ordemServicoId)
                .status(StatusOS.ENTREGUE.name())
                .build();
    }

    @Test
    @DisplayName("Deve entregar ordem de serviço com sucesso")
    void deveEntregarOrdemServicoComSucesso() {
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

        assertTrue(exception.getMessage().contains(ordemServicoId.toString()));
        verify(ordemServicoGateway, times(1)).findById(ordemServicoId);
        verify(ordemServicoGateway, never()).save(any());
        verify(ordemServicoResponseAssembler, never()).montar(any());
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
    @DisplayName("Deve propagar exceção quando domínio falha")
    void devePropagarExcecaoQuandoDominioFalha() {
        // Arrange
        UUID clienteId2 = UUID.randomUUID();
        UUID veiculoId2 = UUID.randomUUID();
        OrdemServico ordemServicoInvalida = OrdemServico.criar(clienteId2, veiculoId2);
        when(ordemServicoGateway.findById(ordemServicoId)).thenReturn(Optional.of(ordemServicoInvalida));
        when(ordemServicoGateway.save(any(OrdemServico.class))).thenReturn(ordemServicoInvalida);
        when(ordemServicoResponseAssembler.montar(any(OrdemServico.class))).thenReturn(response);

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> useCase.execute(command)
        );

        assertTrue(exception.getMessage().contains("Só é possível entregar"));
        verify(ordemServicoGateway, times(1)).findById(ordemServicoId);
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

    @Test
    @DisplayName("Deve chamar entregar antes de salvar")
    void deveChamarEntregarAntesDeSalvar() {
        // Arrange
        when(ordemServicoGateway.findById(ordemServicoId)).thenReturn(Optional.of(ordemServico));
        when(ordemServicoGateway.save(any(OrdemServico.class))).thenReturn(ordemServico);
        when(ordemServicoResponseAssembler.montar(any(OrdemServico.class))).thenReturn(response);

        // Act
        useCase.execute(command);

        // Assert
        verify(ordemServicoGateway, times(1)).findById(ordemServicoId);
        verify(ordemServicoGateway, times(1)).save(any(OrdemServico.class));
        verify(ordemServicoResponseAssembler, times(1)).montar(any(OrdemServico.class));
    }
}
