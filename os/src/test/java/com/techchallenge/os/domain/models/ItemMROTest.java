package com.techchallenge.os.domain.models;

import com.techchallenge.os.domain.exceptions.ValidacaoOrdemServicoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de ItemMRO - Domain Layer")
class ItemMROTest {

    @Test
    @DisplayName("Deve criar ItemMRO com dados válidos")
    void deveCriarItemMROComDadosValidos() {
        // Arrange
        UUID mroId = UUID.randomUUID();
        String nome = "Filtro de Óleo";
        String descricao = "Filtro para motor";
        BigDecimal precoUnitario = new BigDecimal("25.00");
        Integer quantidade = 2;

        // Act
        ItemMRO itemMRO = ItemMRO.criarComDados(mroId, nome, descricao, precoUnitario, quantidade);

        // Assert
        assertNotNull(itemMRO);
        assertNotNull(itemMRO.getId());
        assertEquals(mroId, itemMRO.getMroId());
        assertEquals(nome, itemMRO.getMroNome());
        assertEquals(descricao, itemMRO.getMroDescricao());
        assertEquals(quantidade, itemMRO.getQuantidade());
        assertEquals(precoUnitario, itemMRO.getValorUnitario());
    }

    @Test
    @DisplayName("Deve lançar exceção quando mroId é nulo")
    void deveLancarExcecaoQuandoMroIdENulo() {
        // Act & Assert
        ValidacaoOrdemServicoException exception = assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> ItemMRO.criarComDados(null, "Nome", "Descrição", new BigDecimal("10.00"), 1)
        );

