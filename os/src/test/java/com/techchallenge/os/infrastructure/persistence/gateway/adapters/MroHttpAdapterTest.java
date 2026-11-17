package com.techchallenge.os.infrastructure.persistence.gateway.adapters;

import com.techchallenge.os.application.gateway.ports.output.models.MROResumo;
import com.techchallenge.os.infrastructure.persistence.gateway.feign.CatalogMroClient;
import com.techchallenge.os.infrastructure.persistence.gateway.feign.dto.MroCatalogResponse;
import com.techchallenge.os.infrastructure.persistence.gateway.feign.dto.RetirarEstoqueMroRequest;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de MroHttpAdapter - Infrastructure Layer")
class MroHttpAdapterTest {

    @Mock
    private CatalogMroClient catalogMroClient;

    private MroHttpAdapter adapter;

    private UUID mroId;
    private String codigo;
    private MroCatalogResponse catalogResponse;

    @BeforeEach
    void setUp() {
        adapter = new MroHttpAdapter(catalogMroClient);
        mroId = UUID.randomUUID();
        codigo = "PC001";

        catalogResponse = new MroCatalogResponse(
                mroId,
                "Filtro de Óleo",
                codigo,
                "Filtro para motor",
                "PECA",
                50,
                new BigDecimal("25.00"),
                true
        );
    }

