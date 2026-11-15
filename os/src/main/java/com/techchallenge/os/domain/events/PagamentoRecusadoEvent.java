package com.techchallenge.os.domain.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PagamentoRecusadoEvent(
        UUID pagamentoId,
        UUID orcamentoId,
        UUID ordemServicoId,
        UUID clienteId,
        String motivo,
        String statusMercadoPago,
        LocalDateTime dataRejeicao,
        LocalDateTime timestamp
) {}
