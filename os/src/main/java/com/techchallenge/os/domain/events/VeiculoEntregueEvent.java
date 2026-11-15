package com.techchallenge.os.domain.events;

import java.util.UUID;

/**
 * Evento de domínio emitido quando o veículo é entregue.
 */
public record VeiculoEntregueEvent(
        UUID ordemServicoId,
        UUID clienteId,
        UUID veiculoId
) {}
