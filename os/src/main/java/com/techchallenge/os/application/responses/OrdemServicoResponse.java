package com.techchallenge.os.application.responses;

import com.techchallenge.os.application.gateway.ports.output.models.ClienteResumo;
import com.techchallenge.os.application.gateway.ports.output.models.VeiculoResumo;
import com.techchallenge.os.domain.models.OrdemServico;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrdemServicoResponse {
    
    private UUID id;
    private ClienteResponse cliente;
    private VeiculoResponse veiculo;
    private String status;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataInicioExecucao;
    private LocalDateTime dataFinalizacao;
    private BigDecimal valorTotal;
    private List<ItemServicoResponse> itensServico;
    
    public static OrdemServicoResponse from(OrdemServico ordemServico, ClienteResumo cliente, VeiculoResumo veiculo) {
        if (ordemServico == null) {
            return null;
        }
        
        ClienteResponse clienteResponse = ClienteResponse.from(cliente);
        VeiculoResponse veiculoResponse = VeiculoResponse.from(veiculo);
        
        List<ItemServicoResponse> itensServicoResponses = ordemServico.getItensServico() != null ?
                ordemServico.getItensServico().stream()
                        .map(ItemServicoResponse::from)
                        .collect(Collectors.toList()) :
                List.of();
        
        return OrdemServicoResponse.builder()
                .id(ordemServico.getId())
                .cliente(clienteResponse)
                .veiculo(veiculoResponse)
                .status(ordemServico.getStatus() != null ? ordemServico.getStatus().name() : null)
                .dataCriacao(ordemServico.getDataCriacao())
                .dataInicioExecucao(ordemServico.getDataInicioExecucao())
                .dataFinalizacao(ordemServico.getDataFinalizacao())
                .valorTotal(ordemServico.calcularValorTotal())
                .itensServico(itensServicoResponses)
                .build();
    }
}
