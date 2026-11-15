package com.techchallenge.os.application.usecases;

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
@DisplayName("Testes de BuscarOrdemServicoUseCase - Application Layer")
class BuscarOrdemServicoUseCaseTest {

    @Mock
    private OrdemServicoGateway ordemServicoGateway;

    @Mock
    private OrdemServicoResponseAssembler ordemServicoResponseAssembler;

    @InjectMocks
    private BuscarOrdemServicoUseCase useCase;

    private UUID ordemServicoId;
    private OrdemServico ordemServico;
    private OrdemServicoResponse response;

    @BeforeEach
    void setUp() {
        ordemServicoId = UUID.randomUUID();
        
        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();
        ordemServico = OrdemServico.criar(clienteId, veiculoId);
        
        UUID servicoId = UUID.randomUUID();
        ItemServico itemServico = ItemServico.criarComDados(servicoId, "Troca de Óleo", "Troca completa", new BigDecimal("150.00"));
        ordemServico.adicionarItemServico(itemServico);
        
        response = OrdemServicoResponse.builder()
                .id(ordemServicoId)
                .status(StatusOS.RECEBIDA.name())
                .build();
    }

    @Test
    @DisplayName("Deve buscar ordem de serviço com sucesso")
    void deveBuscarOrdemServicoComSucesso() {
        // Arrange
        when(ordemServicoGateway.findById(ordemServicoId)).thenReturn(Optional.of(ordemServico));
        when(ordemServicoResponseAssembler.montar(any(OrdemServico.class))).thenReturn(response);

        // Act
        OrdemServicoResponse result = useCase.execute(ordemServicoId);

        // Assert
        assertNotNull(result);
        verify(ordemServicoGateway, times(1)).findById(ordemServicoId);
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
                () -> useCase.execute(ordemServicoId)
        );

        assertTrue(exception.getMessage().contains(ordemServicoId.toString()));
        verify(ordemServicoGateway, times(1)).findById(ordemServicoId);
        verify(ordemServicoResponseAssembler, never()).montar(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando ID é nulo")
    void deveLancarExcecaoQuandoIdENulo() {
        // Act & Assert
        assertThrows(OrdemServicoNaoEncontradaException.class, () -> useCase.execute(null));

        verify(ordemServicoGateway, times(1)).findById(null);
        verify(ordemServicoResponseAssembler, never()).montar(any());
    }

    @Test
    @DisplayName("Deve buscar ordem de serviço com UUID válido")
    void deveBuscarOrdemServicoComUUIDValido() {
        // Arrange
        UUID validId = UUID.randomUUID();
        when(ordemServicoGateway.findById(validId)).thenReturn(Optional.of(ordemServico));
        when(ordemServicoResponseAssembler.montar(any(OrdemServico.class))).thenReturn(response);

        // Act
        OrdemServicoResponse result = useCase.execute(validId);

        // Assert
        assertNotNull(result);
        verify(ordemServicoGateway, times(1)).findById(validId);
        verify(ordemServicoResponseAssembler, times(1)).montar(any(OrdemServico.class));
    }

    @Test
    @DisplayName("Deve chamar repository apenas uma vez")
    void deveChamarRepositoryApenasUmaVez() {
        // Arrange
        when(ordemServicoGateway.findById(ordemServicoId)).thenReturn(Optional.of(ordemServico));
        when(ordemServicoResponseAssembler.montar(any(OrdemServico.class))).thenReturn(response);

        // Act
        useCase.execute(ordemServicoId);

        // Assert
        verify(ordemServicoGateway, times(1)).findById(ordemServicoId);
        verify(ordemServicoResponseAssembler, times(1)).montar(any(OrdemServico.class));
    }

    @Test
    @DisplayName("Deve propagar exceção quando repository falha")
    void devePropagarExcecaoQuandoRepositoryFalha() {
        // Arrange
        when(ordemServicoGateway.findById(ordemServicoId))
                .thenThrow(new RuntimeException("Erro ao buscar no banco"));

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> useCase.execute(ordemServicoId)
        );

        assertEquals("Erro ao buscar no banco", exception.getMessage());
        verify(ordemServicoGateway, times(1)).findById(ordemServicoId);
        verify(ordemServicoResponseAssembler, never()).montar(any());
    }
}
