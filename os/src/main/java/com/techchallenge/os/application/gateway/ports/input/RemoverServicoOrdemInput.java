package com.techchallenge.os.application.gateway.ports.input;

import com.techchallenge.os.application.commands.RemoverServicoOrdemCommand;
import com.techchallenge.os.application.responses.OrdemServicoResponse;

public interface RemoverServicoOrdemInput {
    OrdemServicoResponse execute(RemoverServicoOrdemCommand command);
}
