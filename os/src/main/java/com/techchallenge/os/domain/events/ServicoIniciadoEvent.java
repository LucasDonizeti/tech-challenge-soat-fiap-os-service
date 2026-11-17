package com.techchallenge.os.domain.events;

import java.util.UUID;

/**
 * Evento de domínio emitido quando os serviços são iniciados.
 */
public record ServicoIniciadoEvent(
        UUID ordemServicoId,
        UUID clienteId,
        UUID veiculoId
) {}
