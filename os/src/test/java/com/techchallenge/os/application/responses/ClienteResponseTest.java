package com.techchallenge.os.application.responses;

import com.techchallenge.os.application.gateway.ports.output.models.ClienteResumo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de ClienteResponse - Application Layer")
class ClienteResponseTest {

    @Test
    @DisplayName("Deve criar ClienteResponse a partir de ClienteResumo com CPF")
    void deveCriarClienteResponseAPartirDeClienteResumoComCPF() {
        // Arrange
        UUID id = UUID.randomUUID();
        ClienteResumo clienteResumo = ClienteResumo.builder()
                .id(id)
                .nome("João Silva")
                .cpf("52998224725")
                .cnpj(null)
                .email("joao@email.com")
                .status("ATIVO")
                .build();

        // Act
        ClienteResponse response = ClienteResponse.from(clienteResumo);

        // Assert
        assertNotNull(response);
        assertEquals(id, response.getId());
        assertEquals("João Silva", response.getNome());
        assertEquals("52998224725", response.getCpf());
        assertNull(response.getCnpj());
        assertEquals("joao@email.com", response.getEmail());
        assertEquals("PESSOA_FISICA", response.getTipo());
        assertEquals("ATIVO", response.getStatus());
    }

    @Test
    @DisplayName("Deve criar ClienteResponse a partir de ClienteResumo com CNPJ")
    void deveCriarClienteResponseAPartirDeClienteResumoComCNPJ() {
        // Arrange
        UUID id = UUID.randomUUID();
        ClienteResumo clienteResumo = ClienteResumo.builder()
                .id(id)
                .nome("Empresa Ltda")
                .cpf(null)
                .cnpj("12345678000190")
                .email("contato@empresa.com")
                .status("ATIVO")
                .build();

        // Act
        ClienteResponse response = ClienteResponse.from(clienteResumo);

        // Assert
        assertNotNull(response);
        assertEquals(id, response.getId());
        assertEquals("Empresa Ltda", response.getNome());
        assertNull(response.getCpf());
        assertEquals("12345678000190", response.getCnpj());
        assertEquals("contato@empresa.com", response.getEmail());
        assertEquals("PESSOA_JURIDICA", response.getTipo());
        assertEquals("ATIVO", response.getStatus());
    }

    @Test
    @DisplayName("Deve criar ClienteResponse com tipo null quando não tem CPF nem CNPJ")
    void deveCriarClienteResponseComTipoNullQuandoNaoTemCPFNemCNPJ() {
        // Arrange
        UUID id = UUID.randomUUID();
        ClienteResumo clienteResumo = ClienteResumo.builder()
                .id(id)
                .nome("Cliente Sem Documento")
                .cpf(null)
                .cnpj(null)
                .email("cliente@email.com")
                .status("ATIVO")
                .build();

        // Act
        ClienteResponse response = ClienteResponse.from(clienteResumo);

        // Assert
        assertNotNull(response);
        assertEquals(id, response.getId());
        assertEquals("Cliente Sem Documento", response.getNome());
        assertNull(response.getCpf());
        assertNull(response.getCnpj());
        assertEquals("cliente@email.com", response.getEmail());
        assertNull(response.getTipo());
        assertEquals("ATIVO", response.getStatus());
    }

    @Test
    @DisplayName("Deve retornar null quando ClienteResumo é null")
    void deveRetornarNullQuandoClienteResumoENull() {
        // Act
        ClienteResponse response = ClienteResponse.from(null);

        // Assert
        assertNull(response);
    }

    @Test
    @DisplayName("Deve criar ClienteResponse usando builder")
    void deveCriarClienteResponseUsandoBuilder() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act
        ClienteResponse response = ClienteResponse.builder()
                .id(id)
                .nome("Maria Santos")
                .cpf("98765432100")
                .cnpj(null)
                .email("maria@email.com")
                .tipo("PESSOA_FISICA")
                .status("ATIVO")
                .build();

        // Assert
        assertNotNull(response);
        assertEquals(id, response.getId());
        assertEquals("Maria Santos", response.getNome());
        assertEquals("98765432100", response.getCpf());
        assertEquals("maria@email.com", response.getEmail());
        assertEquals("PESSOA_FISICA", response.getTipo());
        assertEquals("ATIVO", response.getStatus());
    }

    @Test
    @DisplayName("Deve criar ClienteResponse usando construtor vazio e setters")
    void deveCriarClienteResponseUsandoConstrutorVazioESetters() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act
        ClienteResponse response = new ClienteResponse();
        response.setId(id);
        response.setNome("Pedro Oliveira");
        response.setCpf("45678912300");
        response.setEmail("pedro@email.com");
        response.setTipo("PESSOA_FISICA");
        response.setStatus("INATIVO");

        // Assert
        assertNotNull(response);
        assertEquals(id, response.getId());
        assertEquals("Pedro Oliveira", response.getNome());
        assertEquals("45678912300", response.getCpf());
        assertEquals("pedro@email.com", response.getEmail());
        assertEquals("PESSOA_FISICA", response.getTipo());
        assertEquals("INATIVO", response.getStatus());
    }

    @Test
    @DisplayName("Deve criar ClienteResponse usando construtor com todos os argumentos")
    void deveCriarClienteResponseUsandoConstrutorComTodosOsArgumentos() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act
        ClienteResponse response = new ClienteResponse(
                id,
                "Ana Costa",
                "32165498700",
                null,
                "ana@email.com",
                "PESSOA_FISICA",
                "ATIVO"
        );

        // Assert
        assertNotNull(response);
        assertEquals(id, response.getId());
        assertEquals("Ana Costa", response.getNome());
        assertEquals("32165498700", response.getCpf());
        assertEquals("ana@email.com", response.getEmail());
        assertEquals("PESSOA_FISICA", response.getTipo());
        assertEquals("ATIVO", response.getStatus());
    }

    @Test
    @DisplayName("Deve mapear corretamente todos os campos")
    void deveMapearCorretamenteTodosOsCampos() {
        // Arrange
        UUID id = UUID.randomUUID();
        ClienteResumo clienteResumo = ClienteResumo.builder()
                .id(id)
                .nome("Carlos Souza")
                .cpf("12345678901")
                .cnpj("98765432000100")
                .email("carlos@email.com")
                .status("PENDENTE")
                .build();

        // Act
        ClienteResponse response = ClienteResponse.from(clienteResumo);

        // Assert
        assertNotNull(response);
        assertEquals(id, response.getId());
        assertEquals("Carlos Souza", response.getNome());
        assertEquals("12345678901", response.getCpf());
        assertEquals("98765432000100", response.getCnpj());
        assertEquals("carlos@email.com", response.getEmail());
        assertEquals("PESSOA_FISICA", response.getTipo());
        assertEquals("PENDENTE", response.getStatus());
    }
}
