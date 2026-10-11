package com.techchallenge.os.application.gateway.ports.input;

import com.techchallenge.os.application.commands.EntregarOrdemServicoCommand;
import com.techchallenge.os.application.responses.OrdemServicoResponse;

public interface EntregarOrdemServicoInput {
    OrdemServicoResponse execute(EntregarOrdemServicoCommand command);
}
