package com.techchallenge.os.application.usecases;

import com.techchallenge.os.application.commands.RemoverServicoOrdemCommand;
import com.techchallenge.os.application.gateway.ports.input.RemoverServicoOrdemInput;
import com.techchallenge.os.application.gateway.ports.output.OrdemServicoGateway;
import com.techchallenge.os.application.responses.OrdemServicoResponse;
import com.techchallenge.os.application.usecases.assemblers.OrdemServicoResponseAssembler;
import com.techchallenge.os.domain.exceptions.OrdemServicoNaoEncontradaException;
import com.techchallenge.os.domain.models.OrdemServico;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class RemoverServicoOrdemUseCase implements RemoverServicoOrdemInput {
    
    private final OrdemServicoGateway ordemServicoGateway;
    private final OrdemServicoResponseAssembler ordemServicoResponseAssembler;

    public OrdemServicoResponse execute(RemoverServicoOrdemCommand command) {
        log.info("Removendo serviço da ordem de serviço: ordemServicoId={}, itemServicoId={}", 
                command.getOrdemServicoId(), command.getItemServicoId());
        
        // Buscar ordem de serviço
        OrdemServico ordemServico = ordemServicoGateway.findById(command.getOrdemServicoId())
                .orElseThrow(() -> new OrdemServicoNaoEncontradaException(command.getOrdemServicoId()));
        
        // A validação de status é feita dentro do próprio aggregate (removerItemServico)
        // que só permite remover serviços quando a OS está RECEBIDA
        
        // Remover item de serviço (validação acontece dentro do método)
        ordemServico.removerItemServico(command.getItemServicoId());
        
        // Persistência
        OrdemServico savedOrdemServico = ordemServicoGateway.save(ordemServico);
        
        log.info("Serviço removido da ordem de serviço com sucesso: itemServicoId={}", command.getItemServicoId());

        return ordemServicoResponseAssembler.montar(savedOrdemServico);
    }
}
