package com.techchallenge.os.application.gateway.ports.input;


import com.techchallenge.os.application.commands.AtualizarQuantidadeMROCommand;
import com.techchallenge.os.application.responses.OrdemServicoResponse;

public interface AtualizarQuantidadeMROInput {
    OrdemServicoResponse execute(AtualizarQuantidadeMROCommand command);
}
