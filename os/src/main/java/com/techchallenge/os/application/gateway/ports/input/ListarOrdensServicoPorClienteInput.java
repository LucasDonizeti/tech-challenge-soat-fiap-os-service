package com.techchallenge.os.application.gateway.ports.input;

import com.techchallenge.os.application.responses.OrdemServicoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ListarOrdensServicoPorClienteInput {
    Page<OrdemServicoResponse> execute(UUID clienteId, Pageable pageable);
}
