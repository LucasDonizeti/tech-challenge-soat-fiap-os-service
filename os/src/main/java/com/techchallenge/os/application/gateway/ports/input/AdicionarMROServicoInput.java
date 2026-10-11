package com.techchallenge.os.application.gateway.ports.input;


import com.techchallenge.os.application.commands.AdicionarMROServicoCommand;
import com.techchallenge.os.application.responses.OrdemServicoResponse;

public interface AdicionarMROServicoInput {
    OrdemServicoResponse execute(AdicionarMROServicoCommand command);
}
