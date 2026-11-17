package com.techchallenge.os.domain.events;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Evento de domínio emitido quando o orçamento está pronto para aprovação.
 */
public record OrcamentoProntoEvent(
    UUID ordemServicoId,
    UUID clienteId,
    UUID veiculoId,
    BigDecimal valorTotal,
    String descricaoServicos,
    LocalDateTime dataOrcamento,
    String payerEmail,
    String payerIdentificationType,
    String payerIdentificationNumber
) {}
