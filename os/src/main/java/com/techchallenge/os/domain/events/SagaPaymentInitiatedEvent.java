package com.techchallenge.os.domain.events;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Evento de saga: PIX foi gerado e saga iniciada
 * Publicado por billing-service para notificar OS-Service
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SagaPaymentInitiatedEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonProperty
    private UUID sagaId;

    @JsonProperty
    private UUID pagamentoId;

    @JsonProperty
    private UUID orcamentoId;

    @JsonProperty
    private UUID ordemServicoId;

    @JsonProperty
    private UUID clienteId;

    @JsonProperty
    private String mercadoPagoPaymentId;

    @JsonProperty
    private String qrCodePix;

    @JsonProperty
    private String qrCodeBase64;

    @JsonProperty
    private BigDecimal valorCobrado;

    @JsonProperty
    private LocalDateTime dataExpiracao;

    @JsonProperty
    private LocalDateTime timestamp;
}
