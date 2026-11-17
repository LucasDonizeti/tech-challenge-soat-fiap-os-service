package com.techchallenge.os.infrastructure.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techchallenge.os.application.commands.RecusarOrcamentoCommand;
import com.techchallenge.os.application.gateway.ports.input.RecusarOrcamentoInput;
import com.techchallenge.os.application.usecases.NotificarClienteOrcamentoComPixUseCase;
import com.techchallenge.os.domain.events.CobrancaPixGeradaEvent;
import com.techchallenge.os.domain.events.SagaPaymentExpiredEvent;
import com.techchallenge.os.domain.events.SagaPaymentInitiatedEvent;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "messaging.type", havingValue = "aws")
public class SqsSagaEventListener {

    private final ObjectMapper objectMapper;
    private final NotificarClienteOrcamentoComPixUseCase notificarClienteOrcamentoComPixUseCase;
    private final RecusarOrcamentoInput recusarOrcamentoInput;

    @SqsListener("${aws.sqs.queues.saga-initiated}")
    public void receiveSagaPaymentInitiated(String body) {
        SagaPaymentInitiatedEvent event = readEvent(body, SagaPaymentInitiatedEvent.class);
        CobrancaPixGeradaEvent legacyEvent = new CobrancaPixGeradaEvent(
                event.getPagamentoId(),
                event.getOrcamentoId(),
                event.getOrdemServicoId(),
                event.getClienteId(),
                event.getQrCodePix(),
                event.getQrCodeBase64(),
                event.getValorCobrado(),
                event.getDataExpiracao(),
                event.getOrdemServicoId().toString(),
                event.getTimestamp()
        );
        notificarClienteOrcamentoComPixUseCase.execute(legacyEvent);
        log.info("SagaPaymentInitiatedEvent processado: ordemServicoId={}", event.getOrdemServicoId());
    }

    @SqsListener("${aws.sqs.queues.saga-expired}")
    @Transactional
    public void receiveSagaPaymentExpired(String body) {
        SagaPaymentExpiredEvent event = readEvent(body, SagaPaymentExpiredEvent.class);
        String motivo = String.format("PIX expirou em %s", event.getDataExpiracao());
        recusarOrcamentoInput.execute(new RecusarOrcamentoCommand(event.getOrdemServicoId(), motivo));
        log.info("SagaPaymentExpiredEvent processado: ordemServicoId={}", event.getOrdemServicoId());
    }

    private <T> T readEvent(String body, Class<T> eventType) {
        try {
            JsonNode payload = objectMapper.readTree(body);
            if (payload != null && payload.isObject() && payload.hasNonNull("Message")) {
                payload = objectMapper.readTree(payload.get("Message").asText());
            }
            if (payload == null || !payload.isObject()) {
                throw new IllegalArgumentException("A mensagem SQS/SNS precisa conter um objeto JSON.");
            }
            return objectMapper.treeToValue(payload, eventType);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Mensagem inválida recebida da fila de Saga.", exception);
        }
    }
}
