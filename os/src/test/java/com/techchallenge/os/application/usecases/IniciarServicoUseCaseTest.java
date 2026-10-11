package com.techchallenge.os.application.usecases;

import com.techchallenge.os.application.commands.IniciarServicoCommand;
import com.techchallenge.os.application.gateway.ports.output.MroGateway;
import com.techchallenge.os.application.gateway.ports.output.OrdemServicoGateway;
import com.techchallenge.os.application.gateway.ports.output.models.MROResumo;
import com.techchallenge.os.application.responses.OrdemServicoResponse;
import com.techchallenge.os.application.usecases.assemblers.OrdemServicoResponseAssembler;
import com.techchallenge.os.domain.exceptions.ItemServicoNaoEncontradoException;
import com.techchallenge.os.domain.exceptions.OrdemServicoNaoEncontradaException;
import com.techchallenge.os.domain.exceptions.ValidacaoEstoqueException;
import com.techchallenge.os.domain.models.ItemMRO;
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
@DisplayName("Testes do Use Case de Iniciar Serviço")
class IniciarServicoUseCaseTest {

    @Mock
    private OrdemServicoGateway ordemServicoGateway;

    @Mock
    private MroGateway mroGateway;

    @Mock
    private OrdemServicoResponseAssembler ordemServicoResponseAssembler;

    @InjectMocks
    private IniciarServicoUseCase useCase;

    private OrdemServico ordemServico;
    private ItemServico itemServico;
    private ItemMRO itemMRO;
    private UUID ordemServicoId;
    private UUID itemServicoId;
    private UUID mroId;
    private OrdemServicoResponse response;

    @BeforeEach
    void setUp() {
        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();
        ordemServico = OrdemServico.criar(clienteId, veiculoId);
        ordemServicoId = ordemServico.getId();

        itemServico = ItemServico.criarComDados(UUID.randomUUID(), "Troca de Óleo", "Troca completa", new BigDecimal("150.00"));
        ordemServico.adicionarItemServico(itemServico);
        itemServicoId = itemServico.getId();

        ItemServico itemServico2 = ItemServico.criarComDados(UUID.randomUUID(), "Troca de Filtro", "Troca de filtro de ar", new BigDecimal("50.00"));
        ordemServico.adicionarItemServico(itemServico2);

        mroId = UUID.randomUUID();
        itemMRO = ItemMRO.criarComDados(mroId, "Óleo 5W30", "Óleo sintético", new BigDecimal("50.00"), 2);
        itemServico.adicionarMRO(itemMRO);

        ordemServico.enviarParaDiagnostico();
        ordemServico.enviarOrcamentoAoCliente();
        ordemServico.aprovarOrcamento();

        response = OrdemServicoResponse.builder()
                .id(ordemServicoId)
                .status(StatusOS.EM_EXECUCAO.name())
                .build();
    }

