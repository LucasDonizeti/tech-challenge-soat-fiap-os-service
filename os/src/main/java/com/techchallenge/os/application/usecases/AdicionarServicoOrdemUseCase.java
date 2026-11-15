package com.techchallenge.os.application.usecases;

import com.techchallenge.os.application.commands.AdicionarServicoOrdemCommand;
import com.techchallenge.os.application.gateway.ports.input.AdicionarServicoOrdemInput;
import com.techchallenge.os.application.gateway.ports.output.ClienteGateway;
import com.techchallenge.os.application.gateway.ports.output.OrdemServicoGateway;
import com.techchallenge.os.application.gateway.ports.output.ServicoGateway;
import com.techchallenge.os.application.gateway.ports.output.VeiculoGateway;
import com.techchallenge.os.application.gateway.ports.output.models.ClienteResumo;
import com.techchallenge.os.application.gateway.ports.output.models.ServicoResumo;
import com.techchallenge.os.application.gateway.ports.output.models.VeiculoResumo;
import com.techchallenge.os.application.responses.OrdemServicoResponse;
import com.techchallenge.os.application.usecases.assemblers.OrdemServicoResponseAssembler;
import com.techchallenge.os.domain.exceptions.OrdemServicoNaoEncontradaException;
import com.techchallenge.os.domain.exceptions.ValidacaoServicoException;
import com.techchallenge.os.domain.models.ItemServico;
import com.techchallenge.os.domain.models.OrdemServico;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@RequiredArgsConstructor
@Slf4j
public class AdicionarServicoOrdemUseCase implements AdicionarServicoOrdemInput {
    
    private final OrdemServicoGateway ordemServicoGateway;
    private final ServicoGateway servicoGateway;
    private final OrdemServicoResponseAssembler ordemServicoResponseAssembler;

    public OrdemServicoResponse execute(AdicionarServicoOrdemCommand command) {
        log.info("Adicionando serviço à ordem de serviço: ordemServicoId={}, servicoId={}", 
                command.getOrdemServicoId(), command.getServicoId());
        
        // Buscar ordem de serviço
        OrdemServico ordemServico = ordemServicoGateway.findById(command.getOrdemServicoId())
                .orElseThrow(() -> new OrdemServicoNaoEncontradaException(command.getOrdemServicoId()));
        
        // A validação de status é feita dentro do próprio aggregate (adicionarItemServico)
        // que só permite adicionar serviços quando a OS está RECEBIDA
        
        // Buscar serviço
        ServicoResumo servicoResponse = servicoGateway.findById(command.getServicoId())
                .orElseThrow(() -> new ValidacaoServicoException("MRO não encontrado com ID: " + command.getServicoId()));
        
        // Criar item de serviço com dados do DTO
        ItemServico itemServico = ItemServico.criarComDados(
            servicoResponse.getId(),
            servicoResponse.getNome(),
            servicoResponse.getDescricao(),
            servicoResponse.getPreco()
        );
        
        // Adicionar à ordem de serviço (validação acontece dentro do método)
        ordemServico.adicionarItemServico(itemServico);
        
        // Persistência
        OrdemServico savedOrdemServico = ordemServicoGateway.save(ordemServico);
        
        log.info("Serviço adicionado à ordem de serviço com sucesso: itemServicoId={}", itemServico.getId());

        return ordemServicoResponseAssembler.montar(savedOrdemServico);
    }
}
