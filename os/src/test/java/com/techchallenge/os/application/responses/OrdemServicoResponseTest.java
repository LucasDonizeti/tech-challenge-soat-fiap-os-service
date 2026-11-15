package com.techchallenge.os.application.responses;

import com.techchallenge.os.application.gateway.ports.output.models.ClienteResumo;
import com.techchallenge.os.application.gateway.ports.output.models.VeiculoResumo;
import com.techchallenge.os.domain.models.ItemServico;
import com.techchallenge.os.domain.models.OrdemServico;
import com.techchallenge.os.domain.models.valueobjects.StatusOS;
import com.techchallenge.os.domain.models.valueobjects.StatusItemServico;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de OrdemServicoResponse - Application Layer")
class OrdemServicoResponseTest {

    @Test
    @DisplayName("Deve criar OrdemServicoResponse a partir de OrdemServico")
    void deveCriarOrdemServicoResponseAPartirDeOrdemServico() {
        // Arrange
        UUID ordemId = UUID.randomUUID();
        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();
        LocalDateTime agora = LocalDateTime.now();

        ClienteResumo cliente = ClienteResumo.builder()
                .id(clienteId)
                .nome("João Silva")
                .cpf("52998224725")
                .email("joao@email.com")
                .status("ATIVO")
                .build();

        VeiculoResumo veiculo = VeiculoResumo.builder()
                .id(veiculoId)
                .placa("ABC1234")
                .marca("Fiat")
                .modelo("Uno")
                .ano(2020)
                .cor("Branco")
                .build();

        OrdemServico ordemServico = OrdemServico.reconstruir(
                ordemId,
                clienteId,
                veiculoId,
                StatusOS.RECEBIDA,
                agora,
                null,
                null,
                agora
        );

        // Act
        OrdemServicoResponse response = OrdemServicoResponse.from(ordemServico, cliente, veiculo);

        // Assert
        assertNotNull(response);
        assertEquals(ordemId, response.getId());
        assertNotNull(response.getCliente());
        assertNotNull(response.getVeiculo());
        assertEquals("RECEBIDA", response.getStatus());
        assertNotNull(response.getDataCriacao());
        assertNotNull(response.getItensServico());
    }

    @Test
    @DisplayName("Deve retornar null quando OrdemServico é null")
    void deveRetornarNullQuandoOrdemServicoENull() {
        // Act
        OrdemServicoResponse response = OrdemServicoResponse.from(null, null, null);

        // Assert
        assertNull(response);
    }

    @Test
    @DisplayName("Deve criar OrdemServicoResponse usando builder")
    void deveCriarOrdemServicoResponseUsandoBuilder() {
        // Arrange
        UUID id = UUID.randomUUID();
        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();

        ClienteResponse cliente = ClienteResponse.builder()
                .id(clienteId)
                .nome("Maria Santos")
                .cpf("98765432100")
                .email("maria@email.com")
                .tipo("PESSOA_FISICA")
                .status("ATIVO")
                .build();

        VeiculoResponse veiculo = VeiculoResponse.builder()
                .id(veiculoId)
                .placa("XYZ5678")
                .marca("Toyota")
                .modelo("Corolla")
                .ano(2021)
                .cor("Prata")
                .status("ATIVO")
                .build();

        // Act
        OrdemServicoResponse response = OrdemServicoResponse.builder()
                .id(id)
                .cliente(cliente)
                .veiculo(veiculo)
                .status("EM_EXECUCAO")
                .dataCriacao(LocalDateTime.now())
                .dataInicioExecucao(LocalDateTime.now())
                .dataFinalizacao(null)
                .valorTotal(new BigDecimal("500.00"))
                .itensServico(List.of())
                .build();

        // Assert
        assertNotNull(response);
        assertEquals(id, response.getId());
        assertNotNull(response.getCliente());
        assertNotNull(response.getVeiculo());
        assertEquals("EM_EXECUCAO", response.getStatus());
        assertEquals(new BigDecimal("500.00"), response.getValorTotal());
    }

    @Test
    @DisplayName("Deve criar OrdemServicoResponse usando construtor vazio e setters")
    void deveCriarOrdemServicoResponseUsandoConstrutorVazioESetters() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act
        OrdemServicoResponse response = new OrdemServicoResponse();
        response.setId(id);
        response.setStatus("FINALIZADA");
        response.setDataCriacao(LocalDateTime.now());
        response.setDataInicioExecucao(LocalDateTime.now());
        response.setDataFinalizacao(LocalDateTime.now());
        response.setValorTotal(new BigDecimal("750.00"));
        response.setItensServico(List.of());

