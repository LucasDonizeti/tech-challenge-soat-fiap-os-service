package com.techchallenge.os.application.usecases;

import com.techchallenge.os.application.commands.AtualizarQuantidadeMROCommand;
import com.techchallenge.os.application.gateway.ports.input.AtualizarQuantidadeMROInput;
import com.techchallenge.os.application.gateway.ports.output.OrdemServicoGateway;
import com.techchallenge.os.application.responses.OrdemServicoResponse;
import com.techchallenge.os.application.usecases.assemblers.OrdemServicoResponseAssembler;
import com.techchallenge.os.domain.exceptions.ItemServicoNaoEncontradoException;
import com.techchallenge.os.domain.exceptions.OrdemServicoNaoEncontradaException;
import com.techchallenge.os.domain.exceptions.ValidacaoOrdemServicoException;
import com.techchallenge.os.domain.models.ItemMRO;
import com.techchallenge.os.domain.models.ItemServico;
import com.techchallenge.os.domain.models.OrdemServico;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class AtualizarQuantidadeMROUseCase implements AtualizarQuantidadeMROInput {
    
    private final OrdemServicoGateway ordemServicoGateway;
    private final OrdemServicoResponseAssembler ordemServicoResponseAssembler;

    public OrdemServicoResponse execute(AtualizarQuantidadeMROCommand command) {
        log.info("Atualizando quantidade de MRO: ordemServicoId={}, itemServicoId={}, itemMroId={}, novaQuantidade={}", 
                command.getOrdemServicoId(), command.getItemServicoId(), command.getItemMroId(), command.getNovaQuantidade());
        
        // Buscar ordem de serviço
        OrdemServico ordemServico = ordemServicoGateway.findById(command.getOrdemServicoId())
                .orElseThrow(() -> new OrdemServicoNaoEncontradaException(command.getOrdemServicoId()));
        
        // Buscar item de serviço
        ItemServico itemServico = ordemServico.getItensServico().stream()
                .filter(item -> item.getId().equals(command.getItemServicoId()))
                .findFirst()
                .orElseThrow(() -> new ItemServicoNaoEncontradoException(command.getItemServicoId()));
        
        // Buscar item de MRO
        ItemMRO itemMRO = itemServico.getMrosServicos().stream()
                .filter(mro -> mro.getId().equals(command.getItemMroId()))
                .findFirst()
                .orElseThrow(() -> new ValidacaoOrdemServicoException("Item de MRO não encontrado com ID: " + command.getItemMroId()));
        
        // Atualizar quantidade
        itemMRO.atualizarQuantidade(command.getNovaQuantidade());
        
        // Atualizar valor total do item de serviço
        itemServico.atualizarValorMRO();
        
        // Persistência
        OrdemServico savedOrdemServico = ordemServicoGateway.save(ordemServico);
        
        log.info("Quantidade de MRO atualizada com sucesso: itemMroId={}, novaQuantidade={}", 
                command.getItemMroId(), command.getNovaQuantidade());

        return ordemServicoResponseAssembler.montar(savedOrdemServico);
    }
}
