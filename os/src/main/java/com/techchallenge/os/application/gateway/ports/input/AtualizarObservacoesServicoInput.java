package com.techchallenge.os.application.gateway.ports.input;


import com.techchallenge.os.application.commands.AtualizarObservacoesServicoCommand;
import com.techchallenge.os.application.responses.OrdemServicoResponse;

public interface AtualizarObservacoesServicoInput {
    OrdemServicoResponse execute(AtualizarObservacoesServicoCommand command);
}
