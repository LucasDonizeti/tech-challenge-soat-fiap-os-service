package com.techchallenge.os.application.usecases;

import com.techchallenge.os.application.commands.RemoverMROServicoCommand;
import com.techchallenge.os.application.gateway.ports.input.RemoverMROServicoInput;
import com.techchallenge.os.application.gateway.ports.output.OrdemServicoGateway;
import com.techchallenge.os.application.responses.OrdemServicoResponse;
import com.techchallenge.os.application.usecases.assemblers.OrdemServicoResponseAssembler;
import com.techchallenge.os.domain.exceptions.ItemServicoNaoEncontradoException;
import com.techchallenge.os.domain.exceptions.OrdemServicoNaoEncontradaException;
import com.techchallenge.os.domain.exceptions.OrdemServicoStatusInvalidoException;
import com.techchallenge.os.domain.models.ItemServico;
import com.techchallenge.os.domain.models.OrdemServico;
import com.techchallenge.os.domain.models.valueobjects.StatusOS;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class RemoverMROServicoUseCase implements RemoverMROServicoInput {
    
    private final OrdemServicoGateway ordemServicoGateway;
    private final OrdemServicoResponseAssembler ordemServicoResponseAssembler;

    public OrdemServicoResponse execute(RemoverMROServicoCommand command) {
        log.info("Removendo MRO do serviço: ordemServicoId={}, itemServicoId={}, itemMroId={}", 
                command.getOrdemServicoId(), command.getItemServicoId(), command.getItemMroId());
        
        // Buscar ordem de serviço
        OrdemServico ordemServico = ordemServicoGateway.findById(command.getOrdemServicoId())
                .orElseThrow(() -> new OrdemServicoNaoEncontradaException(command.getOrdemServicoId()));
        
        // Validar status - permite remover MROs quando a OS está RECEBIDA ou EM_DIAGNOSTICO
        if (ordemServico.getStatus() != StatusOS.RECEBIDA && ordemServico.getStatus() != StatusOS.EM_DIAGNOSTICO) {
            throw new OrdemServicoStatusInvalidoException("Só é possível remover MROs quando a Ordem de Serviço está nos status RECEBIDA ou EM_DIAGNOSTICO. Status atual: " + ordemServico.getStatus());
        }
        
        // Buscar item de serviço
        ItemServico itemServico = ordemServico.getItensServico().stream()
                .filter(item -> item.getId().equals(command.getItemServicoId()))
                .findFirst()
                .orElseThrow(() -> new ItemServicoNaoEncontradoException(command.getItemServicoId()));
        
        // Remover MRO do item de serviço
        itemServico.removerMRO(command.getItemMroId());
        
        // Persistência
        OrdemServico savedOrdemServico = ordemServicoGateway.save(ordemServico);
        
        log.info("MRO removido do serviço com sucesso: itemMroId={}", command.getItemMroId());

        return ordemServicoResponseAssembler.montar(savedOrdemServico);
    }
}