    @Test
    @DisplayName("Deve encontrar MRO por ID com sucesso")
    void deveEncontrarMROPorIdComSucesso() {
        // Arrange
        when(catalogMroClient.buscarPorId(mroId)).thenReturn(catalogResponse);

        // Act
        Optional<MROResumo> result = adapter.findById(mroId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(mroId, result.get().getId());
        assertEquals("Filtro de Óleo", result.get().getNome());
        assertEquals(codigo, result.get().getCodigo());
        assertEquals("Filtro para motor", result.get().getDescricao());
        assertEquals("PECA", result.get().getTipo());
        assertEquals(50, result.get().getQuantidadeEstoque());
        assertEquals(new BigDecimal("25.00"), result.get().getPrecoUnitario());
        assertTrue(result.get().getAtivo());

        verify(catalogMroClient, times(1)).buscarPorId(mroId);
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando MRO não encontrado por ID")
    void deveRetornarOptionalVazioQuandoMRONaoEncontradoPorId() {
        // Arrange
        when(catalogMroClient.buscarPorId(mroId))
                .thenThrow(FeignException.NotFound.class);

        // Act
        Optional<MROResumo> result = adapter.findById(mroId);

        // Assert
        assertFalse(result.isPresent());

        verify(catalogMroClient, times(1)).buscarPorId(mroId);
    }

    @Test
    @DisplayName("Deve encontrar MRO por código com sucesso")
    void deveEncontrarMROPorCodigoComSucesso() {
        // Arrange
        when(catalogMroClient.buscarPorCodigo(codigo)).thenReturn(catalogResponse);

        // Act
        Optional<MROResumo> result = adapter.buscarPorCodigo(codigo);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(mroId, result.get().getId());
        assertEquals("Filtro de Óleo", result.get().getNome());
        assertEquals(codigo, result.get().getCodigo());

        verify(catalogMroClient, times(1)).buscarPorCodigo(codigo);
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando MRO não encontrado por código")
    void deveRetornarOptionalVazioQuandoMRONaoEncontradoPorCodigo() {
        // Arrange
        when(catalogMroClient.buscarPorCodigo(codigo))
                .thenThrow(FeignException.NotFound.class);

        // Act
        Optional<MROResumo> result = adapter.buscarPorCodigo(codigo);

        // Assert
        assertFalse(result.isPresent());

        verify(catalogMroClient, times(1)).buscarPorCodigo(codigo);
    }

    @Test
    @DisplayName("Deve debitar estoque com sucesso")
    void deveDebitarEstoqueComSucesso() {
        // Arrange
        Integer quantidade = 5;
        MroCatalogResponse responseAtualizada = new MroCatalogResponse(
                mroId,
                "Filtro de Óleo",
                codigo,
                "Filtro para motor",
                "PECA",
                45,
                new BigDecimal("25.00"),
                true
        );
        when(catalogMroClient.retirarEstoque(eq(mroId), any(RetirarEstoqueMroRequest.class)))
                .thenReturn(responseAtualizada);

        // Act
        Optional<MROResumo> result = adapter.debitarEstoque(mroId, quantidade);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(45, result.get().getQuantidadeEstoque());

        ArgumentCaptor<RetirarEstoqueMroRequest> requestCaptor = ArgumentCaptor.forClass(RetirarEstoqueMroRequest.class);
        verify(catalogMroClient, times(1)).retirarEstoque(eq(mroId), requestCaptor.capture());
        assertEquals(quantidade, requestCaptor.getValue().getQuantidade());
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando MRO não encontrado ao debitar estoque")
    void deveRetornarOptionalVazioQuandoMRONaoEncontradoAoDebitarEstoque() {
        // Arrange
        when(catalogMroClient.retirarEstoque(eq(mroId), any(RetirarEstoqueMroRequest.class)))
                .thenThrow(FeignException.NotFound.class);

        // Act
        Optional<MROResumo> result = adapter.debitarEstoque(mroId, 5);

        // Assert
        assertFalse(result.isPresent());

        verify(catalogMroClient, times(1)).retirarEstoque(eq(mroId), any(RetirarEstoqueMroRequest.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando erro diferente de 404 ocorre ao buscar por ID")
    void deveLancarExcecaoQuandoErroDiferenteDe404OcorreAoBuscarPorId() {
        // Arrange
        when(catalogMroClient.buscarPorId(mroId))
                .thenThrow(new FeignException.InternalServerError(
                        "Erro interno",
                        mock(feign.Request.class),
                        null,
                        null));

        // Act & Assert
        assertThrows(FeignException.InternalServerError.class, () -> adapter.findById(mroId));

        verify(catalogMroClient, times(1)).buscarPorId(mroId);
    }

    @Test
    @DisplayName("Deve lançar exceção quando erro diferente de 404 ocorre ao buscar por código")
    void deveLancarExcecaoQuandoErroDiferenteDe404OcorreAoBuscarPorCodigo() {
        // Arrange
        when(catalogMroClient.buscarPorCodigo(codigo))
                .thenThrow(new FeignException.ServiceUnavailable(
                        "Serviço indisponível",
                        mock(feign.Request.class),
                        null,
                        null));

        // Act & Assert
        assertThrows(FeignException.ServiceUnavailable.class, () -> adapter.buscarPorCodigo(codigo));

        verify(catalogMroClient, times(1)).buscarPorCodigo(codigo);
    }

    @Test
    @DisplayName("Deve mapear corretamente todos os campos do DTO")
    void deveMapearCorretamenteTodosOsCamposDoDTO() {
        // Arrange
        MroCatalogResponse response = new MroCatalogResponse(
                mroId,
                "Óleo Sintético",
                "OL001",
                "Óleo 5W30 4L",
                "OLEO",
                100,
                new BigDecimal("89.90"),
                false
        );
        when(catalogMroClient.buscarPorId(mroId)).thenReturn(response);

        // Act
        Optional<MROResumo> result = adapter.findById(mroId);

        // Assert
        assertTrue(result.isPresent());
        MROResumo resumo = result.get();
        assertEquals(mroId, resumo.getId());
        assertEquals("Óleo Sintético", resumo.getNome());
        assertEquals("OL001", resumo.getCodigo());
        assertEquals("Óleo 5W30 4L", resumo.getDescricao());
        assertEquals("OLEO", resumo.getTipo());
        assertEquals(100, resumo.getQuantidadeEstoque());
        assertEquals(new BigDecimal("89.90"), resumo.getPrecoUnitario());
        assertFalse(resumo.getAtivo());
    }
}