    @Test
    @DisplayName("Deve iniciar serviço e debitar estoque")
    void deveIniciarServicoEDebitarEstoque() {
        IniciarServicoCommand command = IniciarServicoCommand.builder()
                .ordemServicoId(ordemServicoId)
                .itemServicoId(itemServicoId)
                .build();

        MROResumo estoqueAtualizado = MROResumo.builder()
                .id(mroId)
                .nome("Óleo 5W30")
                .quantidadeEstoque(10)
                .build();

        when(ordemServicoGateway.findById(ordemServicoId)).thenReturn(Optional.of(ordemServico));
        when(mroGateway.debitarEstoque(itemMRO.getId(), itemMRO.getQuantidade())).thenReturn(Optional.of(estoqueAtualizado));
        when(ordemServicoGateway.save(any(OrdemServico.class))).thenReturn(ordemServico);
        when(ordemServicoResponseAssembler.montar(any(OrdemServico.class))).thenReturn(response);

        OrdemServicoResponse result = useCase.execute(command);

        assertNotNull(result);
        assertEquals(StatusItemServico.EM_ANDAMENTO, itemServico.getStatus());
        assertNotNull(itemServico.getDataInicioExecucao());
        verify(mroGateway, times(1)).debitarEstoque(itemMRO.getId(), itemMRO.getQuantidade());
        verify(ordemServicoGateway, times(1)).save(ordemServico);
        verify(ordemServicoResponseAssembler, times(1)).montar(any(OrdemServico.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando OS não encontrada")
    void deveLancarExcecaoQuandoOSNaoEncontrada() {
        IniciarServicoCommand command = IniciarServicoCommand.builder()
                .ordemServicoId(ordemServicoId)
                .itemServicoId(itemServicoId)
                .build();

        when(ordemServicoGateway.findById(ordemServicoId)).thenReturn(Optional.empty());

        assertThrows(OrdemServicoNaoEncontradaException.class, () -> useCase.execute(command));
        verify(ordemServicoGateway, never()).save(any());
        verify(ordemServicoResponseAssembler, never()).montar(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando ItemServico não encontrado")
    void deveLancarExcecaoQuandoItemServicoNaoEncontrado() {
        UUID nonExistentItemId = UUID.randomUUID();

        IniciarServicoCommand command = IniciarServicoCommand.builder()
                .ordemServicoId(ordemServicoId)
                .itemServicoId(nonExistentItemId)
                .build();

        when(ordemServicoGateway.findById(ordemServicoId)).thenReturn(Optional.of(ordemServico));

        assertThrows(ItemServicoNaoEncontradoException.class, () -> useCase.execute(command));
        verify(ordemServicoGateway, never()).save(any());
        verify(ordemServicoResponseAssembler, never()).montar(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando OS não está em EM_EXECUCAO")
    void deveLancarExcecaoQuandoOSNaoEstaEmExecucao() {
        OrdemServico osEmDiagnostico = OrdemServico.criar(UUID.randomUUID(), UUID.randomUUID());
        ItemServico novoItem = ItemServico.criarComDados(UUID.randomUUID(), "Troca de Óleo", "Troca completa", new BigDecimal("150.00"));
        osEmDiagnostico.adicionarItemServico(novoItem);
        osEmDiagnostico.enviarParaDiagnostico();

        IniciarServicoCommand command = IniciarServicoCommand.builder()
                .ordemServicoId(osEmDiagnostico.getId())
                .itemServicoId(novoItem.getId())
                .build();

        when(ordemServicoGateway.findById(osEmDiagnostico.getId())).thenReturn(Optional.of(osEmDiagnostico));

        assertThrows(ValidacaoEstoqueException.class, () -> useCase.execute(command));
        verify(ordemServicoGateway, never()).save(any());
        verify(ordemServicoResponseAssembler, never()).montar(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando item não está PENDENTE")
    void deveLancarExcecaoQuandoItemNaoEstaPendente() {
        itemServico.atualizarStatus(StatusItemServico.EM_ANDAMENTO);

        IniciarServicoCommand command = IniciarServicoCommand.builder()
                .ordemServicoId(ordemServicoId)
                .itemServicoId(itemServicoId)
                .build();

        when(ordemServicoGateway.findById(ordemServicoId)).thenReturn(Optional.of(ordemServico));

        assertThrows(ValidacaoEstoqueException.class, () -> useCase.execute(command));
        verify(ordemServicoGateway, never()).save(any());
        verify(ordemServicoResponseAssembler, never()).montar(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando debite de estoque falha")
    void deveLancarExcecaoQuandoDebitoEstoqueFalha() {
        IniciarServicoCommand command = IniciarServicoCommand.builder()
                .ordemServicoId(ordemServicoId)
                .itemServicoId(itemServicoId)
                .build();

        when(ordemServicoGateway.findById(ordemServicoId)).thenReturn(Optional.of(ordemServico));
        when(mroGateway.debitarEstoque(itemMRO.getId(), itemMRO.getQuantidade()))
                .thenThrow(new RuntimeException("Estoque insuficiente"));

        assertThrows(ValidacaoEstoqueException.class, () -> useCase.execute(command));
        verify(ordemServicoGateway, never()).save(any());
        verify(ordemServicoResponseAssembler, never()).montar(any());
    }
}
