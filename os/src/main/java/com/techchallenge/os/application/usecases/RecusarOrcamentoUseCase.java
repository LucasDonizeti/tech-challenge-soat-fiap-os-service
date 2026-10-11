package com.techchallenge.os.application.usecases;

import com.techchallenge.os.application.commands.RecusarOrcamentoCommand;
import com.techchallenge.os.application.gateway.ports.input.RecusarOrcamentoInput;
import com.techchallenge.os.application.gateway.ports.output.OrdemServicoGateway;
import com.techchallenge.os.application.responses.OrdemServicoResponse;
import com.techchallenge.os.application.usecases.assemblers.OrdemServicoResponseAssembler;
import com.techchallenge.os.domain.exceptions.OrdemServicoNaoEncontradaException;
import com.techchallenge.os.domain.models.OrdemServico;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class RecusarOrcamentoUseCase implements RecusarOrcamentoInput {

    private final OrdemServicoGateway ordemServicoGateway;
    private final OrdemServicoResponseAssembler ordemServicoResponseAssembler;

    @Override
    public OrdemServicoResponse execute(RecusarOrcamentoCommand command) {
        log.info("Recusando orçamento: ordemServicoId={}, motivo={}",
                command.getOrdemServicoId(), command.getMotivo());

        OrdemServico ordemServico = ordemServicoGateway.findById(command.getOrdemServicoId())
                .orElseThrow(() -> new OrdemServicoNaoEncontradaException(command.getOrdemServicoId()));

        // Transição de status + emissão de evento de domínio feitos no agregado
        ordemServico.recusarOrcamento(command.getMotivo());

        OrdemServico saved = ordemServicoGateway.save(ordemServico);

        log.info("Orçamento recusado com sucesso: ID={}, Status={}",
                saved.getId(), saved.getStatus());

        return ordemServicoResponseAssembler.montar(saved);
    }
}
