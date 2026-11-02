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
 * Evento de saga: Resultado final do pagamento PIX
 * Publicado por billing-service para notificar OS-Service
 * Pode ser APROVADO, RECUSADO ou EXPIRADO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SagaPaymentConfirmedEvent implements Serializable {
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
    private String statusFinal; // APROVADO, RECUSADO, EXPIRADO

    @JsonProperty
    private BigDecimal valorPago;

    @JsonProperty
    private LocalDateTime dataPagamento;

    @JsonProperty
    private String motivo; // Motivo de rejeição/expiração

    @JsonProperty
    private LocalDateTime timestamp;
}
