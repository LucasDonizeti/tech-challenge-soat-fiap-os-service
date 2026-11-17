package com.techchallenge.os.infrastructure.web.dtos;

import com.techchallenge.os.application.responses.*;
import com.techchallenge.os.domain.models.valueobjects.StatusItemServico;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de OrdemServicoResponseDto - Infrastructure Layer")
class OrdemServicoResponseDtoTest {

    @Test
    @DisplayName("Deve converter OrdemServicoResponse para DTO")
    void deveConverterOrdemServicoResponseParaDTO() {
        // Arrange
        UUID id = UUID.randomUUID();
        OrdemServicoResponse response = OrdemServicoResponse.builder()
                .id(id)
                .status("RECEBIDA")
                .dataCriacao(LocalDateTime.now())
                .valorTotal(new BigDecimal("500.00"))
                .cliente(ClienteResponse.builder()
                        .id(UUID.randomUUID())
                        .nome("João Silva")
                        .cpf("12345678909")
                        .email("joao@teste.com")
                        .build())
                .veiculo(VeiculoResponse.builder()
                        .id(UUID.randomUUID())
                        .placa("ABC1234")
                        .marca("Toyota")
                        .modelo("Corolla")
                        .build())
                .itensServico(List.of(
                        ItemServicoResponse.builder()
                                .id(UUID.randomUUID())
                                .servicoId(UUID.randomUUID())
                                .servicoNome("Troca de Óleo")
                                .build()
                ))
                .build();

        // Act
        OrdemServicoResponseDto dto = OrdemServicoResponseDto.from(response);

        // Assert
        assertNotNull(dto);
        assertEquals(id, dto.getId());
        assertEquals("RECEBIDA", dto.getStatus());
        assertNotNull(dto.getCliente());
        assertNotNull(dto.getVeiculo());
        assertEquals(1, dto.getItensServico().size());
    }

    @Test
    @DisplayName("Deve retornar null quando response é nulo")
    void deveRetornarNullQuandoResponseENulo() {
        // Act
        OrdemServicoResponseDto dto = OrdemServicoResponseDto.from(null);

        // Assert
        assertNull(dto);
    }

    @Test
    @DisplayName("Deve converter ClienteResponse para ClienteDto")
    void deveConverterClienteResponseParaClienteDto() {
        // Arrange
        UUID id = UUID.randomUUID();
        ClienteResponse cliente = ClienteResponse.builder()
                .id(id)
                .nome("João Silva")
                .cpf("12345678909")
                .cnpj(null)
                .email("joao@teste.com")
                .tipo("PF")
                .status("ATIVO")
                .build();

        // Act
        OrdemServicoResponseDto.ClienteDto dto = OrdemServicoResponseDto.ClienteDto.from(cliente);

        // Assert
        assertNotNull(dto);
        assertEquals(id, dto.getId());
        assertEquals("João Silva", dto.getNome());
        assertEquals("12345678909", dto.getCpf());
        assertEquals("joao@teste.com", dto.getEmail());
        assertEquals("PF", dto.getTipo());
        assertEquals("ATIVO", dto.getStatus());
    }

    @Test
    @DisplayName("Deve retornar null quando ClienteResponse é nulo")
    void deveRetornarNullQuandoClienteResponseENulo() {
        // Act
        OrdemServicoResponseDto.ClienteDto dto = OrdemServicoResponseDto.ClienteDto.from(null);

        // Assert
        assertNull(dto);
    }

    @Test
    @DisplayName("Deve converter VeiculoResponse para VeiculoDto")
    void deveConverterVeiculoResponseParaVeiculoDto() {
        // Arrange
        UUID id = UUID.randomUUID();
        VeiculoResponse veiculo = VeiculoResponse.builder()
                .id(id)
                .placa("ABC1234")
                .marca("Toyota")
                .modelo("Corolla")
                .ano(2020)
                .cor("Prata")
                .status("ATIVO")
                .build();

        // Act
        OrdemServicoResponseDto.VeiculoDto dto = OrdemServicoResponseDto.VeiculoDto.from(veiculo);

        // Assert
        assertNotNull(dto);
        assertEquals(id, dto.getId());
        assertEquals("ABC1234", dto.getPlaca());
        assertEquals("Toyota", dto.getMarca());
        assertEquals("Corolla", dto.getModelo());
        assertEquals(2020, dto.getAno());
        assertEquals("Prata", dto.getCor());
        assertEquals("ATIVO", dto.getStatus());
    }

    @Test
    @DisplayName("Deve retornar null quando VeiculoResponse é nulo")
    void deveRetornarNullQuandoVeiculoResponseENulo() {
        // Act
        OrdemServicoResponseDto.VeiculoDto dto = OrdemServicoResponseDto.VeiculoDto.from(null);

        // Assert
        assertNull(dto);
    }

