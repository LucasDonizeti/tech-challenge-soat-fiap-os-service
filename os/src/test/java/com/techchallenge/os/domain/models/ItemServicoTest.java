package com.techchallenge.os.domain.models;

import com.techchallenge.os.domain.exceptions.ValidacaoOrdemServicoException;
import com.techchallenge.os.domain.models.valueobjects.StatusItemServico;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de ItemServico - Domain Layer")
class ItemServicoTest {

    private ItemServico itemServico;
    private UUID servicoId;

    @BeforeEach
    void setUp() {
        servicoId = UUID.randomUUID();
        itemServico = ItemServico.criarComDados(servicoId, "Troca de Óleo", "Troca completa", new BigDecimal("150.00"));
    }

    @Test
    @DisplayName("Deve criar ItemServico com dados válidos")
    void deveCriarItemServicoComDadosValidos() {
        // Assert
        assertNotNull(itemServico);
        assertNotNull(itemServico.getId());
        assertEquals(servicoId, itemServico.getServicoId());
        assertEquals("Troca de Óleo", itemServico.getServicoNome());
        assertEquals("Troca completa", itemServico.getServicoDescricao());
        assertEquals(StatusItemServico.PENDENTE, itemServico.getStatus());
        assertEquals(new BigDecimal("150.00"), itemServico.getValorServico());
        assertEquals(BigDecimal.ZERO, itemServico.getValorMro());
        assertNotNull(itemServico.getDataUltimaMudancaStatus());
    }

    @Test
    @DisplayName("Deve lançar exceção quando servicoId é nulo")
    void deveLancarExcecaoQuandoServicoIdENulo() {
        // Act & Assert
        ValidacaoOrdemServicoException exception = assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> ItemServico.criarComDados(null, "Nome", "Descrição", new BigDecimal("10.00"))
        );

