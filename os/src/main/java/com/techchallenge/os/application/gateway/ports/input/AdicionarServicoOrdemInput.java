package com.techchallenge.os.application.gateway.ports.input;


import com.techchallenge.os.application.commands.AdicionarServicoOrdemCommand;
import com.techchallenge.os.application.responses.OrdemServicoResponse;

public interface AdicionarServicoOrdemInput {
    OrdemServicoResponse execute(AdicionarServicoOrdemCommand command);
}
