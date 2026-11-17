package com.techchallenge.os.application.gateway.ports.input;

import com.techchallenge.os.application.commands.ConcluirServicoCommand;
import com.techchallenge.os.application.responses.OrdemServicoResponse;

public interface ConcluirServicoInput {
    OrdemServicoResponse execute(ConcluirServicoCommand command);
}
