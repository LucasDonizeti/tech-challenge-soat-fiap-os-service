package com.techchallenge.os.infrastructure.persistence.mappers;

import com.techchallenge.os.domain.models.ItemServico;
import com.techchallenge.os.domain.models.OrdemServico;
import com.techchallenge.os.domain.models.valueobjects.StatusItemServico;
import com.techchallenge.os.domain.models.valueobjects.StatusOS;
import com.techchallenge.os.infrastructure.persistence.entities.ItemServicoEntity;
import com.techchallenge.os.infrastructure.persistence.entities.OrdemServicoEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes unitários do mapper de OrdemServico")
class OrdemServicoJpaMapperTest {

    @Mock
    private ItemServicoJpaMapper itemServicoJpaMapper;

    private OrdemServicoJpaMapper mapper;

    private UUID id;
    private UUID clienteId;
    private UUID veiculoId;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataInicioExecucao;
    private LocalDateTime dataFinalizacao;
    private LocalDateTime dataUltimaMudancaStatus;

    @BeforeEach
    void setUp() {
        mapper = new OrdemServicoJpaMapper(itemServicoJpaMapper);

        id = UUID.randomUUID();
        clienteId = UUID.randomUUID();
        veiculoId = UUID.randomUUID();
        dataCriacao = LocalDateTime.now().minusDays(1);
        dataInicioExecucao = LocalDateTime.now().minusHours(2);
        dataFinalizacao = LocalDateTime.now();
        dataUltimaMudancaStatus = LocalDateTime.now().minusMinutes(5);
    }

    @Test
    @DisplayName("Deve converter OrdemServico para entity")
    void deveConverterOrdemServicoParaEntity() {
        OrdemServico ordemServico = OrdemServico.reconstruir(
                id,
                clienteId,
                veiculoId,
                StatusOS.EM_EXECUCAO,
                dataCriacao,
                dataInicioExecucao,
                dataFinalizacao,
                dataUltimaMudancaStatus
        );

        OrdemServicoEntity entity = mapper.toEntity(ordemServico);

        assertNotNull(entity);
        assertEquals(id, entity.getId());
        assertEquals(clienteId, entity.getClienteId());
        assertEquals(veiculoId, entity.getVeiculoId());
        assertEquals(OrdemServicoEntity.StatusOSEntity.EM_EXECUCAO, entity.getStatus());
        assertEquals(dataCriacao, entity.getDataCriacao());
        assertEquals(dataInicioExecucao, entity.getDataInicioExecucao());
        assertEquals(dataFinalizacao, entity.getDataFinalizacao());
        assertTrue(entity.getItensServico().isEmpty());
    }

    @Test
    @DisplayName("Deve converter OrdemServico para entity com itens")
    void deveConverterOrdemServicoParaEntityComItensServico() {
        ItemServico itemServico = ItemServico.criarComDados(
                UUID.randomUUID(),
                "Serviço Teste",
                "Descrição do serviço",
                new BigDecimal("100.00")
        );

        OrdemServico ordemServico = OrdemServico.reconstruir(
                id,
                clienteId,
                veiculoId,
                StatusOS.RECEBIDA,
                dataCriacao,
                dataInicioExecucao,
                dataFinalizacao,
                dataUltimaMudancaStatus
        );
        ordemServico.adicionarItemServico(itemServico);

        ItemServicoEntity itemEntity = ItemServicoEntity.builder()
                .id(itemServico.getId())
                .ordemServicoId(id)
                .servicoId(itemServico.getServicoId())
                .status(ItemServicoEntity.StatusItemServicoEntity.PENDENTE)
                .valorServico(itemServico.getValorServico())
                .valorMro(itemServico.getValorMro())
                .build();

        when(itemServicoJpaMapper.toEntityList(anyList(), eq(id))).thenReturn(List.of(itemEntity));

        OrdemServicoEntity entity = mapper.toEntity(ordemServico);

        assertNotNull(entity);
        assertEquals(1, entity.getItensServico().size());
        assertEquals(itemEntity, entity.getItensServico().get(0));
        verify(itemServicoJpaMapper, times(1)).toEntityList(anyList(), eq(id));
    }

    @Test
    @DisplayName("Deve retornar null quando OrdemServico for null")
    void deveRetornarNullQuandoOrdemServicoENull() {
        OrdemServicoEntity entity = mapper.toEntity(null);

        assertNull(entity);
        verify(itemServicoJpaMapper, never()).toEntityList(anyList(), eq(id));
    }

    @Test
    @DisplayName("Deve converter entity para ordem de serviço")
    void deveConverterEntityParaOrdemServico() {
        OrdemServicoEntity entity = OrdemServicoEntity.builder()
                .id(id)
                .clienteId(clienteId)
                .veiculoId(veiculoId)
                .status(OrdemServicoEntity.StatusOSEntity.EM_EXECUCAO)
                .dataCriacao(dataCriacao)
                .dataInicioExecucao(dataInicioExecucao)
                .dataFinalizacao(dataFinalizacao)
                .dataUltimaMudancaStatus(dataUltimaMudancaStatus)
                .build();

        ItemServico itemServico = ItemServico.reconstruir(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Serviço Teste",
                "Descrição",
                StatusItemServico.PENDENTE,
                null,
                new BigDecimal("100.00"),
                BigDecimal.ZERO,
                null,
                null,
                null
        );

        when(itemServicoJpaMapper.toDomainList(anyList())).thenReturn(List.of(itemServico));

        OrdemServico ordemServico = mapper.toDomain(entity);

        assertNotNull(ordemServico);
        assertEquals(id, ordemServico.getId());
        assertEquals(clienteId, ordemServico.getCliente());
        assertEquals(veiculoId, ordemServico.getVeiculo());
        assertEquals(StatusOS.EM_EXECUCAO, ordemServico.getStatus());
        assertEquals(dataCriacao, ordemServico.getDataCriacao());
        assertEquals(dataInicioExecucao, ordemServico.getDataInicioExecucao());
        assertEquals(dataFinalizacao, ordemServico.getDataFinalizacao());
        verify(itemServicoJpaMapper, times(1)).toDomainList(anyList());
    }

    @Test
    @DisplayName("Deve retornar null quando entity for null")
    void deveRetornarNullQuandoEntityENull() {
        OrdemServico ordemServico = mapper.toDomain(null);

        assertNull(ordemServico);
        verify(itemServicoJpaMapper, never()).toDomainList(anyList());
    }

    @Test
    @DisplayName("Deve manter status nulo quando entity vier sem status")
    void deveManterStatusNuloQuandoEntityNaoTemStatus() {
        OrdemServicoEntity entity = OrdemServicoEntity.builder()
                .id(id)
                .clienteId(clienteId)
                .veiculoId(veiculoId)
                .status(null)
                .dataCriacao(dataCriacao)
                .build();

        OrdemServico ordemServico = mapper.toDomain(entity);

        assertNotNull(ordemServico);
        assertNull(ordemServico.getStatus());
    }
}
