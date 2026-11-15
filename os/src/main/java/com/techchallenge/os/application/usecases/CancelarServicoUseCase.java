package com.techchallenge.os.application.usecases;


import com.techchallenge.os.application.commands.CancelarServicoCommand;
import com.techchallenge.os.application.gateway.ports.input.CancelarServicoInput;
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
public class CancelarServicoUseCase implements CancelarServicoInput {
    
    private final OrdemServicoGateway ordemServicoGateway;
    private final OrdemServicoResponseAssembler ordemServicoResponseAssembler;

    public OrdemServicoResponse execute(CancelarServicoCommand command) {
        log.info("Cancelando serviço: ordemServicoId={}, itemServicoId={}", 
                command.getOrdemServicoId(), command.getItemServicoId());
        
        // Buscar ordem de serviço
        OrdemServico ordemServico = ordemServicoGateway.findById(command.getOrdemServicoId())
                .orElseThrow(() -> new OrdemServicoNaoEncontradaException(command.getOrdemServicoId()));
        
        // Buscar item de serviço
        ItemServico itemServico = ordemServico.getItensServico().stream()
                .filter(item -> item.getId().equals(command.getItemServicoId()))
                .findFirst()
                .orElseThrow(() -> new ItemServicoNaoEncontradoException(command.getItemServicoId()));
        
        // Validar que item não está CONCLUIDO ou CANCELADO
        if (itemServico.getStatus() == StatusItemServico.CONCLUIDO) {
            throw new ValidacaoEstoqueException("Não é possível cancelar um serviço já concluído");
        }
        
        if (itemServico.getStatus() == StatusItemServico.CANCELADO) {
            throw new ValidacaoEstoqueException("Serviço já está cancelado");
        }
        
        // Atualizar status para CANCELADO
        itemServico.atualizarStatus(StatusItemServico.CANCELADO);
        
        // Persistência
        OrdemServico savedOrdemServico = ordemServicoGateway.save(ordemServico);
        
        log.info("Serviço cancelado com sucesso: itemServicoId={}", itemServico.getId());

        return ordemServicoResponseAssembler.montar(savedOrdemServico);
    }
}
