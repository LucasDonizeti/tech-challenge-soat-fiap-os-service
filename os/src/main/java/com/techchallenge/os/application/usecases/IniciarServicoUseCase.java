package com.techchallenge.os.application.usecases;

import com.techchallenge.os.application.commands.IniciarServicoCommand;
import com.techchallenge.os.application.gateway.ports.input.IniciarServicoInput;
import com.techchallenge.os.application.gateway.ports.output.MroGateway;
import com.techchallenge.os.application.gateway.ports.output.OrdemServicoGateway;
import com.techchallenge.os.application.gateway.ports.output.models.MROResumo;
import com.techchallenge.os.application.responses.OrdemServicoResponse;
import com.techchallenge.os.application.usecases.assemblers.OrdemServicoResponseAssembler;
import com.techchallenge.os.domain.exceptions.ItemServicoNaoEncontradoException;
import com.techchallenge.os.domain.exceptions.OrdemServicoNaoEncontradaException;
import com.techchallenge.os.domain.exceptions.ValidacaoEstoqueException;
import com.techchallenge.os.domain.exceptions.ValidacaoMROException;
import com.techchallenge.os.domain.models.ItemMRO;
import com.techchallenge.os.domain.models.ItemServico;
import com.techchallenge.os.domain.models.OrdemServico;
import com.techchallenge.os.domain.models.valueobjects.StatusItemServico;
import com.techchallenge.os.domain.models.valueobjects.StatusOS;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class IniciarServicoUseCase implements IniciarServicoInput {
    
    private final OrdemServicoGateway ordemServicoGateway;
    private final MroGateway mroGateway;
    private final OrdemServicoResponseAssembler ordemServicoResponseAssembler;

    public OrdemServicoResponse execute(IniciarServicoCommand command) {
        log.info("Iniciando serviço: ordemServicoId={}, itemServicoId={}", 
                command.getOrdemServicoId(), command.getItemServicoId());
        
        // Buscar ordem de serviço
        OrdemServico ordemServico = ordemServicoGateway.findById(command.getOrdemServicoId())
                .orElseThrow(() -> new OrdemServicoNaoEncontradaException(command.getOrdemServicoId()));
        
        // Buscar item de serviço
        ItemServico itemServico = ordemServico.getItensServico().stream()
                .filter(item -> item.getId().equals(command.getItemServicoId()))
                .findFirst()
                .orElseThrow(() -> new ItemServicoNaoEncontradoException(command.getItemServicoId()));
        
        // Validar que OS está em EM_EXECUCAO
        if (ordemServico.getStatus() != StatusOS.EM_EXECUCAO) {
            throw new ValidacaoEstoqueException("Só é possível iniciar serviços quando a OS está em EM_EXECUCAO. Status atual: " + ordemServico.getStatus());
        }
        
        // Validar que item está PENDENTE
        if (itemServico.getStatus() != StatusItemServico.PENDENTE) {
            throw new ValidacaoEstoqueException("Só é possível iniciar serviços com status PENDENTE. Status atual: " + itemServico.getStatus());
        }
        
        // Debitar estoque dos MROs
        debitarEstoqueMROs(itemServico);
        
        // Atualizar status para EM_ANDAMENTO
        itemServico.atualizarStatus(StatusItemServico.EM_ANDAMENTO);
        
        // Persistência
        OrdemServico savedOrdemServico = ordemServicoGateway.save(ordemServico);
        
        log.info("Serviço iniciado com sucesso: itemServicoId={}", itemServico.getId());

        return ordemServicoResponseAssembler.montar(savedOrdemServico);
    }
    
    private void debitarEstoqueMROs(ItemServico itemServico) {
        for (ItemMRO itemMRO : itemServico.getMrosServicos()) {
            try {

                log.info("Debitando estoque para MRO: mroId={}, quantidade={}", itemMRO.getMroId(), itemMRO.getQuantidade());

                MROResumo mro = mroGateway.debitarEstoque(itemMRO.getId(), itemMRO.getQuantidade())
                        .orElseThrow(() -> new ValidacaoMROException("Erro ao debitar estoque MRO: " + itemMRO.getId()));
                
                log.info("Estoque debitado para MRO: mroId={}, quantidadeFinal={}", mro.getId(), mro.getQuantidadeEstoque());
            } catch (Exception e) {
                log.error("Erro ao debitar estoque do MRO: mroId={}", itemMRO.getMroId(), e);
                throw new ValidacaoEstoqueException("Erro ao debitar estoque do MRO: " + itemMRO.getMroNome() + ". " + e.getMessage());
            }
        }
    }
}
