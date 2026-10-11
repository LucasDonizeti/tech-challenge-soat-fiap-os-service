package com.techchallenge.os.application.usecases;

import com.techchallenge.os.application.commands.CriarOrdemServicoCommand;
import com.techchallenge.os.application.gateway.ports.output.ClienteGateway;
import com.techchallenge.os.application.gateway.ports.output.MroGateway;
import com.techchallenge.os.application.gateway.ports.output.OrdemServicoGateway;
import com.techchallenge.os.application.gateway.ports.output.ServicoGateway;
import com.techchallenge.os.application.gateway.ports.output.VeiculoGateway;
import com.techchallenge.os.application.gateway.ports.output.models.ClienteResumo;
import com.techchallenge.os.application.gateway.ports.output.models.MROResumo;
import com.techchallenge.os.application.gateway.ports.output.models.ServicoResumo;
import com.techchallenge.os.application.gateway.ports.output.models.VeiculoResumo;
import com.techchallenge.os.application.responses.OrdemServicoResponse;
import com.techchallenge.os.application.usecases.assemblers.OrdemServicoResponseAssembler;
import com.techchallenge.os.domain.exceptions.ValidacaoClienteException;
import com.techchallenge.os.domain.exceptions.ValidacaoOrdemServicoException;
import com.techchallenge.os.domain.exceptions.ValidacaoVeiculoException;
import com.techchallenge.os.domain.models.OrdemServico;
import com.techchallenge.os.domain.models.valueobjects.StatusOS;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de CriarOrdemServicoUseCase - Application Layer")
class CriarOrdemServicoUseCaseTest {

    @Mock
    private OrdemServicoGateway ordemServicoGateway;

    @Mock
    private ClienteGateway clienteGateway;

    @Mock
    private VeiculoGateway veiculoGateway;

    @Mock
    private ServicoGateway servicoGateway;

    @Mock
    private MroGateway mroGateway;

    @Mock
    private OrdemServicoResponseAssembler ordemServicoResponseAssembler;

    @InjectMocks
    private CriarOrdemServicoUseCase useCase;

    private CriarOrdemServicoCommand command;
    private ClienteResumo clienteResumo;
    private VeiculoResumo veiculoResumo;
    private ServicoResumo servicoResumo;
    private MROResumo mroResumo;
    private OrdemServico ordemServico;
    private OrdemServicoResponse response;

    private static final String CPF_VALIDO = "52998224725";
    private static final String PLACA_VALIDA = "ABC1234";
    private static final String CODIGO_SERVICO = "SVC-001";

    @BeforeEach
    void setUp() {
        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();
        UUID servicoId = UUID.randomUUID();
        UUID mroId = UUID.randomUUID();

        command = new CriarOrdemServicoCommand(
                CPF_VALIDO, PLACA_VALIDA,
                List.of(CODIGO_SERVICO),
                List.of());

        clienteResumo = ClienteResumo.builder()
                .id(clienteId)
                .nome("João Silva")
                .cpf(CPF_VALIDO)
                .build();

        veiculoResumo = VeiculoResumo.builder()
                .id(veiculoId)
                .placa(PLACA_VALIDA)
                .build();

        servicoResumo = ServicoResumo.builder()
                .id(servicoId)
                .nome("Troca de Óleo")
                .codigo(CODIGO_SERVICO)
                .descricao("Troca completa de óleo")
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

        ordemServico = OrdemServico.criar(clienteId, veiculoId);

        response = OrdemServicoResponse.builder()
                .id(ordemServico.getId())
                .status(StatusOS.RECEBIDA.name())
                .build();
    }