        assertEquals("ID do MRO não pode ser nulo", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando quantidade é nula")
    void deveLancarExcecaoQuandoQuantidadeENula() {
        // Act & Assert
        ValidacaoOrdemServicoException exception = assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> ItemMRO.criarComDados(UUID.randomUUID(), "Nome", "Descrição", new BigDecimal("10.00"), null)
        );

        assertEquals("Quantidade deve ser maior que zero", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando quantidade é zero")
    void deveLancarExcecaoQuandoQuantidadeEZero() {
        // Act & Assert
        ValidacaoOrdemServicoException exception = assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> ItemMRO.criarComDados(UUID.randomUUID(), "Nome", "Descrição", new BigDecimal("10.00"), 0)
        );

        assertEquals("Quantidade deve ser maior que zero", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando quantidade é negativa")
    void deveLancarExcecaoQuandoQuantidadeENegativa() {
        // Act & Assert
        ValidacaoOrdemServicoException exception = assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> ItemMRO.criarComDados(UUID.randomUUID(), "Nome", "Descrição", new BigDecimal("10.00"), -1)
        );

        assertEquals("Quantidade deve ser maior que zero", exception.getMessage());
    }

    @Test
    @DisplayName("Deve reconstruir ItemMRO a partir de dados persistidos")
    void deveReconstruirItemMROAPartirDeDadosPersistidos() {
        // Arrange
        UUID id = UUID.randomUUID();
        UUID mroId = UUID.randomUUID();
        String nome = "Filtro de Óleo";
        String descricao = "Filtro para motor";
        Integer quantidade = 3;
        BigDecimal valorUnitario = new BigDecimal("25.00");

        // Act
        ItemMRO itemMRO = ItemMRO.reconstruir(id, mroId, nome, descricao, quantidade, valorUnitario);

        // Assert
        assertNotNull(itemMRO);
        assertEquals(id, itemMRO.getId());
        assertEquals(mroId, itemMRO.getMroId());
        assertEquals(nome, itemMRO.getMroNome());
        assertEquals(descricao, itemMRO.getMroDescricao());
        assertEquals(quantidade, itemMRO.getQuantidade());
        assertEquals(valorUnitario, itemMRO.getValorUnitario());
    }

    @Test
    @DisplayName("Deve atualizar quantidade com valor válido")
    void deveAtualizarQuantidadeComValorValido() {
        // Arrange
        ItemMRO itemMRO = ItemMRO.criarComDados(UUID.randomUUID(), "Nome", "Descrição", new BigDecimal("10.00"), 2);

        // Act
        itemMRO.atualizarQuantidade(5);

        // Assert
        assertEquals(5, itemMRO.getQuantidade());
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar quantidade com valor nulo")
    void deveLancarExcecaoAoAtualizarQuantidadeComValorNulo() {
        // Arrange
        ItemMRO itemMRO = ItemMRO.criarComDados(UUID.randomUUID(), "Nome", "Descrição", new BigDecimal("10.00"), 2);

        // Act & Assert
        ValidacaoOrdemServicoException exception = assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> itemMRO.atualizarQuantidade(null)
        );

        assertEquals("Quantidade deve ser maior que zero", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar quantidade com valor zero")
    void deveLancarExcecaoAoAtualizarQuantidadeComValorZero() {
        // Arrange
        ItemMRO itemMRO = ItemMRO.criarComDados(UUID.randomUUID(), "Nome", "Descrição", new BigDecimal("10.00"), 2);

        // Act & Assert
        ValidacaoOrdemServicoException exception = assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> itemMRO.atualizarQuantidade(0)
        );

        assertEquals("Quantidade deve ser maior que zero", exception.getMessage());
    }

    @Test
    @DisplayName("Deve calcular valor total corretamente")
    void deveCalcularValorTotalCorretamente() {
        // Arrange
        BigDecimal valorUnitario = new BigDecimal("25.00");
        Integer quantidade = 3;
        ItemMRO itemMRO = ItemMRO.criarComDados(UUID.randomUUID(), "Nome", "Descrição", valorUnitario, quantidade);

        // Act
        BigDecimal valorTotal = itemMRO.calcularValorTotal();

        // Assert
        assertEquals(new BigDecimal("75.00"), valorTotal);
    }

    @Test
    @DisplayName("Deve calcular valor total com valor unitário decimal")
    void deveCalcularValorTotalComValorUnitarioDecimal() {
        // Arrange
        BigDecimal valorUnitario = new BigDecimal("15.50");
        Integer quantidade = 4;
        ItemMRO itemMRO = ItemMRO.criarComDados(UUID.randomUUID(), "Nome", "Descrição", valorUnitario, quantidade);

        // Act
        BigDecimal valorTotal = itemMRO.calcularValorTotal();

        // Assert
        assertEquals(new BigDecimal("62.00"), valorTotal);
    }

    @Test
    @DisplayName("Deve manter igualdade entre itens com mesmo ID")
    void deveManterIgualdadeEntreItensComMesmoId() {
        // Arrange
        UUID id = UUID.randomUUID();
        ItemMRO item1 = ItemMRO.reconstruir(id, UUID.randomUUID(), "Nome", "Descrição", 2, new BigDecimal("10.00"));
        ItemMRO item2 = ItemMRO.reconstruir(id, UUID.randomUUID(), "Outro Nome", "Outra Descrição", 5, new BigDecimal("20.00"));

        // Assert
        assertEquals(item1, item2);
        assertEquals(item1.hashCode(), item2.hashCode());
    }

    @Test
    @DisplayName("Deve ser diferente quando IDs são diferentes")
    void deveSerDiferenteQuandoIdsSaoDiferentes() {
        // Arrange
        ItemMRO item1 = ItemMRO.criarComDados(UUID.randomUUID(), "Nome", "Descrição", new BigDecimal("10.00"), 2);
        ItemMRO item2 = ItemMRO.criarComDados(UUID.randomUUID(), "Nome", "Descrição", new BigDecimal("10.00"), 2);

        // Assert
        assertNotEquals(item1, item2);
    }

    @Test
    @DisplayName("Deve ser igual ao mesmo objeto")
    void deveSerIgualAoMesmoObjeto() {
        // Arrange
        ItemMRO itemMRO = ItemMRO.criarComDados(UUID.randomUUID(), "Nome", "Descrição", new BigDecimal("10.00"), 2);

        // Assert
        assertEquals(itemMRO, itemMRO);
    }

    @Test
    @DisplayName("Deve ser diferente de null")
    void deveSerDiferenteDeNull() {
        // Arrange
        ItemMRO itemMRO = ItemMRO.criarComDados(UUID.randomUUID(), "Nome", "Descrição", new BigDecimal("10.00"), 2);

        // Assert
        assertNotEquals(itemMRO, null);
    }

    @Test
    @DisplayName("Deve criar ItemMRO com nome e descrição nulos")
    void deveCriarItemMROComNomeEDescricaoNulos() {
        // Act
        ItemMRO itemMRO = ItemMRO.criarComDados(UUID.randomUUID(), null, null, new BigDecimal("10.00"), 1);

        // Assert
        assertNotNull(itemMRO);
        assertNull(itemMRO.getMroNome());
        assertNull(itemMRO.getMroDescricao());
    }

    @Test
    @DisplayName("Deve criar ItemMRO com valor unitário nulo")
    void deveCriarItemMROComValorUnitarioNulo() {
        // Act
        ItemMRO itemMRO = ItemMRO.criarComDados(UUID.randomUUID(), "Nome", "Descrição", null, 1);

        // Assert
        assertNotNull(itemMRO);
        assertNull(itemMRO.getValorUnitario());
    }
}