    @Test
    @DisplayName("Deve converter ItemServicoResponse para ItemServicoDto")
    void deveConverterItemServicoResponseParaItemServicoDto() {
        // Arrange
        UUID id = UUID.randomUUID();
        ItemServicoResponse item = ItemServicoResponse.builder()
                .id(id)
                .servicoId(UUID.randomUUID())
                .servicoNome("Troca de Óleo")
                .servicoDescricao("Troca completa")
                .status(StatusItemServico.PENDENTE)
                .valorServico(new BigDecimal("150.00"))
                .valorMro(new BigDecimal("50.00"))
                .valorTotal(new BigDecimal("200.00"))
                .mros(List.of())
                .build();

        // Act
        OrdemServicoResponseDto.ItemServicoDto dto = OrdemServicoResponseDto.ItemServicoDto.from(item);

        // Assert
        assertNotNull(dto);
        assertEquals(id, dto.getId());
        assertEquals("Troca de Óleo", dto.getServicoNome());
        assertEquals("Troca completa", dto.getServicoDescricao());
        assertEquals("PENDENTE", dto.getStatus());
        assertEquals(new BigDecimal("150.00"), dto.getValorServico());
        assertEquals(new BigDecimal("50.00"), dto.getValorMro());
        assertEquals(new BigDecimal("200.00"), dto.getValorTotal());
    }

    @Test
    @DisplayName("Deve retornar null quando ItemServicoResponse é nulo")
    void deveRetornarNullQuandoItemServicoResponseENulo() {
        // Act
        OrdemServicoResponseDto.ItemServicoDto dto = OrdemServicoResponseDto.ItemServicoDto.from(null);

        // Assert
        assertNull(dto);
    }

    @Test
    @DisplayName("Deve converter ItemMROResponse para ItemMroDto")
    void deveConverterItemMROResponseParaItemMroDto() {
        // Arrange
        UUID id = UUID.randomUUID();
        ItemMROResponse item = ItemMROResponse.builder()
                .id(id)
                .mroId(UUID.randomUUID())
                .mroNome("Filtro de Óleo")
                .mroDescricao("Filtro para motor")
                .quantidade(2)
                .valorUnitario(new BigDecimal("25.00"))
                .valorTotal(new BigDecimal("50.00"))
                .build();

        // Act
        OrdemServicoResponseDto.ItemMroDto dto = OrdemServicoResponseDto.ItemMroDto.from(item);

        // Assert
        assertNotNull(dto);
        assertEquals(id, dto.getId());
        assertEquals("Filtro de Óleo", dto.getMroNome());
        assertEquals("Filtro para motor", dto.getMroDescricao());
        assertEquals(2, dto.getQuantidade());
        assertEquals(new BigDecimal("25.00"), dto.getValorUnitario());
        assertEquals(new BigDecimal("50.00"), dto.getValorTotal());
    }

    @Test
    @DisplayName("Deve retornar null quando ItemMROResponse é nulo")
    void deveRetornarNullQuandoItemMROResponseENulo() {
        // Act
        OrdemServicoResponseDto.ItemMroDto dto = OrdemServicoResponseDto.ItemMroDto.from(null);

        // Assert
        assertNull(dto);
    }

    @Test
    @DisplayName("Deve lidar com cliente nulo na conversão")
    void deveLidarComClienteNuloNaConversao() {
        // Arrange
        OrdemServicoResponse response = OrdemServicoResponse.builder()
                .id(UUID.randomUUID())
                .status("RECEBIDA")
                .cliente(null)
                .build();

        // Act
        OrdemServicoResponseDto dto = OrdemServicoResponseDto.from(response);

        // Assert
        assertNotNull(dto);
        assertNull(dto.getCliente());
    }

    @Test
    @DisplayName("Deve lidar com veículo nulo na conversão")
    void deveLidarComVeiculoNuloNaConversao() {
        // Arrange
        OrdemServicoResponse response = OrdemServicoResponse.builder()
                .id(UUID.randomUUID())
                .status("RECEBIDA")
                .veiculo(null)
                .build();

        // Act
        OrdemServicoResponseDto dto = OrdemServicoResponseDto.from(response);

        // Assert
        assertNotNull(dto);
        assertNull(dto.getVeiculo());
    }

    @Test
    @DisplayName("Deve lidar com itens de serviço nulos na conversão")
    void deveLidarComItensDeServicoNulosNaConversao() {
        // Arrange
        OrdemServicoResponse response = OrdemServicoResponse.builder()
                .id(UUID.randomUUID())
                .status("RECEBIDA")
                .itensServico(null)
                .build();

        // Act
        OrdemServicoResponseDto dto = OrdemServicoResponseDto.from(response);

        // Assert
        assertNotNull(dto);
        assertNotNull(dto.getItensServico());
        assertTrue(dto.getItensServico().isEmpty());
    }

    @Test
    @DisplayName("Deve lidar com MROs nulos na conversão de item")
    void deveLidarComMROsNulosNaConversaoDeItem() {
        // Arrange
        ItemServicoResponse item = ItemServicoResponse.builder()
                .id(UUID.randomUUID())
                .servicoNome("Serviço")
                .mros(null)
                .build();

        // Act
        OrdemServicoResponseDto.ItemServicoDto dto = OrdemServicoResponseDto.ItemServicoDto.from(item);

        // Assert
        assertNotNull(dto);
        assertNotNull(dto.getMros());
        assertTrue(dto.getMros().isEmpty());
    }

    @Test
    @DisplayName("Deve lidar com status nulo na conversão de item")
    void deveLidarComStatusNuloNaConversaoDeItem() {
        // Arrange
        ItemServicoResponse item = ItemServicoResponse.builder()
                .id(UUID.randomUUID())
                .servicoNome("Serviço")
                .status(null)
                .build();

        // Act
        OrdemServicoResponseDto.ItemServicoDto dto = OrdemServicoResponseDto.ItemServicoDto.from(item);

        // Assert
        assertNotNull(dto);
        assertNull(dto.getStatus());
    }
}
