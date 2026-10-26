package com.techchallenge.os.domain.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PagamentoAprovadoEvent(
        UUID pagamentoId,
        UUID orcamentoId,
        UUID ordemServicoId,
        UUID clienteId,
        String mercadoPagoPaymentId,
        BigDecimal valorPago,
        LocalDateTime dataPagamento,
        LocalDateTime timestamp
) {}
