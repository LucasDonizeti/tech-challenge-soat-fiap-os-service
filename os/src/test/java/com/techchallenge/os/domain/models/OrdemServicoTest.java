package com.techchallenge.os.domain.models;

import com.techchallenge.os.domain.exceptions.ValidacaoOrdemServicoException;
import com.techchallenge.os.domain.models.valueobjects.StatusOS;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de OrdemServico - Domain Layer")
class OrdemServicoTest {

    private UUID cliente;
    private UUID veiculo;
    private OrdemServico ordemServico;
    private ItemServico itemServico;

    @BeforeEach
    void setUp() {
        cliente = UUID.randomUUID();
        veiculo = UUID.randomUUID();

        ordemServico = OrdemServico.criar(cliente, veiculo);

        UUID servicoId = UUID.randomUUID();
        itemServico = ItemServico.criarComDados(servicoId, "Troca de Óleo", "Troca completa de óleo", new BigDecimal("150.00"));
        ordemServico.adicionarItemServico(itemServico);
    }

    @Test
    @DisplayName("Deve enviar orçamento ao cliente com sucesso")
    void deveEnviarOrcamentoAoClienteComSucesso() {
        // Arrange
        ordemServico.enviarParaDiagnostico();
        assertEquals(StatusOS.EM_DIAGNOSTICO, ordemServico.getStatus());

        // Act
        ordemServico.enviarOrcamentoAoCliente();

        // Assert
        assertEquals(StatusOS.AGUARDANDO_APROVACAO, ordemServico.getStatus());
    }

    @Test
    @DisplayName("Deve lançar exceção ao enviar orçamento quando status não é EM_DIAGNOSTICO")
    void deveLancarExcecaoAoEnviarOrcamentoQuandoStatusNaoEMDiagnostico() {
        // Arrange
        assertEquals(StatusOS.RECEBIDA, ordemServico.getStatus());

        // Act & Assert
        ValidacaoOrdemServicoException exception = assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> ordemServico.enviarOrcamentoAoCliente()
        );

        assertTrue(exception.getMessage().contains("Só é possível enviar orçamento ao cliente quando a OS está no status EM_DIAGNOSTICO"));
        assertEquals(StatusOS.RECEBIDA, ordemServico.getStatus());
    }

    @Test
    @DisplayName("Deve lançar exceção ao enviar orçamento quando não há serviços")
    void deveLancarExcecaoAoEnviarOrcamentoQuandoNaoHaServicos() {
        // Arrange
        UUID cliente2 = UUID.randomUUID();
        UUID veiculo2 = UUID.randomUUID();
        OrdemServico ordemServicoSemServicos = OrdemServico.criar(cliente2, veiculo2);
        // Simula que a OS já está em EM_DIAGNOSTICO mas sem serviços (caso edge)
        ordemServicoSemServicos.atualizarStatus(StatusOS.EM_DIAGNOSTICO);

        // Act & Assert
        ValidacaoOrdemServicoException exception = assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> ordemServicoSemServicos.enviarOrcamentoAoCliente()
        );

        assertTrue(exception.getMessage().contains("OS deve ter pelo menos um serviço para enviar orçamento ao cliente"));
    }

    @Test
    @DisplayName("Deve aprovar orçamento com sucesso")
    void deveAprovarOrcamentoComSucesso() {
        // Arrange
        ordemServico.enviarParaDiagnostico();
        ordemServico.enviarOrcamentoAoCliente();
        assertEquals(StatusOS.AGUARDANDO_APROVACAO, ordemServico.getStatus());

        // Act
        ordemServico.aprovarOrcamento();

        // Assert
        assertEquals(StatusOS.EM_EXECUCAO, ordemServico.getStatus());
    }

    @Test
    @DisplayName("Deve lançar exceção ao aprovar orçamento quando status não é AGUARDANDO_APROVACAO")
    void deveLancarExcecaoAoAprovarOrcamentoQuandoStatusNaoAguardandoAprovacao() {
        // Arrange
        assertEquals(StatusOS.RECEBIDA, ordemServico.getStatus());

        // Act & Assert
        ValidacaoOrdemServicoException exception = assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> ordemServico.aprovarOrcamento()
        );

        assertTrue(exception.getMessage().contains("Só é possível aprovar orçamento quando a OS está no status AGUARDANDO_APROVACAO"));
        assertEquals(StatusOS.RECEBIDA, ordemServico.getStatus());
    }

    @Test
    @DisplayName("Deve manter fluxo completo de status corretamente")
    void deveManterFluxoCompletoDeStatusCorretamente() {
        // Arrange - Status inicial
        assertEquals(StatusOS.RECEBIDA, ordemServico.getStatus());

        // Act & Assert - Enviar para diagnóstico
        ordemServico.enviarParaDiagnostico();
        assertEquals(StatusOS.EM_DIAGNOSTICO, ordemServico.getStatus());

        // Act & Assert - Enviar orçamento ao cliente
        ordemServico.enviarOrcamentoAoCliente();
        assertEquals(StatusOS.AGUARDANDO_APROVACAO, ordemServico.getStatus());

        // Act & Assert - Aprovar orçamento
        ordemServico.aprovarOrcamento();
        assertEquals(StatusOS.EM_EXECUCAO, ordemServico.getStatus());
    }
}
