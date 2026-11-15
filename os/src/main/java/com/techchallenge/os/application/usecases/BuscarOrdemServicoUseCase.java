package com.techchallenge.os.application.usecases;

import com.techchallenge.os.application.gateway.ports.input.BuscarOrdemServicoInput;
import com.techchallenge.os.application.gateway.ports.output.OrdemServicoGateway;
import com.techchallenge.os.application.responses.OrdemServicoResponse;
import com.techchallenge.os.application.usecases.assemblers.OrdemServicoResponseAssembler;
import com.techchallenge.os.domain.exceptions.OrdemServicoNaoEncontradaException;
import com.techchallenge.os.domain.models.OrdemServico;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
public class BuscarOrdemServicoUseCase implements BuscarOrdemServicoInput {
    
    private final OrdemServicoGateway ordemServicoGateway;
    private final OrdemServicoResponseAssembler ordemServicoResponseAssembler;
    
    public OrdemServicoResponse execute(UUID ordemServicoId) {
        log.info("Buscando ordem de serviço: ordemServicoId={}", ordemServicoId);
        
        OrdemServico ordemServico = ordemServicoGateway.findById(ordemServicoId)
                .orElseThrow(() -> new OrdemServicoNaoEncontradaException(ordemServicoId));
        
        log.info("Ordem de serviço encontrada: ID={}, Status={}", ordemServico.getId(), ordemServico.getStatus());

        return ordemServicoResponseAssembler.montar(ordemServico);
    }
}
