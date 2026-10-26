package com.techchallenge.os.domain.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CobrancaPixGeradaEvent(
        UUID pagamentoId,
        UUID orcamentoId,
        UUID ordemServicoId,
        UUID clienteId,
        String qrCodePix,
        String qrCodeBase64,
        BigDecimal valorCobrado,
        LocalDateTime dataExpiracao,
        String externalReference,
        LocalDateTime timestamp
) {}
