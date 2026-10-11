package com.techchallenge.os.application.gateway.ports.input;

import com.techchallenge.os.application.commands.EnviarParaDiagnosticoCommand;
import com.techchallenge.os.application.responses.OrdemServicoResponse;

public interface EnviarParaDiagnosticoInput {
    OrdemServicoResponse execute(EnviarParaDiagnosticoCommand command);
}
