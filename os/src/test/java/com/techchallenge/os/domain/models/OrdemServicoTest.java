package com.techchallenge.os.domain.models;

import com.techchallenge.os.domain.exceptions.ValidacaoOrdemServicoException;
import com.techchallenge.os.domain.models.valueobjects.StatusItemServico;
import com.techchallenge.os.domain.models.valueobjects.StatusOS;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de OrdemServico - Domain Layer")
class OrdemServicoTest {

    private OrdemServico ordemServico;
    private UUID clienteId;
    private UUID veiculoId;

    @BeforeEach
    void setUp() {
        clienteId = UUID.randomUUID();
        veiculoId = UUID.randomUUID();
        ordemServico = OrdemServico.criar(clienteId, veiculoId);
    }

    @Test
    @DisplayName("Deve criar OrdemServico com dados válidos")
    void deveCriarOrdemServicoComDadosValidos() {
        // Assert
        assertNotNull(ordemServico);
        assertNotNull(ordemServico.getId());
        assertEquals(clienteId, ordemServico.getCliente());
        assertEquals(veiculoId, ordemServico.getVeiculo());
        assertEquals(StatusOS.RECEBIDA, ordemServico.getStatus());
        assertNotNull(ordemServico.getDataCriacao());
        assertNotNull(ordemServico.getDataUltimaMudancaStatus());
    }

    @Test
    @DisplayName("Deve lançar exceção quando cliente é nulo")
    void deveLancarExcecaoQuandoClienteENulo() {
        // Act & Assert
        ValidacaoOrdemServicoException exception = assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> OrdemServico.criar(null, veiculoId)
        );

        assertEquals("Cliente não pode ser nulo", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando veículo é nulo")
    void deveLancarExcecaoQuandoVeiculoENulo() {
        // Act & Assert
        ValidacaoOrdemServicoException exception = assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> OrdemServico.criar(clienteId, null)
        );

        assertEquals("Veículo não pode ser nulo", exception.getMessage());
    }

    @Test
    @DisplayName("Deve reconstruir OrdemServico a partir de dados persistidos")
    void deveReconstruirOrdemServicoAPartirDeDadosPersistidos() {
        // Arrange
        UUID id = UUID.randomUUID();
        UUID cliente = UUID.randomUUID();
        UUID veiculo = UUID.randomUUID();
        StatusOS status = StatusOS.EM_EXECUCAO;
        LocalDateTime dataCriacao = LocalDateTime.now();
        LocalDateTime dataInicio = LocalDateTime.now().plusHours(1);
        LocalDateTime dataFinalizacao = LocalDateTime.now().plusHours(5);
        LocalDateTime dataUltimaMudanca = LocalDateTime.now();

        // Act
        OrdemServico os = OrdemServico.reconstruir(id, cliente, veiculo, status, dataCriacao,
                dataInicio, dataFinalizacao, dataUltimaMudanca);

        // Assert
        assertNotNull(os);
        assertEquals(id, os.getId());
        assertEquals(cliente, os.getCliente());
        assertEquals(veiculo, os.getVeiculo());
        assertEquals(status, os.getStatus());
        assertEquals(dataCriacao, os.getDataCriacao());
        assertEquals(dataInicio, os.getDataInicioExecucao());
        assertEquals(dataFinalizacao, os.getDataFinalizacao());
        assertEquals(dataUltimaMudanca, os.getDataUltimaMudancaStatus());
    }

    @Test
    @DisplayName("Deve adicionar item de serviço")
    void deveAdicionarItemDeServico() {
        // Arrange
        ItemServico itemServico = ItemServico.criarComDados(UUID.randomUUID(), "Troca de Óleo", "Descrição", new BigDecimal("150.00"));

        // Act
        ItemServico result = ordemServico.adicionarItemServico(itemServico);

        // Assert
        assertEquals(1, ordemServico.getItensServico().size());
        assertEquals(itemServico, result);
        assertEquals(ordemServico, itemServico.getOrdemServico());
    }

    @Test
    @DisplayName("Deve lançar exceção ao adicionar item de serviço nulo")
    void deveLancarExcecaoAoAdicionarItemDeServicoNulo() {
        // Act & Assert
        ValidacaoOrdemServicoException exception = assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> ordemServico.adicionarItemServico(null)
        );

