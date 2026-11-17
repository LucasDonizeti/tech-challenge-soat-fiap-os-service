package com.techchallenge.os.infrastructure.persistence.repositories.impl;

import com.techchallenge.os.domain.models.OrdemServico;
import com.techchallenge.os.domain.models.valueobjects.StatusOS;
import com.techchallenge.os.infrastructure.persistence.entities.OrdemServicoEntity;
import com.techchallenge.os.infrastructure.persistence.mappers.OrdemServicoJpaMapper;
import com.techchallenge.os.infrastructure.persistence.repositories.OrdemServicoJpaRepository;
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
@DisplayName("Testes de OrdemServicoRepositoryImpl - Infrastructure Layer")
class OrdemServicoRepositoryImplTest {

    @Mock
    private OrdemServicoJpaRepository jpaRepository;

    @Mock
    private OrdemServicoJpaMapper mapper;

    @InjectMocks
    private OrdemServicoRepositoryImpl repository;

    private OrdemServico ordemServico;
    private OrdemServicoEntity entity;
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

        entity = new OrdemServicoEntity();
        entity.setId(ordemId);
    }

    @Test
    @DisplayName("Deve salvar OrdemServico com sucesso")
    void deveSalvarOrdemServicoComSucesso() {
        // Arrange
        when(mapper.toEntity(any(OrdemServico.class))).thenReturn(entity);
        when(jpaRepository.save(any(OrdemServicoEntity.class))).thenReturn(entity);
        when(mapper.toDomain(any(OrdemServicoEntity.class))).thenReturn(ordemServico);

        // Act
        OrdemServico result = repository.save(ordemServico);

        // Assert
        assertNotNull(result);
        assertEquals(ordemServico.getId(), result.getId());
        verify(mapper).toEntity(ordemServico);
        verify(jpaRepository).save(entity);
        verify(mapper).toDomain(entity);
    }

    @Test
    @DisplayName("Deve buscar OrdemServico por ID com sucesso")
    void deveBuscarOrdemServicoPorIDComSucesso() {
        // Arrange
        when(jpaRepository.findById(ordemId)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(any(OrdemServicoEntity.class))).thenReturn(ordemServico);

        // Act
        Optional<OrdemServico> result = repository.findById(ordemId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(ordemId, result.get().getId());
        verify(jpaRepository).findById(ordemId);
        verify(mapper).toDomain(entity);
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando OrdemServico não encontrada por ID")
    void deveRetornarOptionalVazioQuandoOrdemServicoNaoEncontradaPorID() {
        // Arrange
        when(jpaRepository.findById(ordemId)).thenReturn(Optional.empty());

        // Act
        Optional<OrdemServico> result = repository.findById(ordemId);

        // Assert
        assertFalse(result.isPresent());
        verify(jpaRepository).findById(ordemId);
        verify(mapper, never()).toDomain(any());
    }

    @Test
    @DisplayName("Deve buscar todas OrdensServico com paginação")
    void deveBuscarTodasOrdensServicoComPaginacao() {
        // Arrange
        Page<OrdemServicoEntity> entityPage = new PageImpl<>(List.of(entity));
        Page<OrdemServico> domainPage = new PageImpl<>(List.of(ordemServico));

        when(jpaRepository.findAll(any(Pageable.class))).thenReturn(entityPage);
        when(mapper.toDomain(any(OrdemServicoEntity.class))).thenReturn(ordemServico);

        // Act
        Page<OrdemServico> result = repository.findAll(Pageable.ofSize(10));

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        verify(jpaRepository).findAll(any(Pageable.class));
        verify(mapper).toDomain(entity);
    }

    @Test
    @DisplayName("Deve buscar OrdensServico por filtros")
    void deveBuscarOrdensServicoPorFiltros() {
        // Arrange
        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();
        LocalDateTime dataInicio = LocalDateTime.now().minusDays(7);
        LocalDateTime dataFim = LocalDateTime.now();

        Page<OrdemServicoEntity> entityPage = new PageImpl<>(List.of(entity));
        Page<OrdemServico> domainPage = new PageImpl<>(List.of(ordemServico));

        when(jpaRepository.findByFilters(
                eq(clienteId),
                eq(veiculoId),
                any(OrdemServicoEntity.StatusOSEntity.class),
                eq(dataInicio),
                eq(dataFim),
                any(Pageable.class)
        )).thenReturn(entityPage);
        when(mapper.toDomain(any(OrdemServicoEntity.class))).thenReturn(ordemServico);

        // Act
        Page<OrdemServico> result = repository.findByFilters(
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
        verify(jpaRepository).findByFilters(
                eq(clienteId),
                eq(veiculoId),
                any(OrdemServicoEntity.StatusOSEntity.class),
                eq(dataInicio),
                eq(dataFim),
                any(Pageable.class)
        );
        verify(mapper).toDomain(entity);
    }

    @Test
    @DisplayName("Deve buscar OrdensServico por filtros com status null")
    void deveBuscarOrdensServicoPorFiltrosComStatusNull() {
        // Arrange
        UUID clienteId = UUID.randomUUID();
        LocalDateTime dataInicio = LocalDateTime.now().minusDays(7);
        LocalDateTime dataFim = LocalDateTime.now();

        Page<OrdemServicoEntity> entityPage = new PageImpl<>(List.of(entity));

        when(jpaRepository.findByFilters(
                eq(clienteId),
                eq(null),
                eq(null),
                eq(dataInicio),
                eq(dataFim),
                any(Pageable.class)
        )).thenReturn(entityPage);
        when(mapper.toDomain(any(OrdemServicoEntity.class))).thenReturn(ordemServico);

        // Act
        Page<OrdemServico> result = repository.findByFilters(
                clienteId,
                null,
                null,
                dataInicio,
                dataFim,
                Pageable.ofSize(10)
        );

        // Assert
        assertNotNull(result);
        verify(jpaRepository).findByFilters(
                eq(clienteId),
                eq(null),
                eq(null),
                eq(dataInicio),
                eq(dataFim),
                any(Pageable.class)
        );
    }
}
