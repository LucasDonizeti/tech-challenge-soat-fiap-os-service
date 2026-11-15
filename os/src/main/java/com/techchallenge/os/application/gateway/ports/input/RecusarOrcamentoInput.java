package com.techchallenge.os.application.gateway.ports.input;

import com.techchallenge.os.application.commands.RecusarOrcamentoCommand;
import com.techchallenge.os.application.responses.OrdemServicoResponse;

public interface RecusarOrcamentoInput {
    OrdemServicoResponse execute(RecusarOrcamentoCommand command);
}
