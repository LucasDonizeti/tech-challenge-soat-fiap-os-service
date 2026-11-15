package com.techchallenge.os.application.gateway.ports.input;
import com.techchallenge.os.application.responses.OrdemServicoResponse;
import com.techchallenge.os.domain.models.valueobjects.StatusOS;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.UUID;

public interface ListarOrdensServicoInput {
    Page<OrdemServicoResponse> execute(UUID clienteId, UUID veiculoId, StatusOS status,
                                       LocalDateTime dataInicio, LocalDateTime dataFim,
                                       Pageable pageable);
}
