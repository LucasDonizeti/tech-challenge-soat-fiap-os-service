package com.techchallenge.os.application.usecases;

import com.techchallenge.os.application.commands.EntregarOrdemServicoCommand;
import com.techchallenge.os.application.gateway.ports.input.EntregarOrdemServicoInput;
import com.techchallenge.os.application.gateway.ports.output.OrdemServicoGateway;
import com.techchallenge.os.application.responses.OrdemServicoResponse;
import com.techchallenge.os.application.usecases.assemblers.OrdemServicoResponseAssembler;
import com.techchallenge.os.domain.exceptions.OrdemServicoNaoEncontradaException;
import com.techchallenge.os.domain.models.OrdemServico;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class EntregarOrdemServicoUseCase implements EntregarOrdemServicoInput {

    private final OrdemServicoGateway ordemServicoGateway;
    private final OrdemServicoResponseAssembler ordemServicoResponseAssembler;

    public OrdemServicoResponse execute(EntregarOrdemServicoCommand command) {
        log.info("Entregando ordem de serviço: ordemServicoId={}", command.getOrdemServicoId());
        
        // Buscar ordem de serviço
        OrdemServico ordemServico = ordemServicoGateway.findById(command.getOrdemServicoId())
                .orElseThrow(() -> new OrdemServicoNaoEncontradaException(command.getOrdemServicoId()));
        
        // Entregar (validações são feitas no domínio)
        ordemServico.entregar();
        
        // Persistência
        OrdemServico savedOrdemServico = ordemServicoGateway.save(ordemServico);
        
        log.info("Ordem de serviço entregue com sucesso: ID={}, Status={}", 
                savedOrdemServico.getId(), savedOrdemServico.getStatus());

        return ordemServicoResponseAssembler.montar(savedOrdemServico);
    }
}