        assertEquals("ID do serviço não pode ser nulo", exception.getMessage());
    }

    @Test
    @DisplayName("Deve reconstruir ItemServico a partir de dados persistidos")
    void deveReconstruirItemServicoAPartirDeDadosPersistidos() {
        // Arrange
        UUID id = UUID.randomUUID();
        UUID servicoId = UUID.randomUUID();
        String nome = "Troca de Óleo";
        String descricao = "Troca completa";
        StatusItemServico status = StatusItemServico.EM_ANDAMENTO;
        String observacoes = "Observação";
        BigDecimal valorServico = new BigDecimal("150.00");
        BigDecimal valorMro = new BigDecimal("50.00");
        LocalDateTime dataInicio = LocalDateTime.now();
        LocalDateTime dataFinalizacao = LocalDateTime.now().plusHours(2);
        LocalDateTime dataUltimaMudanca = LocalDateTime.now();

        // Act
        ItemServico item = ItemServico.reconstruir(id, servicoId, nome, descricao, status, observacoes,
                valorServico, valorMro, dataInicio, dataFinalizacao, dataUltimaMudanca);

        // Assert
        assertNotNull(item);
        assertEquals(id, item.getId());
        assertEquals(servicoId, item.getServicoId());
        assertEquals(nome, item.getServicoNome());
        assertEquals(descricao, item.getServicoDescricao());
        assertEquals(status, item.getStatus());
        assertEquals(observacoes, item.getObservacoes());
        assertEquals(valorServico, item.getValorServico());
        assertEquals(valorMro, item.getValorMro());
        assertEquals(dataInicio, item.getDataInicioExecucao());
        assertEquals(dataFinalizacao, item.getDataFinalizacao());
        assertEquals(dataUltimaMudanca, item.getDataUltimaMudancaStatus());
    }

    @Test
    @DisplayName("Deve adicionar MRO com dados válidos")
    void deveAdicionarMROComDadosValidos() {
        // Arrange
        UUID mroId = UUID.randomUUID();
        BigDecimal precoUnitario = new BigDecimal("25.00");
        Integer quantidade = 2;

        // Act
        itemServico.adicionarMRO(mroId, "Filtro de Óleo", "Filtro para motor", precoUnitario, quantidade);

        // Assert
        assertEquals(1, itemServico.getMrosServicos().size());
        assertEquals(new BigDecimal("50.00"), itemServico.getValorMro());
    }

    @Test
    @DisplayName("Deve lançar exceção ao adicionar MRO com ID nulo")
    void deveLancarExcecaoAoAdicionarMROComIdNulo() {
        // Act & Assert
        ValidacaoOrdemServicoException exception = assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> itemServico.adicionarMRO(null, "Nome", "Descrição", new BigDecimal("10.00"), 1)
        );

        assertEquals("ID do MRO não pode ser nulo", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao adicionar MRO com quantidade nula")
    void deveLancarExcecaoAoAdicionarMROComQuantidadeNula() {
        // Act & Assert
        ValidacaoOrdemServicoException exception = assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> itemServico.adicionarMRO(UUID.randomUUID(), "Nome", "Descrição", new BigDecimal("10.00"), null)
        );

        assertEquals("Quantidade deve ser maior que zero", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao adicionar MRO com quantidade zero")
    void deveLancarExcecaoAoAdicionarMROComQuantidadeZero() {
        // Act & Assert
        ValidacaoOrdemServicoException exception = assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> itemServico.adicionarMRO(UUID.randomUUID(), "Nome", "Descrição", new BigDecimal("10.00"), 0)
        );

        assertEquals("Quantidade deve ser maior que zero", exception.getMessage());
    }

    @Test
    @DisplayName("Deve adicionar ItemMRO já construído")
    void deveAdicionarItemMROJaConstruido() {
        // Arrange
        ItemMRO itemMRO = ItemMRO.criarComDados(UUID.randomUUID(), "Filtro", "Descrição", new BigDecimal("10.00"), 1);

        // Act
        itemServico.adicionarMRO(itemMRO);

        // Assert
        assertEquals(1, itemServico.getMrosServicos().size());
        assertEquals(new BigDecimal("10.00"), itemServico.getValorMro());
    }

    @Test
    @DisplayName("Deve lançar exceção ao adicionar ItemMRO nulo")
    void deveLancarExcecaoAoAdicionarItemMRONulo() {
        // Act & Assert
        ValidacaoOrdemServicoException exception = assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> itemServico.adicionarMRO(null)
        );

        assertEquals("ItemMRO não pode ser nulo", exception.getMessage());
    }

    @Test
    @DisplayName("Deve remover MRO por ID")
    void deveRemoverMROPorId() {
        // Arrange
        ItemMRO itemMRO = ItemMRO.criarComDados(UUID.randomUUID(), "Filtro", "Descrição", new BigDecimal("10.00"), 1);
        itemServico.adicionarMRO(itemMRO);

        // Act
        itemServico.removerMRO(itemMRO.getId());

        // Assert
        assertEquals(0, itemServico.getMrosServicos().size());
        assertEquals(BigDecimal.ZERO, itemServico.getValorMro());
    }

    @Test
    @DisplayName("Deve atualizar status para EM_ANDAMENTO")
    void deveAtualizarStatusParaEmAndamento() {
        // Act
        itemServico.atualizarStatus(StatusItemServico.EM_ANDAMENTO);

        // Assert
        assertEquals(StatusItemServico.EM_ANDAMENTO, itemServico.getStatus());
        assertNotNull(itemServico.getDataInicioExecucao());
    }

    @Test
    @DisplayName("Deve atualizar status para CONCLUIDO")
    void deveAtualizarStatusParaConcluido() {
        // Act
        itemServico.atualizarStatus(StatusItemServico.CONCLUIDO);

        // Assert
        assertEquals(StatusItemServico.CONCLUIDO, itemServico.getStatus());
        assertNotNull(itemServico.getDataFinalizacao());
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar status para nulo")
    void deveLancarExcecaoAoAtualizarStatusParaNulo() {
        // Act & Assert
        ValidacaoOrdemServicoException exception = assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> itemServico.atualizarStatus(null)
        );

        assertEquals("Status não pode ser nulo", exception.getMessage());
    }

    @Test
    @DisplayName("Deve atualizar observações")
    void deveAtualizarObservacoes() {
        // Act
        itemServico.atualizarObservacoes("Nova observação");

        // Assert
        assertEquals("Nova observação", itemServico.getObservacoes());
    }

    @Test
    @DisplayName("Deve atualizar valor MRO")
    void deveAtualizarValorMRO() {
        // Arrange
        itemServico.adicionarMRO(UUID.randomUUID(), "Filtro", "Descrição", new BigDecimal("10.00"), 2);

        // Act
        itemServico.atualizarValorMRO();

        // Assert
        assertEquals(new BigDecimal("20.00"), itemServico.getValorMro());
    }

    @Test
    @DisplayName("Deve atualizar valor serviço")
    void deveAtualizarValorServico() {
        // Act
        itemServico.atualizarValorServico(new BigDecimal("200.00"));

        // Assert
        assertEquals(new BigDecimal("200.00"), itemServico.getValorServico());
    }

    @Test
    @DisplayName("Não deve atualizar valor serviço quando novo preço é nulo")
    void naoDeveAtualizarValorServicoQuandoNovoPrecoENulo() {
        // Arrange
        BigDecimal valorOriginal = itemServico.getValorServico();

        // Act
        itemServico.atualizarValorServico(null);

        // Assert
        assertEquals(valorOriginal, itemServico.getValorServico());
    }

    @Test
    @DisplayName("Deve calcular valor total")
    void deveCalcularValorTotal() {
        // Arrange
        itemServico.adicionarMRO(UUID.randomUUID(), "Filtro", "Descrição", new BigDecimal("25.00"), 2);

        // Act
        BigDecimal valorTotal = itemServico.calcularValorTotal();

        // Assert
        assertEquals(new BigDecimal("200.00"), valorTotal);
    }

    @Test
    @DisplayName("Deve setar OrdemServico")
    void deveSetarOrdemServico() {
        // Arrange
        OrdemServico ordemServico = OrdemServico.criar(UUID.randomUUID(), UUID.randomUUID());

        // Act
        itemServico.setOrdemServico(ordemServico);

        // Assert
        assertEquals(ordemServico, itemServico.getOrdemServico());
    }

    @Test
    @DisplayName("Deve manter igualdade entre itens com mesmo ID")
    void deveManterIgualdadeEntreItensComMesmoId() {
        // Arrange
        UUID id = itemServico.getId();
        ItemServico item2 = ItemServico.reconstruir(id, UUID.randomUUID(), "Outro Nome", "Outra Descrição",
                StatusItemServico.CONCLUIDO, null, new BigDecimal("100.00"), BigDecimal.ZERO, null, null, null);

        // Assert
        assertEquals(itemServico, item2);
        assertEquals(itemServico.hashCode(), item2.hashCode());
    }

    @Test
    @DisplayName("Deve ser diferente quando IDs são diferentes")
    void deveSerDiferenteQuandoIdsSaoDiferentes() {
        // Arrange
        ItemServico item2 = ItemServico.criarComDados(UUID.randomUUID(), "Nome", "Descrição", new BigDecimal("10.00"));

        // Assert
        assertNotEquals(itemServico, item2);
    }

    @Test
    @DisplayName("Deve ser igual ao mesmo objeto")
    void deveSerIgualAoMesmoObjeto() {
        // Assert
        assertEquals(itemServico, itemServico);
    }

    @Test
    @DisplayName("Deve ser diferente de null")
    void deveSerDiferenteDeNull() {
        // Assert
        assertNotEquals(itemServico, null);
    }

    @Test
    @DisplayName("Deve adicionar múltiplos MROs")
    void deveAdicionarMultiplosMROs() {
        // Act
        itemServico.adicionarMRO(UUID.randomUUID(), "Filtro", "Descrição", new BigDecimal("10.00"), 1);
        itemServico.adicionarMRO(UUID.randomUUID(), "Óleo", "Descrição", new BigDecimal("20.00"), 2);

        // Assert
        assertEquals(2, itemServico.getMrosServicos().size());
        assertEquals(new BigDecimal("50.00"), itemServico.getValorMro());
    }

    @Test
    @DisplayName("Deve criar ItemServico com nome e descrição nulos")
    void deveCriarItemServicoComNomeEDescricaoNulos() {
        // Act
        ItemServico item = ItemServico.criarComDados(UUID.randomUUID(), null, null, new BigDecimal("10.00"));

        // Assert
        assertNotNull(item);
        assertNull(item.getServicoNome());
        assertNull(item.getServicoDescricao());
    }

    @Test
    @DisplayName("Deve criar ItemServico com valor serviço nulo")
    void deveCriarItemServicoComValorServicoNulo() {
        // Act
        ItemServico item = ItemServico.criarComDados(UUID.randomUUID(), "Nome", "Descrição", null);

        // Assert
        assertNotNull(item);
        assertNull(item.getValorServico());
    }
}
