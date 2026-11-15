package com.techchallenge.os.application.gateway.ports.input;


import com.techchallenge.os.application.responses.OrdemServicoResponse;

import java.util.UUID;

public interface BuscarOrdemServicoInput {
    OrdemServicoResponse execute(UUID ordemServicoId);
}
