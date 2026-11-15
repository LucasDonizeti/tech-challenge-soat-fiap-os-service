package com.techchallenge.os.infrastructure.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techchallenge.os.application.gateway.ports.output.OsEventPublisher;
import com.techchallenge.os.domain.events.OrcamentoProntoEvent;
import io.awspring.cloud.sns.core.SnsTemplate;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class SnsOsEventPublisher implements OsEventPublisher {

    private final SnsTemplate snsTemplate;
    private final ObjectMapper objectMapper;

    @Value("${aws.sns.topics.orcamento-pronto:}")
    private String orcamentoProntoTopic;

    @PostConstruct
    void validateTopic() {
        if (orcamentoProntoTopic == null || orcamentoProntoTopic.isBlank()) {
            throw new IllegalStateException(
                    "Configure AWS_SNS_TOPIC_ORCAMENTO_PRONTO com o ARN do tópico SNS.");
        }
    }

    @Override
    public void publishOrcamentoPronto(OrcamentoProntoEvent event) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    publish(event);
                }
            });
            return;
        }
        publish(event);
    }

    private void publish(OrcamentoProntoEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            snsTemplate.convertAndSend(orcamentoProntoTopic, payload,
                    Map.of("EventType", "OrcamentoProntoEvent"));
            log.info("OrcamentoProntoEvent publicado no SNS: ordemServicoId={}", event.ordemServicoId());
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Não foi possível serializar OrcamentoProntoEvent.", exception);
        } catch (RuntimeException exception) {
            log.error("Falha ao publicar OrcamentoProntoEvent no SNS: ordemServicoId={}",
                    event.ordemServicoId(), exception);
            throw exception;
        }
    }
}
