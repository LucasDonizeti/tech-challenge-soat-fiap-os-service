package com.techchallenge.os.infrastructure.web.controllers;

import com.techchallenge.os.application.commands.AprovarOrcamentoCommand;
import com.techchallenge.os.application.gateway.ports.input.AprovarOrcamentoInput;
import com.techchallenge.os.application.gateway.ports.input.ListarOrdensServicoPorClienteInput;
import com.techchallenge.os.application.gateway.ports.output.ClienteGateway;
import com.techchallenge.os.application.gateway.ports.output.models.ClienteResumo;
import com.techchallenge.os.application.responses.ClienteResponse;
import com.techchallenge.os.application.responses.OrdemServicoResponse;
import com.techchallenge.os.application.responses.VeiculoResponse;
import com.techchallenge.os.infrastructure.web.controllers.utils.PageableValidator;
import com.techchallenge.os.infrastructure.web.dtos.OrdemServicoResponseDto;
import com.techchallenge.os.infrastructure.web.presenters.ClienteOSPresenter;
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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes unitários do controller de cliente")
class ClienteOSControllerTest {

    @Mock
    private AprovarOrcamentoInput aprovarOrcamentoInput;

    @Mock
    private ListarOrdensServicoPorClienteInput listarOrdensServicoPorClienteInput;

    @Mock
    private ClienteOSPresenter presenter;

    @Mock
    private PageableValidator pageableValidator;

    @Mock
    private ClienteGateway clienteGateway;

    @InjectMocks
    private ClienteOSController clienteOSController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(clienteOSController).build();
    }

    @Test
    @DisplayName("Deve listar ordens do cliente logado")
    void deveListarOrdensServicoDoClienteLogadoComSucesso() throws Exception {
        UUID clienteId = UUID.randomUUID();
        String documento = "52998224725";

        ClienteResumo clienteEntity = org.mockito.Mockito.mock(ClienteResumo.class);
        org.mockito.Mockito.when(clienteEntity.getId()).thenReturn(clienteId);
        org.mockito.Mockito.when(clienteEntity.getCpf()).thenReturn(documento);
        when(clienteGateway.findByCPF(documento)).thenReturn(Optional.of(clienteEntity));

        OrdemServicoResponse response = OrdemServicoResponse.builder()
                .id(UUID.randomUUID())
                .status("AGUARDANDO_APROVACAO")
                .valorTotal(new BigDecimal("150.00"))
                .build();

        Page<OrdemServicoResponse> responsePage = new PageImpl<>(List.of(response));
        OrdemServicoResponseDto dto = OrdemServicoResponseDto.builder()
                .id(response.getId())
                .status("AGUARDANDO_APROVACAO")
                .valorTotal(new BigDecimal("150.00"))
                .build();
        Page<OrdemServicoResponseDto> dtoPage = new PageImpl<>(List.of(dto));

        when(listarOrdensServicoPorClienteInput.execute(eq(clienteId), any(Pageable.class))).thenReturn(responsePage);
        when(presenter.prepararViewModelPage(responsePage)).thenReturn(dtoPage);
        when(pageableValidator.validate(any(Pageable.class), any(Set.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(documento, null, List.of(new SimpleGrantedAuthority("ROLE_CLIENTE")))
        );

        try {
            mockMvc.perform(get("/api/cliente/os")
                    .param("page", "0")
                    .param("size", "10")
                    .param("sort", "dataCriacao"))
                    .andExpect(status().isOk());

            verify(listarOrdensServicoPorClienteInput).execute(eq(clienteId), any(Pageable.class));
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    @Test
    @DisplayName("Deve aprovar orçamento com sucesso")
    void deveAprovarOrcamentoComSucesso() throws Exception {
        UUID ordemServicoId = UUID.randomUUID();

        OrdemServicoResponse response = OrdemServicoResponse.builder()
                .id(ordemServicoId)
                .status("EM_EXECUCAO")
                .valorTotal(new BigDecimal("150.00"))
                .cliente(ClienteResponse.builder().nome("João Silva").email("joao@email.com").build())
                .veiculo(VeiculoResponse.builder().placa("ABC1234").marca("Fiat").modelo("Uno").build())
                .build();

        OrdemServicoResponseDto dto = OrdemServicoResponseDto.builder()
                .id(ordemServicoId)
                .status("EM_EXECUCAO")
                .valorTotal(new BigDecimal("150.00"))
                .build();

        when(aprovarOrcamentoInput.execute(any(AprovarOrcamentoCommand.class))).thenReturn(response);
        when(presenter.prepararViewModel(response)).thenReturn(dto);

        mockMvc.perform(post("/api/cliente/os/{osId}/aprovar-orcamento", ordemServicoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ordemServicoId.toString()))
                .andExpect(jsonPath("$.status").value("EM_EXECUCAO"));
    }
}
