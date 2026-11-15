package com.techchallenge.os.application.responses;

import com.techchallenge.os.domain.models.ItemMRO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de ItemMROResponse - Application Layer")
class ItemMROResponseTest {

    @Test
    @DisplayName("Deve criar ItemMROResponse a partir de ItemMRO")
    void deveCriarItemMROResponseAPartirDeItemMRO() {
        // Arrange
        UUID id = UUID.randomUUID();
        UUID mroId = UUID.randomUUID();
        ItemMRO itemMRO = ItemMRO.criarComDados(
                mroId,
                "Óleo de Motor",
                "Óleo sintético 5W30",
                new BigDecimal("45.90"),
                4
        );

        // Act
        ItemMROResponse response = ItemMROResponse.from(itemMRO);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals(mroId, response.getMroId());
        assertEquals("Óleo de Motor", response.getMroNome());
        assertEquals("Óleo sintético 5W30", response.getMroDescricao());
        assertEquals(4, response.getQuantidade());
        assertEquals(new BigDecimal("45.90"), response.getValorUnitario());
        assertEquals(new BigDecimal("183.60"), response.getValorTotal());
    }

    @Test
    @DisplayName("Deve retornar null quando ItemMRO é null")
    void deveRetornarNullQuandoItemMROENull() {
        // Act
        ItemMROResponse response = ItemMROResponse.from(null);

        // Assert
        assertNull(response);
    }

    @Test
    @DisplayName("Deve criar ItemMROResponse usando builder")
    void deveCriarItemMROResponseUsandoBuilder() {
        // Arrange
        UUID id = UUID.randomUUID();
        UUID mroId = UUID.randomUUID();

        // Act
        ItemMROResponse response = ItemMROResponse.builder()
                .id(id)
                .mroId(mroId)
                .mroNome("Filtro de Óleo")
                .mroDescricao("Filtro para motor")
                .quantidade(1)
                .valorUnitario(new BigDecimal("25.00"))
                .valorTotal(new BigDecimal("25.00"))
                .build();

        // Assert
        assertNotNull(response);
        assertEquals(id, response.getId());
        assertEquals(mroId, response.getMroId());
        assertEquals("Filtro de Óleo", response.getMroNome());
        assertEquals("Filtro para motor", response.getMroDescricao());
        assertEquals(1, response.getQuantidade());
        assertEquals(new BigDecimal("25.00"), response.getValorUnitario());
        assertEquals(new BigDecimal("25.00"), response.getValorTotal());
    }

    @Test
    @DisplayName("Deve criar ItemMROResponse usando construtor vazio e setters")
    void deveCriarItemMROResponseUsandoConstrutorVazioESetters() {
        // Arrange
        UUID id = UUID.randomUUID();
        UUID mroId = UUID.randomUUID();

        // Act
        ItemMROResponse response = new ItemMROResponse();
        response.setId(id);
        response.setMroId(mroId);
        response.setMroNome("Pastilha de Freio");
        response.setMroDescricao("Pastilha para freio dianteiro");
        response.setQuantidade(2);
        response.setValorUnitario(new BigDecimal("80.00"));
        response.setValorTotal(new BigDecimal("160.00"));

        // Assert
        assertNotNull(response);
        assertEquals(id, response.getId());
        assertEquals(mroId, response.getMroId());
        assertEquals("Pastilha de Freio", response.getMroNome());
        assertEquals("Pastilha para freio dianteiro", response.getMroDescricao());
        assertEquals(2, response.getQuantidade());
        assertEquals(new BigDecimal("80.00"), response.getValorUnitario());
        assertEquals(new BigDecimal("160.00"), response.getValorTotal());
    }

    @Test
    @DisplayName("Deve criar ItemMROResponse usando construtor com todos os argumentos")
    void deveCriarItemMROResponseUsandoConstrutorComTodosOsArgumentos() {
        // Arrange
        UUID id = UUID.randomUUID();
        UUID mroId = UUID.randomUUID();

        // Act
        ItemMROResponse response = new ItemMROResponse(
                id,
                mroId,
                "Bateria",
                "Bateria 12V",
                1,
                new BigDecimal("350.00"),
                new BigDecimal("350.00")
        );

        // Assert
        assertNotNull(response);
        assertEquals(id, response.getId());
        assertEquals(mroId, response.getMroId());
        assertEquals("Bateria", response.getMroNome());
        assertEquals("Bateria 12V", response.getMroDescricao());
        assertEquals(1, response.getQuantidade());
        assertEquals(new BigDecimal("350.00"), response.getValorUnitario());
        assertEquals(new BigDecimal("350.00"), response.getValorTotal());
    }

    @Test
    @DisplayName("Deve calcular valor total corretamente")
    void deveCalcularValorTotalCorretamente() {
        // Arrange
        UUID mroId = UUID.randomUUID();
        ItemMRO itemMRO = ItemMRO.criarComDados(
                mroId,
                "Pneu",
                "Pneu 195/65 R15",
                new BigDecimal("350.00"),
                4
        );

        // Act
        ItemMROResponse response = ItemMROResponse.from(itemMRO);

        // Assert
        assertEquals(new BigDecimal("1400.00"), response.getValorTotal());
    }
}
