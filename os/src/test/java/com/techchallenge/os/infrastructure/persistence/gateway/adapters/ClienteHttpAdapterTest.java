package com.techchallenge.os.infrastructure.persistence.gateway.adapters;

import com.techchallenge.os.application.gateway.ports.output.models.ClienteResumo;
import com.techchallenge.os.infrastructure.persistence.gateway.feign.CatalogClienteClient;
import com.techchallenge.os.infrastructure.persistence.gateway.feign.dto.ClienteCatalogResponse;
import com.techchallenge.os.infrastructure.persistence.gateway.feign.dto.PageResponse;
import feign.FeignException;
import feign.Request;
import feign.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de ClienteHttpAdapter - Infrastructure Layer")
class ClienteHttpAdapterTest {

    @Mock
    private CatalogClienteClient catalogClienteClient;

    @InjectMocks
    private ClienteHttpAdapter clienteHttpAdapter;

    private UUID clienteId;
    private ClienteCatalogResponse clienteCatalogResponse;
    private PageResponse<ClienteCatalogResponse> pageResponse;

    @BeforeEach
    void setUp() {
        clienteId = UUID.randomUUID();
        clienteCatalogResponse = new ClienteCatalogResponse(
                clienteId,
                "João Silva",
                "52998224725",
                null,
                "joao@email.com",
                "ATIVO"
        );
        pageResponse = new PageResponse<>(
                List.of(clienteCatalogResponse),
                1,
                1,
                0,
                1
        );
    }

