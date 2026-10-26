package com.techchallenge.os.infrastructure.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techchallenge.os.application.commands.AprovarOrcamentoCommand;
import com.techchallenge.os.application.commands.RecusarOrcamentoCommand;
import com.techchallenge.os.application.gateway.ports.input.AprovarOrcamentoInput;
import com.techchallenge.os.application.gateway.ports.input.RecusarOrcamentoInput;
import com.techchallenge.os.application.usecases.NotificarClienteOrcamentoComPixUseCase;
import com.techchallenge.os.domain.events.CobrancaPixGeradaEvent;
import com.techchallenge.os.domain.events.PagamentoAprovadoEvent;
import com.techchallenge.os.domain.events.PagamentoRecusadoEvent;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "messaging.type", havingValue = "aws")
public class SqsBillingEventListener {

    private final ObjectMapper objectMapper;
    private final NotificarClienteOrcamentoComPixUseCase notificarClienteOrcamentoComPixUseCase;
    private final AprovarOrcamentoInput aprovarOrcamentoInput;
    private final RecusarOrcamentoInput recusarOrcamentoInput;

    @SqsListener("${aws.sqs.queues.cobranca-pix-gerada}")
    public void receiveCobrancaPixGerada(String body) {
        CobrancaPixGeradaEvent event = readEvent(body, CobrancaPixGeradaEvent.class);
        notificarClienteOrcamentoComPixUseCase.execute(event);
        log.info("CobrancaPixGeradaEvent processado: ordemServicoId={}", event.ordemServicoId());
    }

    @SqsListener("${aws.sqs.queues.pagamento-aprovado}")
    public void receivePagamentoAprovado(String body) {
        PagamentoAprovadoEvent event = readEvent(body, PagamentoAprovadoEvent.class);
        aprovarOrcamentoInput.execute(new AprovarOrcamentoCommand(event.ordemServicoId()));
        log.info("PagamentoAprovadoEvent processado: ordemServicoId={}", event.ordemServicoId());
    }

    @SqsListener("${aws.sqs.queues.pagamento-recusado}")
    public void receivePagamentoRecusado(String body) {
        PagamentoRecusadoEvent event = readEvent(body, PagamentoRecusadoEvent.class);
        recusarOrcamentoInput.execute(new RecusarOrcamentoCommand(
                event.ordemServicoId(), event.motivo()));
        log.info("PagamentoRecusadoEvent processado: ordemServicoId={}", event.ordemServicoId());
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
            throw new IllegalArgumentException("Mensagem inválida recebida da fila de eventos do Billing.", exception);
        }
    }
}
