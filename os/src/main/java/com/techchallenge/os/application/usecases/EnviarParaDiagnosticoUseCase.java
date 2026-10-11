package com.techchallenge.os.application.usecases;

import com.techchallenge.os.application.commands.EnviarParaDiagnosticoCommand;
import com.techchallenge.os.application.gateway.ports.input.EnviarParaDiagnosticoInput;
import com.techchallenge.os.application.gateway.ports.output.OrdemServicoGateway;
import com.techchallenge.os.application.responses.OrdemServicoResponse;
import com.techchallenge.os.application.usecases.assemblers.OrdemServicoResponseAssembler;
import com.techchallenge.os.domain.exceptions.OrdemServicoNaoEncontradaException;
import com.techchallenge.os.domain.models.OrdemServico;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class EnviarParaDiagnosticoUseCase implements EnviarParaDiagnosticoInput {
    
    private final OrdemServicoGateway ordemServicoGateway;
    private final OrdemServicoResponseAssembler ordemServicoResponseAssembler;

    public OrdemServicoResponse execute(EnviarParaDiagnosticoCommand command) {
        log.info("Enviando ordem de serviço para diagnóstico: ordemServicoId={}", command.getOrdemServicoId());
        
        // Buscar ordem de serviço
        OrdemServico ordemServico = ordemServicoGateway.findById(command.getOrdemServicoId())
                .orElseThrow(() -> new OrdemServicoNaoEncontradaException(command.getOrdemServicoId()));
        
        // Enviar para diagnóstico (validações são feitas no domínio)
        ordemServico.enviarParaDiagnostico();
        
        // Persistência
        OrdemServico savedOrdemServico = ordemServicoGateway.save(ordemServico);
        
        log.info("Ordem de serviço enviada para diagnóstico com sucesso: ID={}, Status={}", 
                savedOrdemServico.getId(), savedOrdemServico.getStatus());

        return ordemServicoResponseAssembler.montar(savedOrdemServico);
    }
}