    @Test
    @DisplayName("Deve criar ordem de serviço com sucesso com CPF")
    void deveCriarOrdemServicoComSucessoComCPF() {
        // Arrange
        when(clienteGateway.findByCPF(CPF_VALIDO)).thenReturn(Optional.of(clienteResumo));
        when(veiculoGateway.findByPlaca(PLACA_VALIDA)).thenReturn(Optional.of(veiculoResumo));
        when(servicoGateway.buscarPorCodigo(CODIGO_SERVICO)).thenReturn(Optional.of(servicoResumo));
        when(ordemServicoGateway.save(any(OrdemServico.class))).thenReturn(ordemServico);
        when(ordemServicoResponseAssembler.montar(any(OrdemServico.class))).thenReturn(response);

        // Act
        OrdemServicoResponse result = useCase.execute(command);

        // Assert
        assertNotNull(result);
        assertEquals(ordemServico.getId(), result.getId());
        assertEquals(StatusOS.RECEBIDA, result.getStatus());

        verify(clienteGateway).findByCPF(CPF_VALIDO);
        verify(veiculoGateway).findByPlaca(PLACA_VALIDA);
        verify(servicoGateway).buscarPorCodigo(CODIGO_SERVICO);
        verify(ordemServicoGateway).save(any(OrdemServico.class));
        verify(ordemServicoResponseAssembler).montar(any(OrdemServico.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando cliente não encontrado pelo CPF")
    void deveLancarExcecaoQuandoClienteNaoEncontradoPorCPF() {
        // Arrange
        when(clienteGateway.findByCPF(CPF_VALIDO)).thenReturn(Optional.empty());

        // Act & Assert
        ValidacaoClienteException ex = assertThrows(
                ValidacaoClienteException.class,
                () -> useCase.execute(command));

        assertTrue(ex.getMessage().contains("Cliente não encontrado"));

        verify(clienteGateway).findByCPF(CPF_VALIDO);
        verify(veiculoGateway, never()).findByPlaca(any());
        verify(ordemServicoGateway, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando veículo não encontrado pela placa")
    void deveLancarExcecaoQuandoVeiculoNaoEncontrado() {
        // Arrange
        when(clienteGateway.findByCPF(CPF_VALIDO)).thenReturn(Optional.of(clienteResumo));
        when(veiculoGateway.findByPlaca(PLACA_VALIDA)).thenReturn(Optional.empty());

        // Act & Assert
        ValidacaoVeiculoException ex = assertThrows(
                ValidacaoVeiculoException.class,
                () -> useCase.execute(command));

        assertTrue(ex.getMessage().contains("Veículo não encontrado"));

        verify(veiculoGateway).findByPlaca(PLACA_VALIDA);
        verify(ordemServicoGateway, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando serviço não encontrado pelo código")
    void deveLancarExcecaoQuandoServicoNaoEncontrado() {
        // Arrange
        when(clienteGateway.findByCPF(CPF_VALIDO)).thenReturn(Optional.of(clienteResumo));
        when(veiculoGateway.findByPlaca(PLACA_VALIDA)).thenReturn(Optional.of(veiculoResumo));
        when(servicoGateway.buscarPorCodigo(CODIGO_SERVICO)).thenReturn(Optional.empty());

        // Act & Assert
        ValidacaoOrdemServicoException ex = assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> useCase.execute(command));

        assertTrue(ex.getMessage().contains("Serviço não encontrado"));

        verify(servicoGateway).buscarPorCodigo(CODIGO_SERVICO);
        verify(ordemServicoGateway, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando serviço está inativo")
    void deveLancarExcecaoQuandoServicoInativo() {
        // Arrange
        ServicoResumo servicoInativo = ServicoResumo.builder()
                .id(UUID.randomUUID())
                .nome("Serviço Inativo")
                .codigo(CODIGO_SERVICO)
                .preco(new BigDecimal("100.00"))
                .ativo(false)
                .build();

        when(clienteGateway.findByCPF(CPF_VALIDO)).thenReturn(Optional.of(clienteResumo));
        when(veiculoGateway.findByPlaca(PLACA_VALIDA)).thenReturn(Optional.of(veiculoResumo));
        when(servicoGateway.buscarPorCodigo(CODIGO_SERVICO)).thenReturn(Optional.of(servicoInativo));

        // Act & Assert
        ValidacaoOrdemServicoException ex = assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> useCase.execute(command));

        assertTrue(ex.getMessage().contains("inativo"));

        verify(ordemServicoGateway, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando lista de serviços é vazia")
    void deveLancarExcecaoQuandoListaDeServicosVazia() {
        // Act & Assert — validação acontece no construtor do command
        assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> new CriarOrdemServicoCommand(CPF_VALIDO, PLACA_VALIDA, List.of(), List.of()));

        verify(ordemServicoGateway, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando CPF/CNPJ é inválido")
    void deveLancarExcecaoQuandoCpfCnpjInvalido() {
        // Arrange
        CriarOrdemServicoCommand commandInvalido = new CriarOrdemServicoCommand(
                "123", PLACA_VALIDA, List.of(CODIGO_SERVICO), List.of());

        // Act & Assert — identificação com tamanho inválido
        ValidacaoOrdemServicoException ex = assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> useCase.execute(commandInvalido));

        assertTrue(ex.getMessage().contains("Identificação inválida"));
    }

    @Test
    @DisplayName("Deve propagar exceção quando gateway falha ao salvar")
    void devePropagarExcecaoQuandoGatewayFalha() {
        // Arrange
        when(clienteGateway.findByCPF(CPF_VALIDO)).thenReturn(Optional.of(clienteResumo));
        when(veiculoGateway.findByPlaca(PLACA_VALIDA)).thenReturn(Optional.of(veiculoResumo));
        when(servicoGateway.buscarPorCodigo(CODIGO_SERVICO)).thenReturn(Optional.of(servicoResumo));
        when(ordemServicoGateway.save(any(OrdemServico.class)))
                .thenThrow(new RuntimeException("Erro ao salvar no banco"));

        // Act & Assert
        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> useCase.execute(command));

        assertEquals("Erro ao salvar no banco", ex.getMessage());

        verify(ordemServicoGateway).save(any(OrdemServico.class));
    }

    @Test
    @DisplayName("Deve criar OS com cliente PJ usando CNPJ")
    void deveCriarOsComClientePJ() {
        // Arrange
        String cnpj = "11444777000161";
        ClienteResumo clientePJ = ClienteResumo.builder()
                .id(UUID.randomUUID())
                .nome("Empresa LTDA")
                .cnpj(cnpj)
                .build();

        CriarOrdemServicoCommand commandCNPJ = new CriarOrdemServicoCommand(
                cnpj, PLACA_VALIDA, List.of(CODIGO_SERVICO), List.of());

        when(clienteGateway.findByCNPJ(cnpj)).thenReturn(Optional.of(clientePJ));
        when(veiculoGateway.findByPlaca(PLACA_VALIDA)).thenReturn(Optional.of(veiculoResumo));
        when(servicoGateway.buscarPorCodigo(CODIGO_SERVICO)).thenReturn(Optional.of(servicoResumo));
        when(ordemServicoGateway.save(any(OrdemServico.class))).thenReturn(ordemServico);
        when(ordemServicoResponseAssembler.montar(any(OrdemServico.class))).thenReturn(response);

        // Act
        OrdemServicoResponse result = useCase.execute(commandCNPJ);

        // Assert
        assertNotNull(result);
        verify(clienteGateway).findByCNPJ(cnpj);
        verify(clienteGateway, never()).findByCPF(any());
    }

    @Test
    @DisplayName("Deve criar OS com itens MRO")
    void deveCriarOsComItensMRO() {
        // Arrange
        String codigoMro = "PC001";
        CriarOrdemServicoCommand commandMRO = new CriarOrdemServicoCommand(
                CPF_VALIDO, PLACA_VALIDA,
                List.of(CODIGO_SERVICO),
                List.of(new CriarOrdemServicoCommand.ItemMROCommand(codigoMro, 2)));

        when(clienteGateway.findByCPF(CPF_VALIDO)).thenReturn(Optional.of(clienteResumo));
        when(veiculoGateway.findByPlaca(PLACA_VALIDA)).thenReturn(Optional.of(veiculoResumo));
        when(servicoGateway.buscarPorCodigo(CODIGO_SERVICO)).thenReturn(Optional.of(servicoResumo));
        when(mroGateway.buscarPorCodigo(codigoMro)).thenReturn(Optional.of(mroResumo));
        when(ordemServicoGateway.save(any(OrdemServico.class))).thenReturn(ordemServico);
        when(ordemServicoResponseAssembler.montar(any(OrdemServico.class))).thenReturn(response);

        // Act
        OrdemServicoResponse result = useCase.execute(commandMRO);

        // Assert
        assertNotNull(result);
        verify(mroGateway).buscarPorCodigo(codigoMro);
    }

    @Test
    @DisplayName("Deve lançar exceção quando MRO não encontrado")
    void deveLancarExcecaoQuandoMRONaoEncontrado() {
        // Arrange
        String codigoMro = "PC999";
        CriarOrdemServicoCommand commandMRO = new CriarOrdemServicoCommand(
                CPF_VALIDO, PLACA_VALIDA,
                List.of(CODIGO_SERVICO),
                List.of(new CriarOrdemServicoCommand.ItemMROCommand(codigoMro, 1)));

        when(clienteGateway.findByCPF(CPF_VALIDO)).thenReturn(Optional.of(clienteResumo));
        when(veiculoGateway.findByPlaca(PLACA_VALIDA)).thenReturn(Optional.of(veiculoResumo));
        when(servicoGateway.buscarPorCodigo(CODIGO_SERVICO)).thenReturn(Optional.of(servicoResumo));
        when(mroGateway.buscarPorCodigo(codigoMro)).thenReturn(Optional.empty());

        // Act & Assert
        ValidacaoOrdemServicoException ex = assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> useCase.execute(commandMRO));

        assertTrue(ex.getMessage().contains("MRO não encontrado"));
    }
}
