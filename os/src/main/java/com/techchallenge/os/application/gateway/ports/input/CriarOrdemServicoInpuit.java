package com.techchallenge.os.application.gateway.ports.input;

import com.techchallenge.os.application.commands.CriarOrdemServicoCommand;
import com.techchallenge.os.application.responses.OrdemServicoResponse;

public interface CriarOrdemServicoInpuit {
    OrdemServicoResponse execute(CriarOrdemServicoCommand command);
}
