package com.techchallenge.os.application.usecases;

import com.techchallenge.os.application.commands.EnviarOrcamentoAoClienteCommand;
import com.techchallenge.os.application.gateway.ports.input.EnviarOrcamentoAoClienteInput;
import com.techchallenge.os.application.gateway.ports.output.OrdemServicoGateway;
import com.techchallenge.os.application.responses.OrdemServicoResponse;
import com.techchallenge.os.application.usecases.assemblers.OrdemServicoResponseAssembler;
import com.techchallenge.os.domain.exceptions.OrdemServicoNaoEncontradaException;
import com.techchallenge.os.domain.models.OrdemServico;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class EnviarOrcamentoAoClienteUseCase implements EnviarOrcamentoAoClienteInput {
    
    private final OrdemServicoGateway ordemServicoGateway;
    private final OrdemServicoResponseAssembler ordemServicoResponseAssembler;

    public OrdemServicoResponse execute(EnviarOrcamentoAoClienteCommand command) {
        log.info("Enviando orçamento ao cliente: ordemServicoId={}", command.getOrdemServicoId());
        
        // Buscar ordem de serviço
        OrdemServico ordemServico = ordemServicoGateway.findById(command.getOrdemServicoId())
                .orElseThrow(() -> new OrdemServicoNaoEncontradaException(command.getOrdemServicoId()));
        
        // Enviar orçamento ao cliente (validações são feitas no domínio)
        ordemServico.enviarOrcamentoAoCliente();
        
        // Log de notificação ao cliente
        log.info("Notificação enviada ao cliente: clienteId={}, ordemServicoId={}, valorTotal={}", 
                ordemServico.getCliente(), ordemServico.getId(), ordemServico.calcularValorTotal());
        
        // Persistência
        OrdemServico savedOrdemServico = ordemServicoGateway.save(ordemServico);
        
        log.info("Orçamento enviado ao cliente com sucesso: ID={}, Status={}", 
                savedOrdemServico.getId(), savedOrdemServico.getStatus());

        return ordemServicoResponseAssembler.montar(savedOrdemServico);
    }
}
