package com.techchallenge.os.application.usecases;


import com.techchallenge.os.application.gateway.ports.input.ListarOrdensServicoPorClienteInput;
import com.techchallenge.os.application.gateway.ports.output.OrdemServicoGateway;
import com.techchallenge.os.application.responses.OrdemServicoResponse;
import com.techchallenge.os.application.usecases.assemblers.OrdemServicoResponseAssembler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;

import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
public class ListarOrdensServicoPorClienteUseCase implements ListarOrdensServicoPorClienteInput {
    
    private final OrdemServicoGateway ordemServicoGateway;
    private final OrdemServicoResponseAssembler ordemServicoResponseAssembler;
    
    public Page<OrdemServicoResponse> execute(UUID clienteId, org.springframework.data.domain.Pageable pageable) {
        log.info("Listando ordens de serviço por cliente: clienteId={}", clienteId);
        
        if (clienteId == null) {
            throw new IllegalArgumentException("Cliente ID é obrigatório");
        }
        
        Page<OrdemServicoResponse> responses = ordemServicoGateway
                .findByFilters(clienteId, null, null, null, null, pageable)
                .map(ordemServicoResponseAssembler::montar);
        
        log.info("Listagem concluída: {} ordens de serviço retornadas", responses.getTotalElements());
        
        return responses;
    }
}
