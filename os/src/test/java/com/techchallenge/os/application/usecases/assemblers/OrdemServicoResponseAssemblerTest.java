package com.techchallenge.os.application.usecases.assemblers;

import com.techchallenge.os.application.gateway.ports.output.ClienteGateway;
import com.techchallenge.os.application.gateway.ports.output.VeiculoGateway;
import com.techchallenge.os.application.gateway.ports.output.models.ClienteResumo;
import com.techchallenge.os.application.gateway.ports.output.models.VeiculoResumo;
import com.techchallenge.os.application.responses.OrdemServicoResponse;
import com.techchallenge.os.domain.models.OrdemServico;
import com.techchallenge.os.domain.models.valueobjects.StatusOS;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de OrdemServicoResponseAssembler - Application Layer")
class OrdemServicoResponseAssemblerTest {

    @Mock
    private ClienteGateway clienteGateway;

    @Mock
    private VeiculoGateway veiculoGateway;

    @InjectMocks
    private OrdemServicoResponseAssembler assembler;

    private OrdemServico ordemServico;
    private ClienteResumo clienteResumo;
    private VeiculoResumo veiculoResumo;

    @BeforeEach
    void setUp() {
        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();
        UUID ordemId = UUID.randomUUID();
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

        clienteResumo = ClienteResumo.builder()
                .id(clienteId)
                .nome("João Silva")
                .cpf("52998224725")
                .email("joao@email.com")
                .status("ATIVO")
                .build();

        veiculoResumo = VeiculoResumo.builder()
                .id(veiculoId)
                .placa("ABC1234")
                .marca("Fiat")
                .modelo("Uno")
                .ano(2020)
                .cor("Branco")
                .build();
    }

    @Test
    @DisplayName("Deve montar OrdemServicoResponse com sucesso")
    void deveMontarOrdemServicoResponseComSucesso() {
        // Arrange
        when(clienteGateway.findById(any(UUID.class))).thenReturn(Optional.of(clienteResumo));
        when(veiculoGateway.findById(any(UUID.class))).thenReturn(Optional.of(veiculoResumo));

        // Act
        OrdemServicoResponse response = assembler.montar(ordemServico);

        // Assert
        assertNotNull(response);
        assertEquals(ordemServico.getId(), response.getId());
        assertNotNull(response.getCliente());
        assertEquals("João Silva", response.getCliente().getNome());
        assertNotNull(response.getVeiculo());
        assertEquals("ABC1234", response.getVeiculo().getPlaca());
        assertEquals("RECEBIDA", response.getStatus());

        verify(clienteGateway).findById(ordemServico.getCliente());
        verify(veiculoGateway).findById(ordemServico.getVeiculo());
    }

    @Test
    @DisplayName("Deve montar OrdemServicoResponse quando cliente não encontrado")
    void deveMontarOrdemServicoResponseQuandoClienteNaoEncontrado() {
        // Arrange
        when(clienteGateway.findById(any(UUID.class))).thenReturn(Optional.empty());
        when(veiculoGateway.findById(any(UUID.class))).thenReturn(Optional.of(veiculoResumo));

        // Act
        OrdemServicoResponse response = assembler.montar(ordemServico);

        // Assert
        assertNotNull(response);
        assertEquals(ordemServico.getId(), response.getId());
        assertNull(response.getCliente());
        assertNotNull(response.getVeiculo());

        verify(clienteGateway).findById(ordemServico.getCliente());
        verify(veiculoGateway).findById(ordemServico.getVeiculo());
    }

    @Test
    @DisplayName("Deve montar OrdemServicoResponse quando veículo não encontrado")
    void deveMontarOrdemServicoResponseQuandoVeiculoNaoEncontrado() {
        // Arrange
        when(clienteGateway.findById(any(UUID.class))).thenReturn(Optional.of(clienteResumo));
        when(veiculoGateway.findById(any(UUID.class))).thenReturn(Optional.empty());

        // Act
        OrdemServicoResponse response = assembler.montar(ordemServico);

        // Assert
        assertNotNull(response);
        assertEquals(ordemServico.getId(), response.getId());
        assertNotNull(response.getCliente());
        assertNull(response.getVeiculo());

        verify(clienteGateway).findById(ordemServico.getCliente());
        verify(veiculoGateway).findById(ordemServico.getVeiculo());
    }

    @Test
    @DisplayName("Deve montar OrdemServicoResponse quando ambos não encontrados")
    void deveMontarOrdemServicoResponseQuandoAmbosNaoEncontrados() {
        // Arrange
        when(clienteGateway.findById(any(UUID.class))).thenReturn(Optional.empty());
        when(veiculoGateway.findById(any(UUID.class))).thenReturn(Optional.empty());

        // Act
        OrdemServicoResponse response = assembler.montar(ordemServico);

        // Assert
        assertNotNull(response);
        assertEquals(ordemServico.getId(), response.getId());
        assertNull(response.getCliente());
        assertNull(response.getVeiculo());

        verify(clienteGateway).findById(ordemServico.getCliente());
        verify(veiculoGateway).findById(ordemServico.getVeiculo());
    }

    @Test
    @DisplayName("Deve chamar gateways com IDs corretos")
    void deveChamarGatewaysComIDsCorretos() {
        // Arrange
        when(clienteGateway.findById(any(UUID.class))).thenReturn(Optional.of(clienteResumo));
        when(veiculoGateway.findById(any(UUID.class))).thenReturn(Optional.of(veiculoResumo));

        // Act
        assembler.montar(ordemServico);

        // Assert
        verify(clienteGateway).findById(ordemServico.getCliente());
        verify(veiculoGateway).findById(ordemServico.getVeiculo());
    }
}
