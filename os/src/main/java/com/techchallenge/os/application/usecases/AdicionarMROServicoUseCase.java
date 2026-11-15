package com.techchallenge.os.application.usecases;

import com.techchallenge.os.application.commands.AdicionarMROServicoCommand;
import com.techchallenge.os.application.gateway.ports.input.AdicionarMROServicoInput;
import com.techchallenge.os.application.gateway.ports.output.MroGateway;
import com.techchallenge.os.application.gateway.ports.output.OrdemServicoGateway;
import com.techchallenge.os.application.gateway.ports.output.models.MROResumo;
import com.techchallenge.os.application.responses.OrdemServicoResponse;
import com.techchallenge.os.application.usecases.assemblers.OrdemServicoResponseAssembler;
import com.techchallenge.os.domain.exceptions.ItemServicoNaoEncontradoException;
import com.techchallenge.os.domain.exceptions.OrdemServicoNaoEncontradaException;
import com.techchallenge.os.domain.exceptions.OrdemServicoStatusInvalidoException;
import com.techchallenge.os.domain.exceptions.ValidacaoMROException;
import com.techchallenge.os.domain.models.ItemServico;
import com.techchallenge.os.domain.models.OrdemServico;
import com.techchallenge.os.domain.models.valueobjects.StatusOS;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class AdicionarMROServicoUseCase implements AdicionarMROServicoInput {
    
    private final OrdemServicoGateway ordemServicoGateway;
    private final MroGateway mroGateway;
    private final OrdemServicoResponseAssembler ordemServicoResponseAssembler;

    public OrdemServicoResponse execute(AdicionarMROServicoCommand command) {
        log.info("Adicionando MRO ao serviço: ordemServicoId={}, itemServicoId={}, mroId={}, quantidade={}", 
                command.getOrdemServicoId(), command.getItemServicoId(), command.getMroId(), command.getQuantidade());
        
        // Buscar ordem de serviço
        OrdemServico ordemServico = ordemServicoGateway.findById(command.getOrdemServicoId())
                .orElseThrow(() -> new OrdemServicoNaoEncontradaException(command.getOrdemServicoId()));
        
        // Validar status - permite adicionar MROs quando a OS está RECEBIDA ou EM_DIAGNOSTICO
        if (ordemServico.getStatus() != StatusOS.RECEBIDA && ordemServico.getStatus() != StatusOS.EM_DIAGNOSTICO) {
            throw new OrdemServicoStatusInvalidoException("Só é possível adicionar MROs quando a Ordem de Serviço está nos status RECEBIDA ou EM_DIAGNOSTICO. Status atual: " + ordemServico.getStatus());
        }
        
        // Buscar item de serviço
        ItemServico itemServico = ordemServico.getItensServico().stream()
                .filter(item -> item.getId().equals(command.getItemServicoId()))
                .findFirst()
                .orElseThrow(() -> new ItemServicoNaoEncontradoException(command.getItemServicoId()));
        
        // Buscar MRO
        MROResumo mroResponse = mroGateway.findById(command.getMroId())
                .orElseThrow(() -> new ValidacaoMROException("MRO não encontrado com ID: " + command.getMroId()));
        
        // Adicionar MRO ao item de serviço com dados do DTO
        // Nota: O débito de estoque será feito em uma etapa posterior (ao iniciar a execução da OS)
        itemServico.adicionarMRO(
            mroResponse.getId(),
            mroResponse.getNome(),
            mroResponse.getDescricao(),
            mroResponse.getPrecoUnitario(),
            command.getQuantidade()
        );
        
        // Persistência
        OrdemServico savedOrdemServico = ordemServicoGateway.save(ordemServico);
        
        log.info("MRO adicionado ao serviço com sucesso");

        return ordemServicoResponseAssembler.montar(savedOrdemServico);
    }
}
