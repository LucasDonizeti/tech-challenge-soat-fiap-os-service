package com.techchallenge.os.application.usecases;

import com.techchallenge.os.application.gateway.ports.output.MroGateway;
import com.techchallenge.os.application.gateway.ports.output.OrdemServicoGateway;
import com.techchallenge.os.application.gateway.ports.output.ServicoGateway;
import com.techchallenge.os.application.gateway.ports.output.models.MROResumo;
import com.techchallenge.os.application.gateway.ports.output.models.ServicoResumo;
import com.techchallenge.os.application.responses.ItemMROResponse;
import com.techchallenge.os.application.responses.ItemServicoResponse;
import com.techchallenge.os.application.responses.OrdemServicoResponse;
import com.techchallenge.os.application.usecases.assemblers.OrdemServicoResponseAssembler;
import com.techchallenge.os.domain.models.ItemServico;
import com.techchallenge.os.domain.models.OrdemServico;
import com.techchallenge.os.domain.models.valueobjects.StatusOS;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de ListarOrdensServicoUseCase - Application Layer")
class ListarOrdensServicoUseCaseTest {

    @Mock
    private OrdemServicoGateway ordemServicoGateway;

    @Mock
    private ServicoGateway servicoGateway;

    @Mock
    private MroGateway mroGateway;

    @Mock
    private OrdemServicoResponseAssembler ordemServicoResponseAssembler;

    @InjectMocks
    private ListarOrdensServicoUseCase useCase;

    private OrdemServico ordemServico;
    private OrdemServicoResponse response;
    private ServicoResumo servicoResumo;
    private MROResumo mroResumo;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();
        UUID servicoId = UUID.randomUUID();
        UUID mroId = UUID.randomUUID();

        ordemServico = OrdemServico.criar(clienteId, veiculoId);

        ItemServico itemServico = ItemServico.criarComDados(servicoId, "Troca de Óleo", "Troca completa", new BigDecimal("150.00"));
        ordemServico.adicionarItemServico(itemServico);

        response = OrdemServicoResponse.builder()
                .id(ordemServico.getId())
                .status(StatusOS.RECEBIDA.name())
                .itensServico(null)
                .build();

        servicoResumo = ServicoResumo.builder()
                .id(servicoId)
                .nome("Troca de Óleo")
                .codigo("SVC-001")
                .descricao("Troca completa")
                .preco(new BigDecimal("150.00"))
                .ativo(true)
                .build();

        mroResumo = MROResumo.builder()
                .id(mroId)
                .nome("Filtro de Óleo")
                .codigo("PC001")
                .descricao("Filtro para motor")
                .precoUnitario(new BigDecimal("25.00"))
                .ativo(true)
                .build();

        pageable = PageRequest.of(0, 10);
    }

    @Test
    @DisplayName("Deve listar ordens de serviço sem filtros")
    void deveListarOrdensServicoSemFiltros() {
        // Arrange
        Page<OrdemServico> page = new PageImpl<>(List.of(ordemServico));
        when(ordemServicoGateway.findByFilters(null, null, null, null, null, pageable))
                .thenReturn(page);
        when(ordemServicoResponseAssembler.montar(ordemServico)).thenReturn(response);

        // Act
        Page<OrdemServicoResponse> result = useCase.execute(null, null, null, null, null, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(ordemServico.getId(), result.getContent().get(0).getId());

        verify(ordemServicoGateway, times(1)).findByFilters(null, null, null, null, null, pageable);
        verify(ordemServicoResponseAssembler, times(1)).montar(ordemServico);
    }

    @Test
    @DisplayName("Deve listar ordens de serviço com filtro de cliente")
    void deveListarOrdensServicoComFiltroDeCliente() {
        // Arrange
        UUID clienteId = UUID.randomUUID();
        Page<OrdemServico> page = new PageImpl<>(List.of(ordemServico));
        when(ordemServicoGateway.findByFilters(clienteId, null, null, null, null, pageable))
                .thenReturn(page);
        when(ordemServicoResponseAssembler.montar(ordemServico)).thenReturn(response);

        // Act
        Page<OrdemServicoResponse> result = useCase.execute(clienteId, null, null, null, null, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());

        verify(ordemServicoGateway, times(1)).findByFilters(clienteId, null, null, null, null, pageable);
    }

    @Test
    @DisplayName("Deve listar ordens de serviço com filtro de status")
    void deveListarOrdensServicoComFiltroDeStatus() {
        // Arrange
        Page<OrdemServico> page = new PageImpl<>(List.of(ordemServico));
        when(ordemServicoGateway.findByFilters(null, null, StatusOS.RECEBIDA, null, null, pageable))
                .thenReturn(page);
        when(ordemServicoResponseAssembler.montar(ordemServico)).thenReturn(response);

        // Act
        Page<OrdemServicoResponse> result = useCase.execute(null, null, StatusOS.RECEBIDA, null, null, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());

        verify(ordemServicoGateway, times(1)).findByFilters(null, null, StatusOS.RECEBIDA, null, null, pageable);
    }

    @Test
    @DisplayName("Deve listar ordens de serviço com filtro de período")
    void deveListarOrdensServicoComFiltroDePeriodo() {
        // Arrange
        LocalDateTime dataInicio = LocalDateTime.now().minusDays(7);
        LocalDateTime dataFim = LocalDateTime.now();
        Page<OrdemServico> page = new PageImpl<>(List.of(ordemServico));
        when(ordemServicoGateway.findByFilters(null, null, null, dataInicio, dataFim, pageable))
                .thenReturn(page);
        when(ordemServicoResponseAssembler.montar(ordemServico)).thenReturn(response);

        // Act
        Page<OrdemServicoResponse> result = useCase.execute(null, null, null, dataInicio, dataFim, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());

        verify(ordemServicoGateway, times(1)).findByFilters(null, null, null, dataInicio, dataFim, pageable);
    }

    @Test
    @DisplayName("Deve retornar página vazia quando não há ordens de serviço")
    void deveRetornarPaginaVaziaQuandoNaoHaOrdensDeServico() {
        // Arrange
        Page<OrdemServico> page = new PageImpl<>(List.of());
        when(ordemServicoGateway.findByFilters(null, null, null, null, null, pageable))
                .thenReturn(page);

        // Act
        Page<OrdemServicoResponse> result = useCase.execute(null, null, null, null, null, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());

        verify(ordemServicoGateway, times(1)).findByFilters(null, null, null, null, null, pageable);
        verify(ordemServicoResponseAssembler, never()).montar(any());
    }
}
