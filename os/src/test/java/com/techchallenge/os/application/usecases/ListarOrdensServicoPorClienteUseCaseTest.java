package com.techchallenge.os.application.usecases;

import com.techchallenge.os.application.gateway.ports.output.OrdemServicoGateway;
import com.techchallenge.os.application.responses.OrdemServicoResponse;
import com.techchallenge.os.application.usecases.assemblers.OrdemServicoResponseAssembler;
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

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de ListarOrdensServicoPorClienteUseCase - Application Layer")
class ListarOrdensServicoPorClienteUseCaseTest {

    @Mock
    private OrdemServicoGateway gateway;

    @Mock
    private OrdemServicoResponseAssembler ordemServicoResponseAssembler;

    @InjectMocks
    private ListarOrdensServicoPorClienteUseCase useCase;

    private UUID clienteId;
    private Pageable pageable;
    private OrdemServico ordemServico1;
    private OrdemServico ordemServico2;
    private OrdemServicoResponse response1;
    private OrdemServicoResponse response2;

    @BeforeEach
    void setUp() {
        clienteId = UUID.randomUUID();
        pageable = PageRequest.of(0, 10);
        ordemServico1 = mock(OrdemServico.class);
        ordemServico2 = mock(OrdemServico.class);
        response1 = mock(OrdemServicoResponse.class);
        response2 = mock(OrdemServicoResponse.class);
    }

    @Test
    @DisplayName("Deve listar ordens de serviço por cliente com sucesso")
    void deveListarOrdensServicoPorClienteComSucesso() {
        // Arrange
        List<OrdemServico> ordensServico = List.of(ordemServico1, ordemServico2);
        Page<OrdemServico> page = new PageImpl<>(ordensServico);

        when(gateway.findByFilters(eq(clienteId), eq(null), eq(null),
                eq(null), eq(null), any(Pageable.class))).thenReturn(page);
        when(ordemServicoResponseAssembler.montar(ordemServico1)).thenReturn(response1);
        when(ordemServicoResponseAssembler.montar(ordemServico2)).thenReturn(response2);

        // Act
        Page<OrdemServicoResponse> response = useCase.execute(clienteId, pageable);

        // Assert
        assertNotNull(response);
        assertEquals(2, response.getTotalElements());
        verify(gateway, times(1)).findByFilters(eq(clienteId), eq(null), eq(null),
                eq(null), eq(null), any(Pageable.class));
        verify(ordemServicoResponseAssembler, times(1)).montar(ordemServico1);
        verify(ordemServicoResponseAssembler, times(1)).montar(ordemServico2);
    }

    @Test
    @DisplayName("Deve lançar exceção quando cliente ID é nulo")
    void deveLancarExcecaoQuandoClienteIdENulo() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> useCase.execute(null, pageable)
        );

        assertEquals("Cliente ID é obrigatório", exception.getMessage());
        verify(gateway, never()).findByFilters(any(), any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("Deve retornar página vazia quando não há ordens de serviço")
    void deveRetornarPaginaVaziaQuandoNaoHaOrdensServico() {
        // Arrange
        Page<OrdemServico> emptyPage = Page.empty();

        when(gateway.findByFilters(eq(clienteId), eq(null), eq(null),
                eq(null), eq(null), any(Pageable.class))).thenReturn(emptyPage);

        // Act
        Page<OrdemServicoResponse> response = useCase.execute(clienteId, pageable);

        // Assert
        assertNotNull(response);
        assertEquals(0, response.getTotalElements());
        verify(gateway, times(1)).findByFilters(eq(clienteId), eq(null), eq(null),
                eq(null), eq(null), any(Pageable.class));
        verify(ordemServicoResponseAssembler, never()).montar(any());
    }

    @Test
    @DisplayName("Deve chamar repository com pageable correto")
    void deveChamarRepositoryComPageableCorreto() {
        // Arrange
        Pageable customPageable = PageRequest.of(2, 20);
        Page<OrdemServico> emptyPage = Page.empty();

        when(gateway.findByFilters(eq(clienteId), eq(null), eq(null),
                eq(null), eq(null), eq(customPageable))).thenReturn(emptyPage);

        // Act
        useCase.execute(clienteId, customPageable);

        // Assert
        verify(gateway, times(1)).findByFilters(eq(clienteId), eq(null), eq(null),
                eq(null), eq(null), eq(customPageable));
        verify(ordemServicoResponseAssembler, never()).montar(any());
    }

    @Test
    @DisplayName("Deve converter OrdemServico para OrdemServicoResponse")
    void deveConverterOrdemServicoParaOrdemServicoResponse() {
        // Arrange
        List<OrdemServico> ordensServico = List.of(ordemServico1);
        Page<OrdemServico> page = new PageImpl<>(ordensServico);

        when(gateway.findByFilters(eq(clienteId), eq(null), eq(null),
                eq(null), eq(null), any(Pageable.class))).thenReturn(page);
        when(ordemServicoResponseAssembler.montar(ordemServico1)).thenReturn(response1);

        // Act
        Page<OrdemServicoResponse> response = useCase.execute(clienteId, pageable);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertNotNull(response.getContent().get(0));
        verify(ordemServicoResponseAssembler, times(1)).montar(ordemServico1);
    }
}
