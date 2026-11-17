package com.techchallenge.os.application.gateway.ports.input;

import com.techchallenge.os.application.commands.EnviarOrcamentoAoClienteCommand;
import com.techchallenge.os.application.responses.OrdemServicoResponse;

public interface EnviarOrcamentoAoClienteInput {
    OrdemServicoResponse execute(EnviarOrcamentoAoClienteCommand command);
}
