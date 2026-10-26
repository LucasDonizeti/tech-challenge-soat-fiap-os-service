package com.techchallenge.os.infrastructure.web.config.gateway;

import com.techchallenge.os.application.gateway.ports.input.*;
import com.techchallenge.os.application.gateway.ports.output.*;
import com.techchallenge.os.application.usecases.*;
import com.techchallenge.os.application.usecases.assemblers.OrdemServicoResponseAssembler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de OSGatewayConfig - Infrastructure Layer")
class OSGatewayConfigTest {

    private OSGatewayConfig osGatewayConfig;

    @Mock
    private OrdemServicoGateway ordemServicoGateway;

    @Mock
    private ClienteGateway clienteGateway;

    @Mock
    private OsEventPublisher osEventPublisher;

    @Mock
    private VeiculoGateway veiculoGateway;

    @Mock
    private ServicoGateway servicoGateway;

    @Mock
    private MroGateway mroGateway;

    @Mock
    private OrdemServicoResponseAssembler ordemServicoResponseAssembler;

    @BeforeEach
    void setUp() {
        osGatewayConfig = new OSGatewayConfig();
    }

    @Test
    @DisplayName("Deve criar bean CriarOrdemServicoInput")
    void deveCriarBeanCriarOrdemServicoInput() {
        // Act
        CriarOrdemServicoInpuit input = osGatewayConfig.criarOrdemServicoInput(
                ordemServicoGateway,
                clienteGateway,
                veiculoGateway,
                servicoGateway,
                mroGateway,
                ordemServicoResponseAssembler
        );

        // Assert
        assertNotNull(input);
        assertTrue(input instanceof CriarOrdemServicoUseCase);
    }

    @Test
    @DisplayName("Deve criar bean ListarOrdensServicoInput")
    void deveCriarBeanListarOrdensServicoInput() {
        // Act
        ListarOrdensServicoInput input = osGatewayConfig.listarOrdensServicoInput(
                ordemServicoGateway,
                servicoGateway,
                mroGateway,
                ordemServicoResponseAssembler
        );

        // Assert
        assertNotNull(input);
        assertTrue(input instanceof ListarOrdensServicoUseCase);
    }

    @Test
    @DisplayName("Deve criar bean BuscarOrdemServicoInput")
    void deveCriarBeanBuscarOrdemServicoInput() {
        // Act
        BuscarOrdemServicoInput input = osGatewayConfig.buscarOrdemServicoInput(
                ordemServicoGateway,
                ordemServicoResponseAssembler
        );

        // Assert
        assertNotNull(input);
        assertTrue(input instanceof BuscarOrdemServicoUseCase);
    }

    @Test
    @DisplayName("Deve criar bean AdicionarServicoOrdemInput")
    void deveCriarBeanAdicionarServicoOrdemInput() {
        // Act
        AdicionarServicoOrdemInput input = osGatewayConfig.adicionarServicoOrdemInput(
                ordemServicoGateway,
                servicoGateway,
                ordemServicoResponseAssembler
        );

        // Assert
        assertNotNull(input);
        assertTrue(input instanceof AdicionarServicoOrdemUseCase);
    }

    @Test
    @DisplayName("Deve criar bean RemoverServicoOrdemInput")
    void deveCriarBeanRemoverServicoOrdemInput() {
        // Act
        RemoverServicoOrdemInput input = osGatewayConfig.removerServicoOrdemInput(
                ordemServicoGateway,
                ordemServicoResponseAssembler
        );

        // Assert
        assertNotNull(input);
        assertTrue(input instanceof RemoverServicoOrdemUseCase);
    }

    @Test
    @DisplayName("Deve criar bean AdicionarMROServicoInput")
    void deveCriarBeanAdicionarMROServicoInput() {
        // Act
        AdicionarMROServicoInput input = osGatewayConfig.adicionarMROServicoInput(
                ordemServicoGateway,
                mroGateway,
                ordemServicoResponseAssembler
        );

        // Assert
        assertNotNull(input);
        assertTrue(input instanceof AdicionarMROServicoUseCase);
    }

    @Test
    @DisplayName("Deve criar bean RemoverMROServicoInput")
    void deveCriarBeanRemoverMROServicoInput() {
        // Act
        RemoverMROServicoInput input = osGatewayConfig.removerMROServicoInput(
                ordemServicoGateway,
                ordemServicoResponseAssembler
        );

        // Assert
        assertNotNull(input);
        assertTrue(input instanceof RemoverMROServicoUseCase);
    }

    @Test
    @DisplayName("Deve criar bean AtualizarQuantidadeMROInput")
    void deveCriarBeanAtualizarQuantidadeMROInput() {
        // Act
        AtualizarQuantidadeMROInput input = osGatewayConfig.atualizarQuantidadeMROInput(
                ordemServicoGateway,
                ordemServicoResponseAssembler
        );

        // Assert
        assertNotNull(input);
        assertTrue(input instanceof AtualizarQuantidadeMROUseCase);
    }