        // Assert
        assertNotNull(response);
        assertEquals(id, response.getId());
        assertEquals("FINALIZADA", response.getStatus());
        assertEquals(new BigDecimal("750.00"), response.getValorTotal());
    }

    @Test
    @DisplayName("Deve criar OrdemServicoResponse usando construtor com todos os argumentos")
    void deveCriarOrdemServicoResponseUsandoConstrutorComTodosOsArgumentos() {
        // Arrange
        UUID id = UUID.randomUUID();
        LocalDateTime agora = LocalDateTime.now();

        // Act
        OrdemServicoResponse response = new OrdemServicoResponse(
                id,
                null,
                null,
                "ENTREGUE",
                agora,
                agora,
                agora,
                new BigDecimal("1000.00"),
                List.of()
        );

        // Assert
        assertNotNull(response);
        assertEquals(id, response.getId());
        assertEquals("ENTREGUE", response.getStatus());
        assertEquals(new BigDecimal("1000.00"), response.getValorTotal());
    }

    @Test
    @DisplayName("Deve mapear corretamente cliente e veiculo")
    void deveMapearCorretamenteClienteEVeiculo() {
        // Arrange
        UUID ordemId = UUID.randomUUID();
        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();

        ClienteResumo cliente = ClienteResumo.builder()
                .id(clienteId)
                .nome("Empresa Ltda")
                .cnpj("12345678000190")
                .email("contato@empresa.com")
                .status("ATIVO")
                .build();

        VeiculoResumo veiculo = VeiculoResumo.builder()
                .id(veiculoId)
                .placa("DEF9012")
                .marca("Volkswagen")
                .modelo("Gol")
                .ano(2019)
                .cor("Preto")
                .build();

        OrdemServico ordemServico = OrdemServico.criar(clienteId, veiculoId);

        // Act
        OrdemServicoResponse response = OrdemServicoResponse.from(ordemServico, cliente, veiculo);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getCliente());
        assertEquals("Empresa Ltda", response.getCliente().getNome());
        assertEquals("12345678000190", response.getCliente().getCnpj());
        assertEquals("PESSOA_JURIDICA", response.getCliente().getTipo());
        assertNotNull(response.getVeiculo());
        assertEquals("DEF9012", response.getVeiculo().getPlaca());
        assertEquals("Volkswagen", response.getVeiculo().getMarca());
    }

    @Test
    @DisplayName("Deve mapear itens de serviço quando presentes")
    void deveMapearItensDeServicoQuandoPresentes() {
        // Arrange
        UUID ordemId = UUID.randomUUID();
        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();
        LocalDateTime agora = LocalDateTime.now();

        ClienteResumo cliente = ClienteResumo.builder()
                .id(clienteId)
                .nome("Pedro Oliveira")
                .cpf("45678912300")
                .email("pedro@email.com")
                .status("ATIVO")
                .build();

        VeiculoResumo veiculo = VeiculoResumo.builder()
                .id(veiculoId)
                .placa("GHI3456")
                .marca("Honda")
                .modelo("Civic")
                .ano(2022)
                .cor("Azul")
                .build();

        OrdemServico ordemServico = OrdemServico.reconstruir(
                ordemId,
                clienteId,
                veiculoId,
                StatusOS.RECEBIDA,
                agora,
                null,
                null,
                agora
        );
        UUID servicoId = UUID.randomUUID();
        ItemServico item = ItemServico.reconstruir(
                UUID.randomUUID(),
                servicoId,
                "Troca de Óleo",
                "Troca completa",
                StatusItemServico.PENDENTE,
                null,
                new BigDecimal("150.00"),
                BigDecimal.ZERO,
                LocalDateTime.now(),
                null,
                LocalDateTime.now()
        );
        ordemServico.adicionarItemServicoSemValidacao(item);

        // Act
        OrdemServicoResponse response = OrdemServicoResponse.from(ordemServico, cliente, veiculo);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getItensServico());
        assertEquals(1, response.getItensServico().size());
        assertEquals("Troca de Óleo", response.getItensServico().get(0).getServicoNome());
    }
}
