package com.techchallenge.os.application.responses;

import com.techchallenge.os.domain.models.ItemMRO;
import com.techchallenge.os.domain.models.ItemServico;
import com.techchallenge.os.domain.models.valueobjects.StatusItemServico;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de ItemServicoResponse - Application Layer")
class ItemServicoResponseTest {

    @Test
    @DisplayName("Deve criar ItemServicoResponse a partir de ItemServico")
    void deveCriarItemServicoResponseAPartirDeItemServico() {
        // Arrange
        UUID id = UUID.randomUUID();
        UUID servicoId = UUID.randomUUID();
        ItemServico itemServico = ItemServico.reconstruir(
                id,
                servicoId,
                "Troca de Óleo",
                "Troca completa de óleo",
                StatusItemServico.PENDENTE,
                "Verificar nível",
                new BigDecimal("100.00"),
                new BigDecimal("45.90"),
                LocalDateTime.now(),
                null,
                LocalDateTime.now()
        );

        // Act
        ItemServicoResponse response = ItemServicoResponse.from(itemServico);

        // Assert
        assertNotNull(response);
        assertEquals(id, response.getId());
        assertEquals(servicoId, response.getServicoId());
        assertEquals("Troca de Óleo", response.getServicoNome());
        assertEquals("Troca completa de óleo", response.getServicoDescricao());
        assertEquals(StatusItemServico.PENDENTE, response.getStatus());
        assertEquals("Verificar nível", response.getObservacoes());
        assertEquals(new BigDecimal("100.00"), response.getValorServico());
        assertEquals(new BigDecimal("45.90"), response.getValorMro());
        assertEquals(new BigDecimal("145.90"), response.getValorTotal());
    }

    @Test
    @DisplayName("Deve retornar null quando ItemServico é null")
    void deveRetornarNullQuandoItemServicoENull() {
        // Act
        ItemServicoResponse response = ItemServicoResponse.from(null);

        // Assert
        assertNull(response);
    }

    @Test
    @DisplayName("Deve criar ItemServicoResponse com lista de MROs vazia")
    void deveCriarItemServicoResponseComListaDeMROsVazia() {
        // Arrange
        UUID id = UUID.randomUUID();
        UUID servicoId = UUID.randomUUID();
        ItemServico itemServico = ItemServico.reconstruir(
                id,
                servicoId,
                "Troca de Óleo",
                "Troca completa de óleo",
                StatusItemServico.PENDENTE,
                null,
                new BigDecimal("100.00"),
                BigDecimal.ZERO,
                LocalDateTime.now(),
                null,
                LocalDateTime.now()
        );

        // Act
        ItemServicoResponse response = ItemServicoResponse.from(itemServico);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getMros());
        assertTrue(response.getMros().isEmpty());
    }

    @Test
    @DisplayName("Deve criar ItemServicoResponse usando builder")
    void deveCriarItemServicoResponseUsandoBuilder() {
        // Arrange
        UUID id = UUID.randomUUID();
        UUID servicoId = UUID.randomUUID();

        // Act
        ItemServicoResponse response = ItemServicoResponse.builder()
                .id(id)
                .servicoId(servicoId)
                .servicoNome("Troca de Pneu")
                .servicoDescricao("Troca dos 4 pneus")
                .status(StatusItemServico.EM_ANDAMENTO)
                .observacoes("Verificar alinhamento")
                .valorServico(new BigDecimal("200.00"))
                .valorMro(new BigDecimal("800.00"))
                .valorTotal(new BigDecimal("1000.00"))
                .dataInicioExecucao(LocalDateTime.now())
                .dataFinalizacao(null)
                .mros(List.of())
                .build();

        // Assert
        assertNotNull(response);
        assertEquals(id, response.getId());
        assertEquals(servicoId, response.getServicoId());
        assertEquals("Troca de Pneu", response.getServicoNome());
        assertEquals("Troca dos 4 pneus", response.getServicoDescricao());
        assertEquals(StatusItemServico.EM_ANDAMENTO, response.getStatus());
        assertEquals("Verificar alinhamento", response.getObservacoes());
        assertEquals(new BigDecimal("200.00"), response.getValorServico());
        assertEquals(new BigDecimal("800.00"), response.getValorMro());
        assertEquals(new BigDecimal("1000.00"), response.getValorTotal());
    }

    @Test
    @DisplayName("Deve criar ItemServicoResponse usando construtor vazio e setters")
    void deveCriarItemServicoResponseUsandoConstrutorVazioESetters() {
        // Arrange
        UUID id = UUID.randomUUID();
        UUID servicoId = UUID.randomUUID();

        // Act
        ItemServicoResponse response = new ItemServicoResponse();
        response.setId(id);
        response.setServicoId(servicoId);
        response.setServicoNome("Revisão Geral");
        response.setServicoDescricao("Revisão completa do veículo");
        response.setStatus(StatusItemServico.CONCLUIDO);
        response.setObservacoes("Tudo OK");
        response.setValorServico(new BigDecimal("500.00"));
        response.setValorMro(new BigDecimal("200.00"));
        response.setValorTotal(new BigDecimal("700.00"));
        response.setDataInicioExecucao(LocalDateTime.now());
        response.setDataFinalizacao(LocalDateTime.now());
        response.setMros(List.of());

        // Assert
        assertNotNull(response);
        assertEquals(id, response.getId());
        assertEquals(servicoId, response.getServicoId());
        assertEquals("Revisão Geral", response.getServicoNome());
        assertEquals(StatusItemServico.CONCLUIDO, response.getStatus());
        assertEquals(new BigDecimal("700.00"), response.getValorTotal());
    }

    @Test
    @DisplayName("Deve criar ItemServicoResponse usando construtor com todos os argumentos")
    void deveCriarItemServicoResponseUsandoConstrutorComTodosOsArgumentos() {
        // Arrange
        UUID id = UUID.randomUUID();
        UUID servicoId = UUID.randomUUID();
        LocalDateTime agora = LocalDateTime.now();

        // Act
        ItemServicoResponse response = new ItemServicoResponse(
                id,
                servicoId,
                "Alinhamento",
                "Alinhamento de direção",
                StatusItemServico.PENDENTE,
                null,
                new BigDecimal("80.00"),
                BigDecimal.ZERO,
                new BigDecimal("80.00"),
                null,
                null,
                List.of()
        );

        // Assert
        assertNotNull(response);
        assertEquals(id, response.getId());
        assertEquals(servicoId, response.getServicoId());
        assertEquals("Alinhamento", response.getServicoNome());
        assertEquals(StatusItemServico.PENDENTE, response.getStatus());
        assertEquals(new BigDecimal("80.00"), response.getValorTotal());
    }

    @Test
    @DisplayName("Deve calcular valor total corretamente")
    void deveCalcularValorTotalCorretamente() {
        // Arrange
        UUID id = UUID.randomUUID();
        UUID servicoId = UUID.randomUUID();
        ItemServico itemServico = ItemServico.reconstruir(
                id,
                servicoId,
                "Troca de Freio",
                "Troca de pastilhas e discos",
                StatusItemServico.PENDENTE,
                null,
                new BigDecimal("300.00"),
                new BigDecimal("450.00"),
                LocalDateTime.now(),
                null,
                LocalDateTime.now()
        );

        // Act
        ItemServicoResponse response = ItemServicoResponse.from(itemServico);

        // Assert
        assertEquals(new BigDecimal("750.00"), response.getValorTotal());
    }
}