        assertEquals("Item de serviço não pode ser nulo", exception.getMessage());
    }

    @Test
    @DisplayName("Deve adicionar item de serviço sem validação")
    void deveAdicionarItemDeServicoSemValidacao() {
        // Arrange
        ItemServico itemServico = ItemServico.criarComDados(UUID.randomUUID(), "Troca de Óleo", "Descrição", new BigDecimal("150.00"));

        // Act
        ordemServico.adicionarItemServicoSemValidacao(itemServico);

        // Assert
        assertEquals(1, ordemServico.getItensServico().size());
    }

    @Test
    @DisplayName("Deve adicionar item de serviço nulo sem validação sem erro")
    void deveAdicionarItemDeServicoNuloSemValidacaoSemErro() {
        // Act
        ordemServico.adicionarItemServicoSemValidacao(null);

        // Assert
        assertEquals(0, ordemServico.getItensServico().size());
    }

    @Test
    @DisplayName("Deve remover item de serviço")
    void deveRemoverItemDeServico() {
        // Arrange
        ItemServico itemServico = ItemServico.criarComDados(UUID.randomUUID(), "Troca de Óleo", "Descrição", new BigDecimal("150.00"));
        ordemServico.adicionarItemServico(itemServico);

        // Act
        ordemServico.removerItemServico(itemServico.getId());

        // Assert
        assertEquals(0, ordemServico.getItensServico().size());
    }

    @Test
    @DisplayName("Deve atualizar cliente")
    void deveAtualizarCliente() {
        // Arrange
        UUID novoClienteId = UUID.randomUUID();

        // Act
        ordemServico.atualizarCliente(novoClienteId);

        // Assert
        assertEquals(novoClienteId, ordemServico.getCliente());
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar cliente para nulo")
    void deveLancarExcecaoAoAtualizarClienteParaNulo() {
        // Act & Assert
        ValidacaoOrdemServicoException exception = assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> ordemServico.atualizarCliente(null)
        );

        assertEquals("Cliente não pode ser nulo", exception.getMessage());
    }

    @Test
    @DisplayName("Deve atualizar veículo")
    void deveAtualizarVeiculo() {
        // Arrange
        UUID novoVeiculoId = UUID.randomUUID();

        // Act
        ordemServico.atualizarVeiculo(novoVeiculoId);

        // Assert
        assertEquals(novoVeiculoId, ordemServico.getVeiculo());
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar veículo para nulo")
    void deveLancarExcecaoAoAtualizarVeiculoParaNulo() {
        // Act & Assert
        ValidacaoOrdemServicoException exception = assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> ordemServico.atualizarVeiculo(null)
        );

        assertEquals("Veículo não pode ser nulo", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao alterar cliente em status EM_DIAGNOSTICO")
    void deveLancarExcecaoAoAlterarClienteEmStatusEmDiagnostico() {
        // Arrange
        ItemServico item = ItemServico.criarComDados(UUID.randomUUID(), "Serviço", "Descrição", new BigDecimal("100.00"));
        ordemServico.adicionarItemServico(item);
        ordemServico.enviarParaDiagnostico();

        // Act & Assert
        ValidacaoOrdemServicoException exception = assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> ordemServico.atualizarCliente(UUID.randomUUID())
        );

        assertTrue(exception.getMessage().contains("Não é permitido alterar cliente ou veículo"));
    }

    @Test
    @DisplayName("Deve lançar exceção ao alterar veículo em status EM_EXECUCAO")
    void deveLancarExcecaoAoAlterarVeiculoEmStatusEmExecucao() {
        // Arrange
        ItemServico item = ItemServico.criarComDados(UUID.randomUUID(), "Serviço", "Descrição", new BigDecimal("100.00"));
        ordemServico.adicionarItemServico(item);
        ordemServico.enviarParaDiagnostico();
        ordemServico.enviarOrcamentoAoCliente();
        ordemServico.aprovarOrcamento();

        // Act & Assert
        ValidacaoOrdemServicoException exception = assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> ordemServico.atualizarVeiculo(UUID.randomUUID())
        );

        assertTrue(exception.getMessage().contains("Não é permitido alterar cliente ou veículo"));
    }

    @Test
    @DisplayName("Deve lançar exceção ao adicionar serviço em status diferente de RECEBIDA")
    void deveLancarExcecaoAoAdicionarServicoEmStatusDiferenteDeRecebida() {
        // Arrange
        ItemServico item = ItemServico.criarComDados(UUID.randomUUID(), "Serviço", "Descrição", new BigDecimal("100.00"));
        ordemServico.adicionarItemServico(item);
        ordemServico.enviarParaDiagnostico();

        // Act & Assert
        ValidacaoOrdemServicoException exception = assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> ordemServico.adicionarItemServico(item)
        );

        assertTrue(exception.getMessage().contains("Não é permitido adicionar ou remover serviços"));
    }

    @Test
    @DisplayName("Deve atualizar status")
    void deveAtualizarStatus() {
        // Act
        ordemServico.atualizarStatus(StatusOS.EM_DIAGNOSTICO);

        // Assert
        assertEquals(StatusOS.EM_DIAGNOSTICO, ordemServico.getStatus());
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar status para nulo")
    void deveLancarExcecaoAoAtualizarStatusParaNulo() {
        // Act & Assert
        ValidacaoOrdemServicoException exception = assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> ordemServico.atualizarStatus(null)
        );

        assertEquals("Status não pode ser nulo", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao mudar status não permitido em AGUARDANDO_APROVACAO")
    void deveLancarExcecaoAoMudarStatusNaoPermitidoEmAguardandoAprovacao() {
        // Arrange
        ItemServico item = ItemServico.criarComDados(UUID.randomUUID(), "Serviço", "Descrição", new BigDecimal("100.00"));
        ordemServico.adicionarItemServico(item);
        ordemServico.enviarParaDiagnostico();
        ordemServico.enviarOrcamentoAoCliente();

        // Act & Assert
        ValidacaoOrdemServicoException exception = assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> ordemServico.atualizarStatus(StatusOS.RECEBIDA)
        );

        assertTrue(exception.getMessage().contains("apenas a aprovação do orçamento é permitida"));
    }

    @Test
    @DisplayName("Deve enviar para diagnóstico")
    void deveEnviarParaDiagnostico() {
        // Arrange
        ItemServico item = ItemServico.criarComDados(UUID.randomUUID(), "Serviço", "Descrição", new BigDecimal("100.00"));
        ordemServico.adicionarItemServico(item);

        // Act
        ordemServico.enviarParaDiagnostico();

        // Assert
        assertEquals(StatusOS.EM_DIAGNOSTICO, ordemServico.getStatus());
    }

    @Test
    @DisplayName("Deve lançar exceção ao enviar para diagnóstico sem serviços")
    void deveLancarExcecaoAoEnviarParaDiagnosticoSemServicos() {
        // Act & Assert
        ValidacaoOrdemServicoException exception = assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> ordemServico.enviarParaDiagnostico()
        );

        assertEquals("OS deve ter pelo menos um serviço para ser enviada para diagnóstico", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao enviar para diagnóstico em status diferente de RECEBIDA")
    void deveLancarExcecaoAoEnviarParaDiagnosticoEmStatusDiferenteDeRecebida() {
        // Arrange
        ordemServico.atualizarStatus(StatusOS.EM_DIAGNOSTICO);

        // Act & Assert
        ValidacaoOrdemServicoException exception = assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> ordemServico.enviarParaDiagnostico()
        );

        assertTrue(exception.getMessage().contains("Só é possível enviar para diagnóstico quando a OS está no status RECEBIDA"));
    }

    @Test
    @DisplayName("Deve enviar orçamento ao cliente")
    void deveEnviarOrcamentoAoCliente() {
        // Arrange
        ItemServico item = ItemServico.criarComDados(UUID.randomUUID(), "Serviço", "Descrição", new BigDecimal("100.00"));
        ordemServico.adicionarItemServico(item);
        ordemServico.enviarParaDiagnostico();

        // Act
        ordemServico.enviarOrcamentoAoCliente();

        // Assert
        assertEquals(StatusOS.AGUARDANDO_APROVACAO, ordemServico.getStatus());
    }

    @Test
    @DisplayName("Deve lançar exceção ao enviar orçamento sem serviços")
    void deveLancarExcecaoAoEnviarOrcamentoSemServicos() {
        // Arrange
        ordemServico.atualizarStatus(StatusOS.EM_DIAGNOSTICO);

        // Act & Assert
        ValidacaoOrdemServicoException exception = assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> ordemServico.enviarOrcamentoAoCliente()
        );

        assertEquals("OS deve ter pelo menos um serviço para enviar orçamento ao cliente", exception.getMessage());
    }

    @Test
    @DisplayName("Deve aprovar orçamento")
    void deveAprovarOrcamento() {
        // Arrange
        ItemServico item = ItemServico.criarComDados(UUID.randomUUID(), "Serviço", "Descrição", new BigDecimal("100.00"));
        ordemServico.adicionarItemServico(item);
        ordemServico.enviarParaDiagnostico();
        ordemServico.enviarOrcamentoAoCliente();

        // Act
        ordemServico.aprovarOrcamento();

        // Assert
        assertEquals(StatusOS.EM_EXECUCAO, ordemServico.getStatus());
        assertNotNull(ordemServico.getDataInicioExecucao());
    }

    @Test
    @DisplayName("Deve lançar exceção ao aprovar orçamento em status diferente de AGUARDANDO_APROVACAO")
    void deveLancarExcecaoAoAprovarOrcamentoEmStatusDiferenteDeAguardandoAprovacao() {
        // Act & Assert
        ValidacaoOrdemServicoException exception = assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> ordemServico.aprovarOrcamento()
        );

        assertTrue(exception.getMessage().contains("Só é possível aprovar orçamento quando a OS está no status AGUARDANDO_APROVACAO"));
    }

    @Test
    @DisplayName("Deve recusar orçamento")
    void deveRecusarOrcamento() {
        // Arrange
        ItemServico item = ItemServico.criarComDados(UUID.randomUUID(), "Serviço", "Descrição", new BigDecimal("100.00"));
        ordemServico.adicionarItemServico(item);
        ordemServico.enviarParaDiagnostico();
        ordemServico.enviarOrcamentoAoCliente();

        // Act
        ordemServico.recusarOrcamento("Preço muito alto");

        // Assert
        assertEquals(StatusOS.CANCELADA, ordemServico.getStatus());
        assertNotNull(ordemServico.getDataFinalizacao());
    }

    @Test
    @DisplayName("Deve lançar exceção ao recusar orçamento em status diferente de AGUARDANDO_APROVACAO")
    void deveLancarExcecaoAoRecusarOrcamentoEmStatusDiferenteDeAguardandoAprovacao() {
        // Act & Assert
        ValidacaoOrdemServicoException exception = assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> ordemServico.recusarOrcamento("Motivo")
        );

        assertTrue(exception.getMessage().contains("Só é possível recusar orçamento quando a OS está no status AGUARDANDO_APROVACAO"));
    }

    @Test
    @DisplayName("Deve finalizar ordem de serviço")
    void deveFinalizarOrdemDeServico() {
        // Arrange
        ItemServico item = ItemServico.criarComDados(UUID.randomUUID(), "Serviço", "Descrição", new BigDecimal("100.00"));
        ordemServico.adicionarItemServico(item);
        ordemServico.enviarParaDiagnostico();
        ordemServico.enviarOrcamentoAoCliente();
        ordemServico.aprovarOrcamento();
        item.atualizarStatus(StatusItemServico.CONCLUIDO);

        // Act - a finalização é automática quando o item é concluído
        // Assert
        assertEquals(StatusOS.FINALIZADA, ordemServico.getStatus());
        assertNotNull(ordemServico.getDataFinalizacao());
    }

    @Test
    @DisplayName("Deve lançar exceção ao finalizar quando não todos os serviços estão concluídos")
    void deveLancarExcecaoAoFinalizarQuandoNaoTodosOsServicosEstaoConcluidos() {
        // Arrange
        ItemServico item = ItemServico.criarComDados(UUID.randomUUID(), "Serviço", "Descrição", new BigDecimal("100.00"));
        ordemServico.adicionarItemServico(item);
        ordemServico.enviarParaDiagnostico();
        ordemServico.enviarOrcamentoAoCliente();
        ordemServico.aprovarOrcamento();

        // Act & Assert
        ValidacaoOrdemServicoException exception = assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> ordemServico.finalizar()
        );

        assertEquals("OS só pode ser finalizada quando todos os serviços estiverem CONCLUIDO", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao finalizar em status diferente de EM_EXECUCAO")
    void deveLancarExcecaoAoFinalizarEmStatusDiferenteDeEmExecucao() {
        // Act & Assert
        ValidacaoOrdemServicoException exception = assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> ordemServico.finalizar()
        );

        assertTrue(exception.getMessage().contains("Só é possível finalizar quando a OS está no status EM_EXECUCAO"));
    }

    @Test
    @DisplayName("Deve verificar e finalizar automaticamente quando todos serviços concluídos")
    void deveVerificarEFinalizarAutomaticamenteQuandoTodosServicosConcluidos() {
        // Arrange
        ItemServico item = ItemServico.criarComDados(UUID.randomUUID(), "Serviço", "Descrição", new BigDecimal("100.00"));
        ordemServico.adicionarItemServico(item);
        ordemServico.enviarParaDiagnostico();
        ordemServico.enviarOrcamentoAoCliente();
        ordemServico.aprovarOrcamento();
        item.atualizarStatus(StatusItemServico.CONCLUIDO);

        // Act
        ordemServico.verificarEFinalizarAutomaticamente();

        // Assert
        assertEquals(StatusOS.FINALIZADA, ordemServico.getStatus());
    }

    @Test
    @DisplayName("Não deve finalizar automaticamente quando nem todos serviços concluídos")
    void naoDeveFinalizarAutomaticamenteQuandoNemTodosServicosConcluidos() {
        // Arrange
        ItemServico item = ItemServico.criarComDados(UUID.randomUUID(), "Serviço", "Descrição", new BigDecimal("100.00"));
        ordemServico.adicionarItemServico(item);
        ordemServico.enviarParaDiagnostico();
        ordemServico.enviarOrcamentoAoCliente();
        ordemServico.aprovarOrcamento();

        // Act
        ordemServico.verificarEFinalizarAutomaticamente();

        // Assert
        assertEquals(StatusOS.EM_EXECUCAO, ordemServico.getStatus());
    }

    @Test
    @DisplayName("Deve entregar ordem de serviço")
    void deveEntregarOrdemDeServico() {
        // Arrange
        ItemServico item = ItemServico.criarComDados(UUID.randomUUID(), "Serviço", "Descrição", new BigDecimal("100.00"));
        ordemServico.adicionarItemServico(item);
        ordemServico.enviarParaDiagnostico();
        ordemServico.enviarOrcamentoAoCliente();
        ordemServico.aprovarOrcamento();
        item.atualizarStatus(StatusItemServico.CONCLUIDO); // Isso finaliza automaticamente

        // Act
        ordemServico.entregar();

        // Assert
        assertEquals(StatusOS.ENTREGUE, ordemServico.getStatus());
    }

    @Test
    @DisplayName("Deve lançar exceção ao entregar em status diferente de FINALIZADA")
    void deveLancarExcecaoAoEntregarEmStatusDiferenteDeFinalizada() {
        // Act & Assert
        ValidacaoOrdemServicoException exception = assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> ordemServico.entregar()
        );

        assertTrue(exception.getMessage().contains("Só é possível entregar quando a OS está no status FINALIZADA"));
    }

    @Test
    @DisplayName("Deve calcular valor total")
    void deveCalcularValorTotal() {
        // Arrange
        ItemServico item1 = ItemServico.criarComDados(UUID.randomUUID(), "Serviço 1", "Descrição", new BigDecimal("100.00"));
        ItemServico item2 = ItemServico.criarComDados(UUID.randomUUID(), "Serviço 2", "Descrição", new BigDecimal("50.00"));
        ordemServico.adicionarItemServico(item1);
        ordemServico.adicionarItemServico(item2);

        // Act
        BigDecimal valorTotal = ordemServico.calcularValorTotal();

        // Assert
        assertEquals(new BigDecimal("150.00"), valorTotal);
    }

    @Test
    @DisplayName("Deve verificar se possui itens de serviço")
    void deveVerificarSePossuiItensDeServico() {
        // Arrange
        ItemServico item = ItemServico.criarComDados(UUID.randomUUID(), "Serviço", "Descrição", new BigDecimal("100.00"));
        ordemServico.adicionarItemServico(item);

        // Act
        boolean possuiItens = ordemServico.possuiItensServico();

        // Assert
        assertTrue(possuiItens);
    }

    @Test
    @DisplayName("Deve retornar cópia dos itens de serviço")
    void deveRetornarCopiaDosItensDeServico() {
        // Arrange
        ItemServico item = ItemServico.criarComDados(UUID.randomUUID(), "Serviço", "Descrição", new BigDecimal("100.00"));
        ordemServico.adicionarItemServico(item);

        // Act
        var itens = ordemServico.getItensServico();

        // Assert
        assertEquals(1, itens.size());
        assertNotSame(ordemServico.getItensServico(), itens);
    }
}
