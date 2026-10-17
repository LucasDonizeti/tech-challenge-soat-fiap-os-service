package com.techchallenge.os.application.usecases;

import com.techchallenge.os.application.commands.RecusarOrcamentoCommand;
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
@DisplayName("Testes de RecusarOrcamentoUseCase - Application Layer")
class RecusarOrcamentoUseCaseTest {

    @Mock
    private OrdemServicoGateway ordemServicoGateway;

    @Mock
    private OrdemServicoResponseAssembler ordemServicoResponseAssembler;

    @InjectMocks
    private RecusarOrcamentoUseCase useCase;

    private RecusarOrcamentoCommand command;
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
        ordemServico.enviarOrcamentoAoCliente();

        UUID ordemServicoId = ordemServico.getId();
        command = new RecusarOrcamentoCommand(ordemServicoId, "Preço muito alto");

        response = OrdemServicoResponse.builder()
                .id(ordemServicoId)
                .status(StatusOS.CANCELADA.name())
                .build();
    }

    @Test
    @DisplayName("Deve recusar orçamento com sucesso")
    void deveRecusarOrcamentoComSucesso() {
        // Arrange
        when(ordemServicoGateway.findById(command.getOrdemServicoId())).thenReturn(Optional.of(ordemServico));
        when(ordemServicoGateway.save(any(OrdemServico.class))).thenReturn(ordemServico);
        when(ordemServicoResponseAssembler.montar(any(OrdemServico.class))).thenReturn(response);

        // Act
        OrdemServicoResponse result = useCase.execute(command);

        // Assert
        assertNotNull(result);
        assertEquals(ordemServico.getId(), result.getId());
        assertEquals(StatusOS.CANCELADA.name(), result.getStatus());

        verify(ordemServicoGateway, times(1)).findById(command.getOrdemServicoId());
        verify(ordemServicoGateway, times(1)).save(any(OrdemServico.class));
        verify(ordemServicoResponseAssembler, times(1)).montar(any(OrdemServico.class));
    }

    @Test
    @DisplayName("Deve recusar orçamento com motivo padrão quando não informado")
    void deveRecusarOrcamentoComMotivoPadraoQuandoNaoInformado() {
        // Arrange
        RecusarOrcamentoCommand commandSemMotivo = new RecusarOrcamentoCommand(ordemServico.getId(), null);
        when(ordemServicoGateway.findById(commandSemMotivo.getOrdemServicoId())).thenReturn(Optional.of(ordemServico));
        when(ordemServicoGateway.save(any(OrdemServico.class))).thenReturn(ordemServico);
        when(ordemServicoResponseAssembler.montar(any(OrdemServico.class))).thenReturn(response);

        // Act
        OrdemServicoResponse result = useCase.execute(commandSemMotivo);

        // Assert
        assertNotNull(result);
        assertEquals("Motivo não informado", commandSemMotivo.getMotivo());

        verify(ordemServicoGateway, times(1)).findById(commandSemMotivo.getOrdemServicoId());
        verify(ordemServicoGateway, times(1)).save(any(OrdemServico.class));
    }

    @Test
    @DisplayName("Deve recusar orçamento com motivo vazio")
    void deveRecusarOrcamentoComMotivoVazio() {
        // Arrange
        RecusarOrcamentoCommand commandMotivoVazio = new RecusarOrcamentoCommand(ordemServico.getId(), "   ");
        when(ordemServicoGateway.findById(commandMotivoVazio.getOrdemServicoId())).thenReturn(Optional.of(ordemServico));
        when(ordemServicoGateway.save(any(OrdemServico.class))).thenReturn(ordemServico);
        when(ordemServicoResponseAssembler.montar(any(OrdemServico.class))).thenReturn(response);

        // Act
        OrdemServicoResponse result = useCase.execute(commandMotivoVazio);

        // Assert
        assertNotNull(result);
        assertEquals("Motivo não informado", commandMotivoVazio.getMotivo());

        verify(ordemServicoGateway, times(1)).findById(commandMotivoVazio.getOrdemServicoId());
        verify(ordemServicoGateway, times(1)).save(any(OrdemServico.class));
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
    @DisplayName("Deve lançar exceção quando ordem de serviço não está em AGUARDANDO_APROVACAO")
    void deveLancarExcecaoQuandoOrdemServicoNaoEstaAguardandoAprovacao() {
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

        assertTrue(exception.getMessage().contains("Só é possível recusar orçamento quando a OS está no status AGUARDANDO_APROVACAO"));

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

    @Test
    @DisplayName("Deve lançar exceção quando ID da ordem de serviço é nulo no comando")
    void deveLancarExcecaoQuandoIdDaOrdemServicoENuloNoComando() {
        // Act & Assert
        assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> new RecusarOrcamentoCommand(null, "Motivo")
        );

        verify(ordemServicoGateway, never()).findById(any());
        verify(ordemServicoGateway, never()).save(any());
        verify(ordemServicoResponseAssembler, never()).montar(any());
    }

    @Test
    @DisplayName("Deve manter motivo informado no comando")
    void deveManterMotivoInformadoNoComando() {
        // Arrange
        String motivoCustomizado = "Cliente não autorizou o serviço";
        RecusarOrcamentoCommand commandComMotivo = new RecusarOrcamentoCommand(ordemServico.getId(), motivoCustomizado);
        when(ordemServicoGateway.findById(commandComMotivo.getOrdemServicoId())).thenReturn(Optional.of(ordemServico));
        when(ordemServicoGateway.save(any(OrdemServico.class))).thenReturn(ordemServico);
        when(ordemServicoResponseAssembler.montar(any(OrdemServico.class))).thenReturn(response);

        // Act
        OrdemServicoResponse result = useCase.execute(commandComMotivo);

        // Assert
        assertNotNull(result);
        assertEquals(motivoCustomizado, commandComMotivo.getMotivo());

        verify(ordemServicoGateway, times(1)).findById(commandComMotivo.getOrdemServicoId());
        verify(ordemServicoGateway, times(1)).save(any(OrdemServico.class));
    }
}
