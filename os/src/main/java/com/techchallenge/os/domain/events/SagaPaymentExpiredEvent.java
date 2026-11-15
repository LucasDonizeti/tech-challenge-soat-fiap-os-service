package com.techchallenge.os.domain.events;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Evento de saga: PIX expirou sem pagamento
 * Publicado por billing-service para notificar OS-Service
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SagaPaymentExpiredEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonProperty
    private UUID sagaId;

    @JsonProperty
    private UUID pagamentoId;

    @JsonProperty
    private UUID ordemServicoId;

    @JsonProperty
    private String mercadoPagoPaymentId;

    @JsonProperty
    private LocalDateTime dataExpiracao;

    @JsonProperty
    private LocalDateTime timestamp;
}
