package com.techchallenge.os.infrastructure.web.presenters;

import com.techchallenge.os.application.responses.ClienteResponse;
import com.techchallenge.os.application.responses.ItemServicoResponse;
import com.techchallenge.os.application.responses.OrdemServicoResponse;
import com.techchallenge.os.application.responses.VeiculoResponse;
import com.techchallenge.os.domain.models.valueobjects.StatusItemServico;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de OrdemServicoPresenter - Infrastructure Layer")
class OrdemServicoPresenterTest {

    @InjectMocks
    private OrdemServicoPresenter presenter;

    private OrdemServicoResponse ordemServicoResponse;

    @BeforeEach
    void setUp() {
        UUID ordemId = UUID.randomUUID();
        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();

        ClienteResponse cliente = ClienteResponse.builder()
                .id(clienteId)
                .nome("João Silva")
                .cpf("52998224725")
                .email("joao@email.com")
                .tipo("PESSOA_FISICA")
                .status("ATIVO")
                .build();

        VeiculoResponse veiculo = VeiculoResponse.builder()
                .id(veiculoId)
                .placa("ABC1234")
                .marca("Fiat")
                .modelo("Uno")
                .ano(2020)
                .cor("Branco")
                .status("ATIVO")
                .build();

        ItemServicoResponse item = ItemServicoResponse.builder()
                .id(UUID.randomUUID())
                .servicoId(UUID.randomUUID())
                .servicoNome("Troca de Óleo")
                .servicoDescricao("Troca completa de óleo")
                .status(StatusItemServico.PENDENTE)
                .valorServico(new BigDecimal("100.00"))
                .valorMro(BigDecimal.ZERO)
                .valorTotal(new BigDecimal("100.00"))
                .dataInicioExecucao(LocalDateTime.now())
                .dataFinalizacao(null)
                .build();

        ordemServicoResponse = OrdemServicoResponse.builder()
                .id(ordemId)
                .cliente(cliente)
                .veiculo(veiculo)
                .status("RECEBIDA")
                .valorTotal(new BigDecimal("100.00"))
                .itensServico(List.of(item))
                .build();
    }

    @Test
    @DisplayName("Deve preparar view model com sucesso")
    void devePrepararViewModelComSucesso() {
        // Act
        var dto = presenter.prepararViewModel(ordemServicoResponse);

        // Assert
        assertNotNull(dto);
        assertEquals(ordemServicoResponse.getId(), dto.getId());
        assertNotNull(dto.getCliente());
        assertNotNull(dto.getVeiculo());
        assertEquals("RECEBIDA", dto.getStatus());
    }

    @Test
    @DisplayName("Deve retornar null quando response é null")
    void deveRetornarNullQuandoResponseENull() {
        // Act
        var dto = presenter.prepararViewModel(null);

        // Assert
        assertNull(dto);
    }

    @Test
    @DisplayName("Deve formatar status com underscores")
    void deveFormatarStatusComUnderscores() {
        // Arrange
        ordemServicoResponse.setStatus("EM_DIAGNOSTICO");

        // Act
        var dto = presenter.prepararViewModel(ordemServicoResponse);

        // Assert
        assertEquals("EM DIAGNOSTICO", dto.getStatus());
    }

    @Test
    @DisplayName("Deve formatar status AGUARDANDO_APROVACAO")
    void deveFormatarStatusAguardandoAprovacao() {
        // Arrange
        ordemServicoResponse.setStatus("AGUARDANDO_APROVACAO");

        // Act
        var dto = presenter.prepararViewModel(ordemServicoResponse);

        // Assert
        assertEquals("AGUARDANDO APROVACAO", dto.getStatus());
    }

    @Test
    @DisplayName("Deve formatar status EM_EXECUCAO")
    void deveFormatarStatusEmExecucao() {
        // Arrange
        ordemServicoResponse.setStatus("EM_EXECUCAO");

        // Act
        var dto = presenter.prepararViewModel(ordemServicoResponse);

        // Assert
        assertEquals("EM EXECUCAO", dto.getStatus());
    }

    @Test
    @DisplayName("Deve manter status null quando status é null")
    void deveManterStatusNullQuandoStatusENull() {
        // Arrange
        ordemServicoResponse.setStatus(null);

        // Act
        var dto = presenter.prepararViewModel(ordemServicoResponse);

        // Assert
        assertNull(dto.getStatus());
    }

    @Test
    @DisplayName("Deve preparar view model page com sucesso")
    void devePrepararViewModelPageComSucesso() {
        // Arrange
        var page = new org.springframework.data.domain.PageImpl<>(List.of(ordemServicoResponse));

        // Act
        var dtoPage = presenter.prepararViewModelPage(page);

        // Assert
        assertNotNull(dtoPage);
        assertEquals(1, dtoPage.getTotalElements());
        assertEquals(1, dtoPage.getContent().size());
        assertEquals("RECEBIDA", dtoPage.getContent().get(0).getStatus());
    }

    @Test
    @DisplayName("Deve retornar page vazia quando response page é null")
    void deveRetornarPageVaziaQuandoResponsePageENull() {
        // Act
        var dtoPage = presenter.prepararViewModelPage(null);

        // Assert
        assertNotNull(dtoPage);
        assertTrue(dtoPage.isEmpty());
    }

    @Test
    @DisplayName("Deve formatar status FINALIZADA")
    void deveFormatarStatusFinalizada() {
        // Arrange
        ordemServicoResponse.setStatus("FINALIZADA");

        // Act
        var dto = presenter.prepararViewModel(ordemServicoResponse);

        // Assert
        assertEquals("FINALIZADA", dto.getStatus());
    }

    @Test
    @DisplayName("Deve formatar status ENTREGUE")
    void deveFormatarStatusEntregue() {
        // Arrange
        ordemServicoResponse.setStatus("ENTREGUE");

        // Act
        var dto = presenter.prepararViewModel(ordemServicoResponse);

        // Assert
        assertEquals("ENTREGUE", dto.getStatus());
    }

    @Test
    @DisplayName("Deve formatar status CANCELADA")
    void deveFormatarStatusCancelada() {
        // Arrange
        ordemServicoResponse.setStatus("CANCELADA");

        // Act
        var dto = presenter.prepararViewModel(ordemServicoResponse);

        // Assert
        assertEquals("CANCELADA", dto.getStatus());
    }
}
