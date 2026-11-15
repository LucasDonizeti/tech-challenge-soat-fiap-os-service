package com.techchallenge.os.infrastructure.persistence.gateway.adapters;

import com.techchallenge.os.application.gateway.ports.output.models.ServicoResumo;
import com.techchallenge.os.infrastructure.persistence.gateway.feign.CatalogServicoClient;
import com.techchallenge.os.infrastructure.persistence.gateway.feign.dto.ServicoCatalogResponse;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
@DisplayName("Testes de ServicoHttpAdapter - Infrastructure Layer")
class ServicoHttpAdapterTest {

    @Mock
    private CatalogServicoClient catalogServicoClient;

    private ServicoHttpAdapter adapter;

    private UUID servicoId;
    private String codigo;
    private ServicoCatalogResponse catalogResponse;

    @BeforeEach
    void setUp() {
        adapter = new ServicoHttpAdapter(catalogServicoClient);
        servicoId = UUID.randomUUID();
        codigo = "SVC-001";

        catalogResponse = new ServicoCatalogResponse(
                servicoId,
                "Troca de Óleo",
                codigo,
                "Troca completa de óleo",
                new BigDecimal("150.00"),
                true
        );
    }

    @Test
    @DisplayName("Deve encontrar serviço por ID com sucesso")
    void deveEncontrarServicoPorIdComSucesso() {
        // Arrange
        when(catalogServicoClient.buscarPorId(servicoId)).thenReturn(catalogResponse);

        // Act
        Optional<ServicoResumo> result = adapter.findById(servicoId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(servicoId, result.get().getId());
        assertEquals("Troca de Óleo", result.get().getNome());
        assertEquals(codigo, result.get().getCodigo());
        assertEquals("Troca completa de óleo", result.get().getDescricao());
        assertEquals(new BigDecimal("150.00"), result.get().getPreco());
        assertTrue(result.get().getAtivo());

        verify(catalogServicoClient, times(1)).buscarPorId(servicoId);
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando serviço não encontrado por ID")
    void deveRetornarOptionalVazioQuandoServicoNaoEncontradoPorId() {
        // Arrange
        when(catalogServicoClient.buscarPorId(servicoId))
                .thenThrow(FeignException.NotFound.class);

        // Act
        Optional<ServicoResumo> result = adapter.findById(servicoId);

        // Assert
        assertFalse(result.isPresent());

        verify(catalogServicoClient, times(1)).buscarPorId(servicoId);
    }

    @Test
    @DisplayName("Deve encontrar serviço por código com sucesso")
    void deveEncontrarServicoPorCodigoComSucesso() {
        // Arrange
        when(catalogServicoClient.buscarPorCodigo(codigo)).thenReturn(catalogResponse);

        // Act
        Optional<ServicoResumo> result = adapter.buscarPorCodigo(codigo);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(servicoId, result.get().getId());
        assertEquals("Troca de Óleo", result.get().getNome());
        assertEquals(codigo, result.get().getCodigo());

        verify(catalogServicoClient, times(1)).buscarPorCodigo(codigo);
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando serviço não encontrado por código")
    void deveRetornarOptionalVazioQuandoServicoNaoEncontradoPorCodigo() {
        // Arrange
        when(catalogServicoClient.buscarPorCodigo(codigo))
                .thenThrow(FeignException.NotFound.class);

        // Act
        Optional<ServicoResumo> result = adapter.buscarPorCodigo(codigo);

        // Assert
        assertFalse(result.isPresent());

        verify(catalogServicoClient, times(1)).buscarPorCodigo(codigo);
    }

    @Test
    @DisplayName("Deve lançar exceção quando erro diferente de 404 ocorre ao buscar por ID")
    void deveLancarExcecaoQuandoErroDiferenteDe404OcorreAoBuscarPorId() {
        // Arrange
        when(catalogServicoClient.buscarPorId(servicoId))
                .thenThrow(new FeignException.InternalServerError(
                        "Erro interno",
                        mock(feign.Request.class),
                        null,
                        null));

        // Act & Assert
        assertThrows(FeignException.InternalServerError.class, () -> adapter.findById(servicoId));

        verify(catalogServicoClient, times(1)).buscarPorId(servicoId);
    }

    @Test
    @DisplayName("Deve lançar exceção quando erro diferente de 404 ocorre ao buscar por código")
    void deveLancarExcecaoQuandoErroDiferenteDe404OcorreAoBuscarPorCodigo() {
        // Arrange
        when(catalogServicoClient.buscarPorCodigo(codigo))
                .thenThrow(new FeignException.ServiceUnavailable(
                        "Serviço indisponível",
                        mock(feign.Request.class),
                        null,
                        null));

        // Act & Assert
        assertThrows(FeignException.ServiceUnavailable.class, () -> adapter.buscarPorCodigo(codigo));

        verify(catalogServicoClient, times(1)).buscarPorCodigo(codigo);
    }

    @Test
    @DisplayName("Deve mapear corretamente todos os campos do DTO")
    void deveMapearCorretamenteTodosOsCamposDoDTO() {
        // Arrange
        ServicoCatalogResponse response = new ServicoCatalogResponse(
                servicoId,
                "Alinhamento",
                "SVC-002",
                "Alinhamento de direção",
                new BigDecimal("80.00"),
                false
        );
        when(catalogServicoClient.buscarPorId(servicoId)).thenReturn(response);

        // Act
        Optional<ServicoResumo> result = adapter.findById(servicoId);

        // Assert
        assertTrue(result.isPresent());
        ServicoResumo resumo = result.get();
        assertEquals(servicoId, resumo.getId());
        assertEquals("Alinhamento", resumo.getNome());
        assertEquals("SVC-002", resumo.getCodigo());
        assertEquals("Alinhamento de direção", resumo.getDescricao());
        assertEquals(new BigDecimal("80.00"), resumo.getPreco());
        assertFalse(resumo.getAtivo());
    }

    @Test
    @DisplayName("Deve retornar serviço ativo corretamente")
    void deveRetornarServicoAtivoCorretamente() {
        // Arrange
        ServicoCatalogResponse response = new ServicoCatalogResponse(
                servicoId,
                "Troca de Pneus",
                "SVC-003",
                "Troca de 4 pneus",
                new BigDecimal("400.00"),
                true
        );
        when(catalogServicoClient.buscarPorCodigo(codigo)).thenReturn(response);

        // Act
        Optional<ServicoResumo> result = adapter.buscarPorCodigo(codigo);

        // Assert
        assertTrue(result.isPresent());
        assertTrue(result.get().getAtivo());
    }

    @Test
    @DisplayName("Deve retornar serviço inativo corretamente")
    void deveRetornarServicoInativoCorretamente() {
        // Arrange
        ServicoCatalogResponse response = new ServicoCatalogResponse(
                servicoId,
                "Serviço Descontinuado",
                "SVC-999",
                "Serviço não oferecido mais",
                new BigDecimal("0.00"),
                false
        );
        when(catalogServicoClient.buscarPorCodigo(codigo)).thenReturn(response);

        // Act
        Optional<ServicoResumo> result = adapter.buscarPorCodigo(codigo);

        // Assert
        assertTrue(result.isPresent());
        assertFalse(result.get().getAtivo());
    }
}
