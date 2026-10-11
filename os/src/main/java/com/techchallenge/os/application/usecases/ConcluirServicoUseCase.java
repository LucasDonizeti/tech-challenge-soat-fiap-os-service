package com.techchallenge.os.application.usecases;

import com.techchallenge.os.application.commands.ConcluirServicoCommand;
import com.techchallenge.os.application.gateway.ports.input.ConcluirServicoInput;
import com.techchallenge.os.application.gateway.ports.output.OrdemServicoGateway;
import com.techchallenge.os.application.responses.OrdemServicoResponse;
import com.techchallenge.os.application.usecases.assemblers.OrdemServicoResponseAssembler;
import com.techchallenge.os.domain.exceptions.ItemServicoNaoEncontradoException;
import com.techchallenge.os.domain.exceptions.OrdemServicoNaoEncontradaException;
import com.techchallenge.os.domain.exceptions.ValidacaoEstoqueException;
import com.techchallenge.os.domain.models.ItemServico;
import com.techchallenge.os.domain.models.OrdemServico;
import com.techchallenge.os.domain.models.valueobjects.StatusItemServico;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class ConcluirServicoUseCase implements ConcluirServicoInput {
    
    private final OrdemServicoGateway ordemServicoGateway;
    private final OrdemServicoResponseAssembler ordemServicoResponseAssembler;

    public OrdemServicoResponse execute(ConcluirServicoCommand command) {
        log.info("Concluindo serviço: ordemServicoId={}, itemServicoId={}", 
                command.getOrdemServicoId(), command.getItemServicoId());
        
        // Buscar ordem de serviço
        OrdemServico ordemServico = ordemServicoGateway.findById(command.getOrdemServicoId())
                .orElseThrow(() -> new OrdemServicoNaoEncontradaException(command.getOrdemServicoId()));
        
        // Buscar item de serviço
        ItemServico itemServico = ordemServico.getItensServico().stream()
                .filter(item -> item.getId().equals(command.getItemServicoId()))
                .findFirst()
                .orElseThrow(() -> new ItemServicoNaoEncontradoException(command.getItemServicoId()));
        
        // Validar que item está EM_ANDAMENTO
        if (itemServico.getStatus() != StatusItemServico.EM_ANDAMENTO) {
            throw new ValidacaoEstoqueException("Só é possível concluir serviços com status EM_ANDAMENTO. Status atual: " + itemServico.getStatus());
        }
        
        // Atualizar status para CONCLUIDO
        itemServico.atualizarStatus(StatusItemServico.CONCLUIDO);
        
        // Persistência
        OrdemServico savedOrdemServico = ordemServicoGateway.save(ordemServico);
        
        log.info("Serviço concluído com sucesso: itemServicoId={}", itemServico.getId());

        return ordemServicoResponseAssembler.montar(savedOrdemServico);
    }
}
