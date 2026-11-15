package com.techchallenge.os.application.gateway.ports.input;

import com.techchallenge.os.application.commands.CancelarServicoCommand;
import com.techchallenge.os.application.responses.OrdemServicoResponse;

public interface CancelarServicoInput {
    OrdemServicoResponse execute(CancelarServicoCommand command);
}
