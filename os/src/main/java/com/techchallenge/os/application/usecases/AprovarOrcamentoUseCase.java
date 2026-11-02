package com.techchallenge.os.application.usecases;


import com.techchallenge.os.application.commands.AprovarOrcamentoCommand;
import com.techchallenge.os.application.gateway.ports.input.AprovarOrcamentoInput;
import com.techchallenge.os.application.gateway.ports.output.OrdemServicoGateway;
import com.techchallenge.os.application.responses.OrdemServicoResponse;
import com.techchallenge.os.application.usecases.assemblers.OrdemServicoResponseAssembler;
import com.techchallenge.os.domain.exceptions.OrdemServicoNaoEncontradaException;
import com.techchallenge.os.domain.models.OrdemServico;
import com.techchallenge.os.domain.models.valueobjects.StatusOS;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class AprovarOrcamentoUseCase implements AprovarOrcamentoInput {
    
    private final OrdemServicoGateway ordemServicoGateway;
    private final OrdemServicoResponseAssembler ordemServicoResponseAssembler;

    public OrdemServicoResponse execute(AprovarOrcamentoCommand command) {
        log.info("Aprovando orçamento: ordemServicoId={}", command.getOrdemServicoId());
        
        // Buscar ordem de serviço
        OrdemServico ordemServico = ordemServicoGateway.findById(command.getOrdemServicoId())
                .orElseThrow(() -> new OrdemServicoNaoEncontradaException(command.getOrdemServicoId()));

        if (ordemServico.getStatus() == StatusOS.EM_EXECUCAO) {
            log.info("Orçamento já aprovado; ignorando evento duplicado: ordemServicoId={}",
                    command.getOrdemServicoId());
            return ordemServicoResponseAssembler.montar(ordemServico);
        }
        
        // Aprovar orçamento (validações são feitas no domínio)
        ordemServico.aprovarOrcamento();
        
        // Persistência
        OrdemServico savedOrdemServico = ordemServicoGateway.save(ordemServico);
        
        log.info("Orçamento aprovado com sucesso: ID={}, Status={}", 
                savedOrdemServico.getId(), savedOrdemServico.getStatus());

        return ordemServicoResponseAssembler.montar(savedOrdemServico);
    }
}
