package com.techchallenge.os.application.usecases;

import com.techchallenge.os.application.commands.RemoverServicoOrdemCommand;
import com.techchallenge.os.application.gateway.ports.output.OrdemServicoGateway;
import com.techchallenge.os.application.responses.OrdemServicoResponse;
import com.techchallenge.os.application.usecases.assemblers.OrdemServicoResponseAssembler;
import com.techchallenge.os.domain.exceptions.OrdemServicoNaoEncontradaException;
import com.techchallenge.os.domain.models.OrdemServico;
import com.techchallenge.os.domain.models.valueobjects.StatusOS;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de RemoverServicoOrdemUseCase - Application Layer")
class RemoverServicoOrdemUseCaseTest {

    @Mock
    private OrdemServicoGateway ordemServicoGateway;

    @Mock
    private OrdemServicoResponseAssembler ordemServicoResponseAssembler;

    @InjectMocks
    private RemoverServicoOrdemUseCase useCase;

    private OrdemServico ordemServico;
    private OrdemServicoResponse response;
    private UUID ordemServicoId;
    private UUID itemServicoId;

    @BeforeEach
    void setUp() {
        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();
        ordemServicoId = UUID.randomUUID();
        itemServicoId = UUID.randomUUID();
        LocalDateTime agora = LocalDateTime.now();

        ordemServico = OrdemServico.reconstruir(
                ordemServicoId,
                clienteId,
                veiculoId,
                StatusOS.RECEBIDA,
                agora,
                null,
                null,
                agora
        );

        response = OrdemServicoResponse.builder()
                .id(ordemServicoId)
                .status("RECEBIDA")
                .build();
    }

    @Test
    @DisplayName("Deve remover serviço da ordem com sucesso")
    void deveRemoverServicoDaOrdemComSucesso() {
        // Arrange
        RemoverServicoOrdemCommand command = new RemoverServicoOrdemCommand(ordemServicoId, itemServicoId);
        when(ordemServicoGateway.findById(ordemServicoId)).thenReturn(Optional.of(ordemServico));
        when(ordemServicoGateway.save(any(OrdemServico.class))).thenReturn(ordemServico);
        when(ordemServicoResponseAssembler.montar(any(OrdemServico.class))).thenReturn(response);

        // Act
        OrdemServicoResponse result = useCase.execute(command);

        // Assert
        assertNotNull(result);
        assertEquals(ordemServicoId, result.getId());
        verify(ordemServicoGateway).findById(ordemServicoId);
        verify(ordemServicoGateway).save(ordemServico);
        verify(ordemServicoResponseAssembler).montar(ordemServico);
    }

    @Test
    @DisplayName("Deve lançar exceção quando ordem de serviço não encontrada")
    void deveLancarExcecaoQuandoOrdemDeServicoNaoEncontrada() {
        // Arrange
        RemoverServicoOrdemCommand command = new RemoverServicoOrdemCommand(ordemServicoId, itemServicoId);
        when(ordemServicoGateway.findById(ordemServicoId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(OrdemServicoNaoEncontradaException.class, () -> useCase.execute(command));
        verify(ordemServicoGateway).findById(ordemServicoId);
        verify(ordemServicoGateway, never()).save(any());
        verify(ordemServicoResponseAssembler, never()).montar(any());
    }

    @Test
    @DisplayName("Deve chamar removerItemServico no aggregate")
    void deveChamarRemoverItemServicoNoAggregate() {
        // Arrange
        RemoverServicoOrdemCommand command = new RemoverServicoOrdemCommand(ordemServicoId, itemServicoId);
        when(ordemServicoGateway.findById(ordemServicoId)).thenReturn(Optional.of(ordemServico));
        when(ordemServicoGateway.save(any(OrdemServico.class))).thenReturn(ordemServico);
        when(ordemServicoResponseAssembler.montar(any(OrdemServico.class))).thenReturn(response);

        // Act
        useCase.execute(command);

        // Assert
        verify(ordemServicoGateway).save(ordemServico);
    }

    @Test
    @DisplayName("Deve montar response após salvar")
    void deveMontarResponseAposSalvar() {
        // Arrange
        RemoverServicoOrdemCommand command = new RemoverServicoOrdemCommand(ordemServicoId, itemServicoId);
        when(ordemServicoGateway.findById(ordemServicoId)).thenReturn(Optional.of(ordemServico));
        when(ordemServicoGateway.save(any(OrdemServico.class))).thenReturn(ordemServico);
        when(ordemServicoResponseAssembler.montar(any(OrdemServico.class))).thenReturn(response);

        // Act
        OrdemServicoResponse result = useCase.execute(command);

        // Assert
        verify(ordemServicoResponseAssembler).montar(ordemServico);
        assertEquals(response, result);
    }
}
