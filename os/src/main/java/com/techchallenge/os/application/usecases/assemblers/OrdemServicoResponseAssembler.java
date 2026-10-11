package com.techchallenge.os.application.usecases.assemblers;

import com.techchallenge.os.application.gateway.ports.output.ClienteGateway;
import com.techchallenge.os.application.gateway.ports.output.VeiculoGateway;
import com.techchallenge.os.application.gateway.ports.output.models.ClienteResumo;
import com.techchallenge.os.application.gateway.ports.output.models.VeiculoResumo;
import com.techchallenge.os.application.responses.OrdemServicoResponse;
import com.techchallenge.os.domain.models.OrdemServico;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OrdemServicoResponseAssembler {
    private final ClienteGateway clienteGateway;
    private final VeiculoGateway veiculoGateway;

    public OrdemServicoResponse montar(OrdemServico ordemServico) {
        ClienteResumo clienteResumo = clienteGateway.findById(ordemServico.getCliente()).orElse(null);

        VeiculoResumo veiculoResumo = veiculoGateway.findById(ordemServico.getVeiculo()).orElse(null);

        return OrdemServicoResponse.from(ordemServico, clienteResumo, veiculoResumo);
    }
}
