package com.techchallenge.os.infrastructure.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techchallenge.os.domain.events.SagaPaymentConfirmedEvent;
import com.techchallenge.os.domain.events.SagaPaymentInitiatedEvent;
import com.techchallenge.os.domain.events.SagaPaymentExpiredEvent;
import com.techchallenge.os.application.commands.AprovarOrcamentoCommand;
import com.techchallenge.os.application.commands.RecusarOrcamentoCommand;
import com.techchallenge.os.application.gateway.ports.input.AprovarOrcamentoInput;
import com.techchallenge.os.application.gateway.ports.input.RecusarOrcamentoInput;
import com.techchallenge.os.application.usecases.NotificarClienteOrcamentoComPixUseCase;
import com.techchallenge.os.domain.events.CobrancaPixGeradaEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Listener de eventos de Saga do Billing-Service via RabbitMQ
 * Processa eventos de iniciação, confirmação e expiração de pagamentos PIX
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "messaging.type", havingValue = "rabbit")
public class RabbitSagaEventListener {

    private final ObjectMapper objectMapper;
    private final AprovarOrcamentoInput aprovarOrcamentoInput;
    private final RecusarOrcamentoInput recusarOrcamentoInput;
    private final NotificarClienteOrcamentoComPixUseCase notificarClienteOrcamentoComPixUseCase;

    /**
     * Recebe evento SagaPaymentInitiatedEvent quando PIX é gerado
     * Notifica cliente com QR Code e dados de pagamento
     */
    @RabbitListener(queues = "${messaging.rabbit.queues.saga-initiated:os.saga.payment.initiated}")
    public void receiveSagaPaymentInitiated(String body) {
        try {
            SagaPaymentInitiatedEvent event = readEvent(body, SagaPaymentInitiatedEvent.class);
            
            log.info("SagaPaymentInitiatedEvent recebido: sagaId={}, ordemServicoId={}",
                    event.getSagaId(), event.getOrdemServicoId());

            // Converter para evento legado de compatibilidade
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
            log.info("Cliente notificado com PIX: ordemServicoId={}", event.getOrdemServicoId());
        } catch (Exception e) {
            log.error("Erro ao processar SagaPaymentInitiatedEvent", e);
            throw new RuntimeException("Falha ao processar evento de saga iniciada", e);
        }
    }

    /**
     * Recebe evento SagaPaymentConfirmedEvent quando pagamento é confirmado
     * Aprova ou recusa orçamento conforme resultado (APROVADO, RECUSADO, EXPIRADO)
     */
    @RabbitListener(queues = "${messaging.rabbit.queues.saga-confirmed:os.saga.payment.confirmed}")
    @Transactional
    public void receiveSagaPaymentConfirmed(String body) {
        try {
            SagaPaymentConfirmedEvent event = readEvent(body, SagaPaymentConfirmedEvent.class);
            
            log.info("SagaPaymentConfirmedEvent recebido: sagaId={}, statusFinal={}, ordemServicoId={}",
                    event.getSagaId(), event.getStatusFinal(), event.getOrdemServicoId());

            switch (event.getStatusFinal()) {
                case "APROVADO":
                    aprovarOrcamentoInput.execute(new AprovarOrcamentoCommand(event.getOrdemServicoId()));
                    log.info("Orçamento aprovado via saga: ordemServicoId={}", event.getOrdemServicoId());
                    break;
                case "RECUSADO":
                case "EXPIRADO":
                case "CANCELADO":
                    recusarOrcamentoInput.execute(
                            new RecusarOrcamentoCommand(event.getOrdemServicoId(), event.getMotivo()));
                    log.info("Orçamento recusado via saga: ordemServicoId={}, motivo={}",
                            event.getOrdemServicoId(), event.getMotivo());
                    break;
                default:
                    log.warn("Status de saga desconhecido: {}", event.getStatusFinal());
            }
        } catch (Exception e) {
            log.error("Erro ao processar SagaPaymentConfirmedEvent", e);
            throw new RuntimeException("Falha ao processar evento de saga confirmada", e);
        }
    }

    /**
     * Recebe evento SagaPaymentExpiredEvent quando PIX expira
     * Recusa orçamento automaticamente
     */
    @RabbitListener(queues = "${messaging.rabbit.queues.saga-expired:os.saga.payment.expired}")
    @Transactional
    public void receiveSagaPaymentExpired(String body) {
        try {
            SagaPaymentExpiredEvent event = readEvent(body, SagaPaymentExpiredEvent.class);
            
            log.info("SagaPaymentExpiredEvent recebido: sagaId={}, ordemServicoId={}",
                    event.getSagaId(), event.getOrdemServicoId());

            String motivo = String.format("PIX expirou em %s", event.getDataExpiracao());
            recusarOrcamentoInput.execute(
                    new RecusarOrcamentoCommand(event.getOrdemServicoId(), motivo));
            
            log.info("Orçamento recusado por expiração de PIX: ordemServicoId={}", event.getOrdemServicoId());
        } catch (Exception e) {
            log.error("Erro ao processar SagaPaymentExpiredEvent", e);
            throw new RuntimeException("Falha ao processar evento de saga expirada", e);
        }
    }

    private <T> T readEvent(String body, Class<T> eventType) {
        try {
            JsonNode payload = objectMapper.readTree(body);
            if (payload == null || !payload.isObject()) {
                throw new IllegalArgumentException("Mensagem RabbitMQ precisa conter um objeto JSON.");
            }
            return objectMapper.treeToValue(payload, eventType);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Mensagem inválida recebida do RabbitMQ.", exception);
        }
    }
}