    @Test
    @DisplayName("Deve criar bean EnviarParaDiagnosticoInput")
    void deveCriarBeanEnviarParaDiagnosticoInput() {
        // Act
        EnviarParaDiagnosticoInput input = osGatewayConfig.enviarParaDiagnosticoInput(
                ordemServicoGateway,
                ordemServicoResponseAssembler
        );

        // Assert
        assertNotNull(input);
        assertTrue(input instanceof EnviarParaDiagnosticoUseCase);
    }

    @Test
    @DisplayName("Deve criar bean EnviarOrcamentoAoClienteInput")
    void deveCriarBeanEnviarOrcamentoAoClienteInput() {
        // Act
        EnviarOrcamentoAoClienteInput input = osGatewayConfig.enviarOrcamentoAoClienteInput(
                ordemServicoGateway,
                ordemServicoResponseAssembler,
                clienteGateway,
                osEventPublisher
        );

        // Assert
        assertNotNull(input);
        assertTrue(input instanceof EnviarOrcamentoAoClienteUseCase);
    }

    @Test
    @DisplayName("Deve criar bean AtualizarObservacoesServicoInput")
    void deveCriarBeanAtualizarObservacoesServicoInput() {
        // Act
        AtualizarObservacoesServicoInput input = osGatewayConfig.atualizarObservacoesServicoInput(
                ordemServicoGateway,
                ordemServicoResponseAssembler
        );

        // Assert
        assertNotNull(input);
        assertTrue(input instanceof AtualizarObservacoesServicoUseCase);
    }

    @Test
    @DisplayName("Deve criar bean IniciarServicoInput")
    void deveCriarBeanIniciarServicoInput() {
        // Act
        IniciarServicoInput input = osGatewayConfig.iniciarServicoInput(
                ordemServicoGateway,
                mroGateway,
                ordemServicoResponseAssembler
        );

        // Assert
        assertNotNull(input);
        assertTrue(input instanceof IniciarServicoUseCase);
    }

    @Test
    @DisplayName("Deve criar bean ConcluirServicoInput")
    void deveCriarBeanConcluirServicoInput() {
        // Act
        ConcluirServicoInput input = osGatewayConfig.concluirServicoInput(
                ordemServicoGateway,
                ordemServicoResponseAssembler
        );

        // Assert
        assertNotNull(input);
        assertTrue(input instanceof ConcluirServicoUseCase);
    }

    @Test
    @DisplayName("Deve criar bean CancelarServicoInput")
    void deveCriarBeanCancelarServicoInput() {
        // Act
        CancelarServicoInput input = osGatewayConfig.cancelarServicoInput(
                ordemServicoGateway,
                ordemServicoResponseAssembler
        );

        // Assert
        assertNotNull(input);
        assertTrue(input instanceof CancelarServicoUseCase);
    }

    @Test
    @DisplayName("Deve criar bean EntregarOrdemServicoInput")
    void deveCriarBeanEntregarOrdemServicoInput() {
        // Act
        EntregarOrdemServicoInput input = osGatewayConfig.entregarOrdemServicoInput(
                ordemServicoGateway,
                ordemServicoResponseAssembler
        );

        // Assert
        assertNotNull(input);
        assertTrue(input instanceof EntregarOrdemServicoUseCase);
    }

    @Test
    @DisplayName("Deve criar bean CalcularTempoMedioExecucaoInput")
    void deveCriarBeanCalcularTempoMedioExecucaoInput() {
        // Act
        CalcularTempoMedioExecucaoInput input = osGatewayConfig.calcularTempoMedioExecucaoInput(
                ordemServicoGateway
        );

        // Assert
        assertNotNull(input);
        assertTrue(input instanceof CalcularTempoMedioExecucaoUseCase);
    }

    @Test
    @DisplayName("Deve criar bean AprovarOrcamentoInput")
    void deveCriarBeanAprovarOrcamentoInput() {
        // Act
        AprovarOrcamentoInput input = osGatewayConfig.aprovarOrcamentoInput(
                ordemServicoGateway,
                ordemServicoResponseAssembler
        );

        // Assert
        assertNotNull(input);
        assertTrue(input instanceof AprovarOrcamentoUseCase);
    }

    @Test
    @DisplayName("Deve criar bean RecusarOrcamentoInput")
    void deveCriarBeanRecusarOrcamentoInput() {
        // Act
        RecusarOrcamentoInput input = osGatewayConfig.recusarOrcamentoInput(
                ordemServicoGateway,
                ordemServicoResponseAssembler
        );

        // Assert
        assertNotNull(input);
        assertTrue(input instanceof RecusarOrcamentoUseCase);
    }

    @Test
    @DisplayName("Deve criar bean ListarOrdensServicoPorClienteInput")
    void deveCriarBeanListarOrdensServicoPorClienteInput() {
        // Act
        ListarOrdensServicoPorClienteInput input = osGatewayConfig.listarOrdensServicoPorClienteInput(
                ordemServicoGateway,
                ordemServicoResponseAssembler
        );

        // Assert
        assertNotNull(input);
        assertTrue(input instanceof ListarOrdensServicoPorClienteUseCase);
    }
}
