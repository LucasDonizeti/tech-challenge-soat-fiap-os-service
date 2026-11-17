package com.techchallenge.os.infrastructure.persistence.gateway.persistences;

import com.techchallenge.os.domain.models.OrdemServico;
import com.techchallenge.os.domain.models.valueobjects.StatusOS;
import com.techchallenge.os.domain.repositories.OrdemServicoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de OrdemServicoPersistenceAdapter - Infrastructure Layer")
class OrdemServicoPersistenceAdapterTest {

    @Mock
    private OrdemServicoRepository ordemServicoRepository;

    @InjectMocks
    private OrdemServicoPersistenceAdapter adapter;

    private OrdemServico ordemServico;
    private UUID ordemId;

    @BeforeEach
    void setUp() {
        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();
        ordemId = UUID.randomUUID();
        LocalDateTime agora = LocalDateTime.now();

        ordemServico = OrdemServico.reconstruir(
                ordemId,
                clienteId,
                veiculoId,
                StatusOS.RECEBIDA,
                agora,
                null,
                null,
                agora
        );
    }

    @Test
    @DisplayName("Deve salvar OrdemServico com sucesso")
    void deveSalvarOrdemServicoComSucesso() {
        // Arrange
        when(ordemServicoRepository.save(any(OrdemServico.class))).thenReturn(ordemServico);

        // Act
        OrdemServico result = adapter.save(ordemServico);

        // Assert
        assertNotNull(result);
        assertEquals(ordemServico.getId(), result.getId());
        verify(ordemServicoRepository).save(ordemServico);
    }

    @Test
    @DisplayName("Deve buscar OrdemServico por ID com sucesso")
    void deveBuscarOrdemServicoPorIDComSucesso() {
        // Arrange
        when(ordemServicoRepository.findById(ordemId)).thenReturn(Optional.of(ordemServico));

        // Act
        Optional<OrdemServico> result = adapter.findById(ordemId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(ordemId, result.get().getId());
        verify(ordemServicoRepository).findById(ordemId);
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando OrdemServico não encontrada por ID")
    void deveRetornarOptionalVazioQuandoOrdemServicoNaoEncontradaPorID() {
        // Arrange
        when(ordemServicoRepository.findById(ordemId)).thenReturn(Optional.empty());

        // Act
        Optional<OrdemServico> result = adapter.findById(ordemId);

        // Assert
        assertFalse(result.isPresent());
        verify(ordemServicoRepository).findById(ordemId);
    }

    @Test
    @DisplayName("Deve buscar todas OrdensServico com paginação")
    void deveBuscarTodasOrdensServicoComPaginacao() {
        // Arrange
        Page<OrdemServico> page = new PageImpl<>(List.of(ordemServico));
        when(ordemServicoRepository.findAll(any(Pageable.class))).thenReturn(page);

        // Act
        Page<OrdemServico> result = adapter.findAll(Pageable.ofSize(10));

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        verify(ordemServicoRepository).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("Deve buscar OrdensServico por filtros")
    void deveBuscarOrdensServicoPorFiltros() {
        // Arrange
        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();
        LocalDateTime dataInicio = LocalDateTime.now().minusDays(7);
        LocalDateTime dataFim = LocalDateTime.now();

        Page<OrdemServico> page = new PageImpl<>(List.of(ordemServico));
        when(ordemServicoRepository.findByFilters(
                eq(clienteId),
                eq(veiculoId),
                eq(StatusOS.RECEBIDA),
                eq(dataInicio),
                eq(dataFim),
                any(Pageable.class)
        )).thenReturn(page);

        // Act
        Page<OrdemServico> result = adapter.findByFilters(
                clienteId,
                veiculoId,
                StatusOS.RECEBIDA,
                dataInicio,
                dataFim,
                Pageable.ofSize(10)
        );

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(ordemServicoRepository).findByFilters(
                eq(clienteId),
                eq(veiculoId),
                eq(StatusOS.RECEBIDA),
                eq(dataInicio),
                eq(dataFim),
                any(Pageable.class)
        );
    }

    @Test
    @DisplayName("Deve buscar OrdensServico por filtros com valores null")
    void deveBuscarOrdensServicoPorFiltrosComValoresNull() {
        // Arrange
        Page<OrdemServico> page = new PageImpl<>(List.of(ordemServico));
        when(ordemServicoRepository.findByFilters(
                eq(null),
                eq(null),
                eq(null),
                eq(null),
                eq(null),
                any(Pageable.class)
        )).thenReturn(page);

        // Act
        Page<OrdemServico> result = adapter.findByFilters(
                null,
                null,
                null,
                null,
                null,
                Pageable.ofSize(10)
        );

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(ordemServicoRepository).findByFilters(
                eq(null),
                eq(null),
                eq(null),
                eq(null),
                eq(null),
                any(Pageable.class)
        );
    }
}
