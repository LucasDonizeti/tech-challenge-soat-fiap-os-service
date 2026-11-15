package com.techchallenge.os.domain.events;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Evento de domínio emitido quando os serviços são finalizados.
 */
public record ServicoFinalizadoEvent(
        UUID ordemServicoId,
        UUID clienteId,
        UUID veiculoId,
        BigDecimal valorTotal
) {}
