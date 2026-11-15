package com.techchallenge.os.application.usecases;


import com.techchallenge.os.application.commands.AtualizarObservacoesServicoCommand;
import com.techchallenge.os.application.gateway.ports.input.AtualizarObservacoesServicoInput;
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
public class AtualizarObservacoesServicoUseCase implements AtualizarObservacoesServicoInput {
    
    private final OrdemServicoGateway ordemServicoGateway;
    private final OrdemServicoResponseAssembler ordemServicoResponseAssembler;

    public OrdemServicoResponse execute(AtualizarObservacoesServicoCommand command) {
        log.info("Atualizando observações do serviço: ordemServicoId={}, itemServicoId={}", 
                command.getOrdemServicoId(), command.getItemServicoId());
        
        // Buscar ordem de serviço
        OrdemServico ordemServico = ordemServicoGateway.findById(command.getOrdemServicoId())
                .orElseThrow(() -> new OrdemServicoNaoEncontradaException(command.getOrdemServicoId()));
        
        // Validar status - só permite atualizar observações quando a OS está RECEBIDA ou EM_DIAGNOSTICO
        if (ordemServico.getStatus() != StatusOS.RECEBIDA && ordemServico.getStatus() != StatusOS.EM_DIAGNOSTICO) {
            throw new OrdemServicoStatusInvalidoException("Só é possível atualizar observações quando a Ordem de Serviço está nos status RECEBIDA ou EM_DIAGNOSTICO. Status atual: " + ordemServico.getStatus());
        }
        
        // Buscar item de serviço
        ItemServico itemServico = ordemServico.getItensServico().stream()
                .filter(item -> item.getId().equals(command.getItemServicoId()))
                .findFirst()
                .orElseThrow(() -> new ItemServicoNaoEncontradoException(command.getItemServicoId()));
        
        // Atualizar observações
        itemServico.atualizarObservacoes(command.getObservacoes());
        
        // Persistência
        OrdemServico savedOrdemServico = ordemServicoGateway.save(ordemServico);
        
        log.info("Observações do serviço atualizadas com sucesso: itemServicoId={}", command.getItemServicoId());

        return ordemServicoResponseAssembler.montar(savedOrdemServico);
    }
}
