package com.techchallenge.os.infrastructure.persistence.gateway.adapters;

import com.techchallenge.os.application.gateway.ports.output.models.VeiculoResumo;
import com.techchallenge.os.infrastructure.persistence.gateway.feign.CatalogVeiculoClient;
import com.techchallenge.os.infrastructure.persistence.gateway.feign.dto.VeiculoCatalogResponse;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de VeiculoHttpAdapter - Infrastructure Layer")
class VeiculoHttpAdapterTest {

    @Mock
    private CatalogVeiculoClient catalogVeiculoClient;

    private VeiculoHttpAdapter adapter;

    private UUID veiculoId;
    private String placa;
    private VeiculoCatalogResponse catalogResponse;

    @BeforeEach
    void setUp() {
        adapter = new VeiculoHttpAdapter(catalogVeiculoClient);
        veiculoId = UUID.randomUUID();
        placa = "ABC1234";

        catalogResponse = new VeiculoCatalogResponse(
                veiculoId,
                placa,
                "Toyota",
                "Corolla",
                2022,
                "Prata",
                "ATIVO",
                UUID.randomUUID()
        );
    }

    @Test
    @DisplayName("Deve encontrar veículo por ID com sucesso")
    void deveEncontrarVeiculoPorIdComSucesso() {
        // Arrange
        when(catalogVeiculoClient.buscarPorId(veiculoId)).thenReturn(catalogResponse);

        // Act
        Optional<VeiculoResumo> result = adapter.findById(veiculoId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(veiculoId, result.get().getId());
        assertEquals(placa, result.get().getPlaca());
        assertEquals("Toyota", result.get().getMarca());
        assertEquals("Corolla", result.get().getModelo());
        assertEquals(2022, result.get().getAno());
        assertEquals("Prata", result.get().getCor());
        assertEquals("ATIVO", result.get().getStatus());

        verify(catalogVeiculoClient, times(1)).buscarPorId(veiculoId);
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando veículo não encontrado por ID")
    void deveRetornarOptionalVazioQuandoVeiculoNaoEncontradoPorId() {
        // Arrange
        when(catalogVeiculoClient.buscarPorId(veiculoId))
                .thenThrow(FeignException.NotFound.class);

        // Act
        Optional<VeiculoResumo> result = adapter.findById(veiculoId);

        // Assert
        assertFalse(result.isPresent());

        verify(catalogVeiculoClient, times(1)).buscarPorId(veiculoId);
    }

    @Test
    @DisplayName("Deve encontrar veículo por placa com sucesso")
    void deveEncontrarVeiculoPorPlacaComSucesso() {
        // Arrange
        when(catalogVeiculoClient.buscarPorPlaca(placa)).thenReturn(catalogResponse);

        // Act
        Optional<VeiculoResumo> result = adapter.findByPlaca(placa);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(veiculoId, result.get().getId());
        assertEquals(placa, result.get().getPlaca());
        assertEquals("Toyota", result.get().getMarca());
        assertEquals("Corolla", result.get().getModelo());

        verify(catalogVeiculoClient, times(1)).buscarPorPlaca(placa);
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando veículo não encontrado por placa")
    void deveRetornarOptionalVazioQuandoVeiculoNaoEncontradoPorPlaca() {
        // Arrange
        when(catalogVeiculoClient.buscarPorPlaca(placa))
                .thenThrow(FeignException.NotFound.class);

        // Act
        Optional<VeiculoResumo> result = adapter.findByPlaca(placa);

        // Assert
        assertFalse(result.isPresent());

        verify(catalogVeiculoClient, times(1)).buscarPorPlaca(placa);
    }

    @Test
    @DisplayName("Deve lançar exceção quando erro diferente de 404 ocorre ao buscar por ID")
    void deveLancarExcecaoQuandoErroDiferenteDe404OcorreAoBuscarPorId() {
        // Arrange
        when(catalogVeiculoClient.buscarPorId(veiculoId))
                .thenThrow(new FeignException.InternalServerError(
                        "Erro interno",
                        mock(feign.Request.class),
                        null,
                        null));

        // Act & Assert
        assertThrows(FeignException.InternalServerError.class, () -> adapter.findById(veiculoId));

        verify(catalogVeiculoClient, times(1)).buscarPorId(veiculoId);
    }

    @Test
    @DisplayName("Deve lançar exceção quando erro diferente de 404 ocorre ao buscar por placa")
    void deveLancarExcecaoQuandoErroDiferenteDe404OcorreAoBuscarPorPlaca() {
        // Arrange
        when(catalogVeiculoClient.buscarPorPlaca(placa))
                .thenThrow(new FeignException.ServiceUnavailable(
                        "Serviço indisponível",
                        mock(feign.Request.class),
                        null,
                        null));

        // Act & Assert
        assertThrows(FeignException.ServiceUnavailable.class, () -> adapter.findByPlaca(placa));

        verify(catalogVeiculoClient, times(1)).buscarPorPlaca(placa);
    }

    @Test
    @DisplayName("Deve mapear corretamente todos os campos do DTO")
    void deveMapearCorretamenteTodosOsCamposDoDTO() {
        // Arrange
        UUID clienteId = UUID.randomUUID();
        VeiculoCatalogResponse response = new VeiculoCatalogResponse(
                veiculoId,
                "XYZ9876",
                "Honda",
                "Civic",
                2023,
                "Azul",
                "INATIVO",
                clienteId
        );
        when(catalogVeiculoClient.buscarPorId(veiculoId)).thenReturn(response);

        // Act
        Optional<VeiculoResumo> result = adapter.findById(veiculoId);

        // Assert
        assertTrue(result.isPresent());
        VeiculoResumo resumo = result.get();
        assertEquals(veiculoId, resumo.getId());
        assertEquals("XYZ9876", resumo.getPlaca());
        assertEquals("Honda", resumo.getMarca());
        assertEquals("Civic", resumo.getModelo());
        assertEquals(2023, resumo.getAno());
        assertEquals("Azul", resumo.getCor());
        assertEquals("INATIVO", resumo.getStatus());
        assertEquals(clienteId, resumo.getClienteId());
    }
}
