package com.techchallenge.os.application.gateway.ports.input;

import com.techchallenge.os.application.commands.IniciarServicoCommand;
import com.techchallenge.os.application.responses.OrdemServicoResponse;

public interface IniciarServicoInput {
    OrdemServicoResponse execute(IniciarServicoCommand command);
}
