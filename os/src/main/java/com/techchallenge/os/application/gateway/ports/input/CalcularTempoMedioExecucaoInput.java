package com.techchallenge.os.application.gateway.ports.input;

import com.techchallenge.os.application.responses.TempoMedioExecucaoResponse;

import java.time.LocalDateTime;
import java.util.UUID;

public interface CalcularTempoMedioExecucaoInput {
    TempoMedioExecucaoResponse execute(UUID servicoId, LocalDateTime dataInicio, LocalDateTime dataFim);
}
