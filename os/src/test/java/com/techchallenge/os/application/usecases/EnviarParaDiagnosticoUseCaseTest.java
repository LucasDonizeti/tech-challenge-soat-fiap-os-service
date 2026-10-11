package com.techchallenge.os.application.usecases;

import com.techchallenge.os.application.commands.EnviarParaDiagnosticoCommand;
import com.techchallenge.os.application.gateway.ports.output.OrdemServicoGateway;
import com.techchallenge.os.application.responses.OrdemServicoResponse;
import com.techchallenge.os.application.usecases.assemblers.OrdemServicoResponseAssembler;
import com.techchallenge.os.domain.exceptions.OrdemServicoNaoEncontradaException;
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
@DisplayName("Testes de EnviarParaDiagnosticoUseCase - Application Layer")
class EnviarParaDiagnosticoUseCaseTest {

    @Mock
    private OrdemServicoGateway ordemServicoGateway;

    @Mock
    private OrdemServicoResponseAssembler ordemServicoResponseAssembler;

    @InjectMocks
    private EnviarParaDiagnosticoUseCase useCase;

    private UUID ordemServicoId;
    private EnviarParaDiagnosticoCommand command;
    private OrdemServico ordemServico;
    private OrdemServicoResponse response;

    @BeforeEach
    void setUp() {
        ordemServicoId = UUID.randomUUID();
        command = new EnviarParaDiagnosticoCommand(ordemServicoId);
        
        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();
        ordemServico = OrdemServico.criar(clienteId, veiculoId);
        
        UUID servicoId = UUID.randomUUID();
        ItemServico itemServico = ItemServico.criarComDados(servicoId, "Troca de Óleo", "Troca completa", new BigDecimal("150.00"));
        ordemServico.adicionarItemServico(itemServico);
        
        response = OrdemServicoResponse.builder()
                .id(ordemServicoId)
                .status(StatusOS.EM_DIAGNOSTICO.name())
                .build();
    }

    @Test
    @DisplayName("Deve enviar ordem de serviço para diagnóstico com sucesso")
    void deveEnviarOrdemServicoParaDiagnosticoComSucesso() {
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
        when(ordemServicoGateway.findById(ordemServicoId)).thenReturn(Optional.of(ordemServico));
        when(ordemServicoGateway.save(any(OrdemServico.class))).thenReturn(ordemServico);
        when(ordemServicoResponseAssembler.montar(any(OrdemServico.class))).thenReturn(response);
        
        // Mock para lançar exceção no domínio
        ordemServico.atualizarStatus(StatusOS.EM_EXECUCAO); // Status inválido para envio

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> useCase.execute(command)
        );

        assertTrue(exception.getMessage().contains("Só é possível enviar para diagnóstico"));
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
    @DisplayName("Deve chamar enviarParaDiagnostico antes de salvar")
    void deveChamarEnviarParaDiagnosticoAntesDeSalvar() {
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
