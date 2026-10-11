package com.techchallenge.os.application.gateway.ports.input;


import com.techchallenge.os.application.commands.RemoverMROServicoCommand;
import com.techchallenge.os.application.responses.OrdemServicoResponse;

public interface RemoverMROServicoInput {
    OrdemServicoResponse execute(RemoverMROServicoCommand command);
}
