package com.techchallenge.os.domain.events;

import java.util.UUID;

/**
 * Evento de domínio emitido quando o cliente recusa o orçamento da OS.
 * Dispara notificação e transiciona o status para CANCELADA.
 */
public record OrcamentoRecusadoEvent(
        UUID ordemServicoId,
        UUID clienteId,
        UUID veiculoId,
        String motivoRecusa
) {}
