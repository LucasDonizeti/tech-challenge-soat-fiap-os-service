package com.techchallenge.os.application.gateway.ports.input;


import com.techchallenge.os.application.commands.AprovarOrcamentoCommand;
import com.techchallenge.os.application.responses.OrdemServicoResponse;

public interface AprovarOrcamentoInput {
    OrdemServicoResponse execute(AprovarOrcamentoCommand command);
}