    @Test
    @DisplayName("Deve buscar cliente por ID com sucesso")
    void deveBuscarClientePorIdComSucesso() {
        // Arrange
        when(catalogClienteClient.buscarPorId(clienteId)).thenReturn(clienteCatalogResponse);

        // Act
        Optional<ClienteResumo> result = clienteHttpAdapter.findById(clienteId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(clienteId, result.get().getId());
        assertEquals("João Silva", result.get().getNome());
        assertEquals("52998224725", result.get().getCpf());
        assertEquals("joao@email.com", result.get().getEmail());
        assertEquals("ATIVO", result.get().getStatus());
        verify(catalogClienteClient).buscarPorId(clienteId);
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando cliente não encontrado por ID")
    void deveRetornarOptionalVazioQuandoClienteNaoEncontradoPorId() {
        // Arrange
        Request request = Request.create(
                Request.HttpMethod.GET,
                "http://localhost:8080/v1/admin/clientes/" + clienteId,
                Collections.emptyMap(),
                Request.Body.empty(),
                null
        );
        Response response = Response.builder()
                .status(404)
                .reason("Not Found")
                .request(request)
                .headers(Collections.emptyMap())
                .body(String.valueOf(new byte[0]), StandardCharsets.UTF_8)
                .build();
        when(catalogClienteClient.buscarPorId(clienteId))
                .thenThrow(FeignException.NotFound.class);

        // Act
        Optional<ClienteResumo> result = clienteHttpAdapter.findById(clienteId);

        // Assert
        assertFalse(result.isPresent());
        verify(catalogClienteClient).buscarPorId(clienteId);
    }

    @Test
    @DisplayName("Deve buscar cliente por CPF com sucesso")
    void deveBuscarClientePorCPFComSucesso() {
        // Arrange
        String cpf = "52998224725";
        when(catalogClienteClient.buscarPorFiltro(eq(cpf), eq(null), eq(0), eq(1)))
                .thenReturn(pageResponse);

        // Act
        Optional<ClienteResumo> result = clienteHttpAdapter.findByCPF(cpf);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(clienteId, result.get().getId());
        assertEquals("João Silva", result.get().getNome());
        assertEquals(cpf, result.get().getCpf());
        verify(catalogClienteClient).buscarPorFiltro(eq(cpf), eq(null), eq(0), eq(1));
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando CPF não encontrado")
    void deveRetornarOptionalVazioQuandoCPFNaoEncontrado() {
        // Arrange
        String cpf = "52998224725";
        when(catalogClienteClient.buscarPorFiltro(eq(cpf), eq(null), eq(0), eq(1)))
                .thenReturn(new PageResponse<>(List.of(), 0, 0, 0, 1));

        // Act
        Optional<ClienteResumo> result = clienteHttpAdapter.findByCPF(cpf);

        // Assert
        assertFalse(result.isPresent());
        verify(catalogClienteClient).buscarPorFiltro(eq(cpf), eq(null), eq(0), eq(1));
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando ocorre erro ao buscar por CPF")
    void deveRetornarOptionalVazioQuandoOcorreErroAoBuscarPorCPF() {
        // Arrange
        String cpf = "52998224725";
        Request request = Request.create(
                Request.HttpMethod.GET,
                "http://localhost:8080/v1/admin/clientes/buscar",
                Collections.emptyMap(),
                Request.Body.empty(),
                null
        );
        Response response = Response.builder()
                .status(500)
                .reason("Internal Server Error")
                .request(request)
                .headers(Collections.emptyMap())
                .body(String.valueOf(new byte[0]), StandardCharsets.UTF_8)
                .build();
        when(catalogClienteClient.buscarPorFiltro(eq(cpf), eq(null), eq(0), eq(1)))
                .thenThrow(new FeignException.InternalServerError("Server error", request, null, Collections.emptyMap()));

        // Act
        Optional<ClienteResumo> result = clienteHttpAdapter.findByCPF(cpf);

        // Assert
        assertFalse(result.isPresent());
        verify(catalogClienteClient).buscarPorFiltro(eq(cpf), eq(null), eq(0), eq(1));
    }

    @Test
    @DisplayName("Deve buscar cliente por CNPJ com sucesso")
    void deveBuscarClientePorCNPJComSucesso() {
        // Arrange
        String cnpj = "12345678000190";
        ClienteCatalogResponse clientePJ = new ClienteCatalogResponse(
                clienteId,
                "Empresa Ltda",
                null,
                cnpj,
                "contato@empresa.com",
                "ATIVO"
        );
        PageResponse<ClienteCatalogResponse> pagePJ = new PageResponse<>(
                List.of(clientePJ),
                1,
                1,
                0,
                1
        );
        when(catalogClienteClient.buscarPorFiltro(eq(null), eq(cnpj), eq(0), eq(1)))
                .thenReturn(pagePJ);

        // Act
        Optional<ClienteResumo> result = clienteHttpAdapter.findByCNPJ(cnpj);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(clienteId, result.get().getId());
        assertEquals("Empresa Ltda", result.get().getNome());
        assertEquals(cnpj, result.get().getCnpj());
        assertEquals("contato@empresa.com", result.get().getEmail());
        verify(catalogClienteClient).buscarPorFiltro(eq(null), eq(cnpj), eq(0), eq(1));
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando CNPJ não encontrado")
    void deveRetornarOptionalVazioQuandoCNPJNaoEncontrado() {
        // Arrange
        String cnpj = "12345678000190";
        when(catalogClienteClient.buscarPorFiltro(eq(null), eq(cnpj), eq(0), eq(1)))
                .thenReturn(new PageResponse<>(List.of(), 0, 0, 0, 1));

        // Act
        Optional<ClienteResumo> result = clienteHttpAdapter.findByCNPJ(cnpj);

        // Assert
        assertFalse(result.isPresent());
        verify(catalogClienteClient).buscarPorFiltro(eq(null), eq(cnpj), eq(0), eq(1));
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando ocorre erro ao buscar por CNPJ")
    void deveRetornarOptionalVazioQuandoOcorreErroAoBuscarPorCNPJ() {
        // Arrange
        String cnpj = "12345678000190";
        Request request = Request.create(
                Request.HttpMethod.GET,
                "http://localhost:8080/v1/admin/clientes/buscar",
                Collections.emptyMap(),
                Request.Body.empty(),
                null
        );
        Response response = Response.builder()
                .status(500)
                .reason("Internal Server Error")
                .request(request)
                .headers(Collections.emptyMap())
                .body(String.valueOf(new byte[0]), StandardCharsets.UTF_8)
                .build();
        when(catalogClienteClient.buscarPorFiltro(eq(null), eq(cnpj), eq(0), eq(1)))
                .thenThrow(new FeignException.InternalServerError("Server error", request, null, Collections.emptyMap()));

        // Act
        Optional<ClienteResumo> result = clienteHttpAdapter.findByCNPJ(cnpj);

        // Assert
        assertFalse(result.isPresent());
        verify(catalogClienteClient).buscarPorFiltro(eq(null), eq(cnpj), eq(0), eq(1));
    }

    @Test
    @DisplayName("Deve mapear corretamente todos os campos do ClienteCatalogResponse")
    void deveMapearCorretamenteTodosOsCamposDoClienteCatalogResponse() {
        // Arrange
        UUID id = UUID.randomUUID();
        ClienteCatalogResponse response = new ClienteCatalogResponse(
                id,
                "Maria Santos",
                "98765432100",
                "11223344000155",
                "maria@email.com",
                "INATIVO"
        );
        when(catalogClienteClient.buscarPorId(id)).thenReturn(response);

        // Act
        Optional<ClienteResumo> result = clienteHttpAdapter.findById(id);

        // Assert
        assertTrue(result.isPresent());
        ClienteResumo resumo = result.get();
        assertEquals(id, resumo.getId());
        assertEquals("Maria Santos", resumo.getNome());
        assertEquals("98765432100", resumo.getCpf());
        assertEquals("11223344000155", resumo.getCnpj());
        assertEquals("maria@email.com", resumo.getEmail());
        assertEquals("INATIVO", resumo.getStatus());
    }
}
