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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "messaging.type", havingValue = "rabbit")
public class RabbitBillingEventListener {

    private final ObjectMapper objectMapper;
    private final NotificarClienteOrcamentoComPixUseCase notificarClienteOrcamentoComPixUseCase;
    private final AprovarOrcamentoInput aprovarOrcamentoInput;
    private final RecusarOrcamentoInput recusarOrcamentoInput;

    @RabbitListener(queues = "${messaging.rabbit.queues.cobranca-pix-gerada}")
    public void receiveCobrancaPixGerada(String body) {
        var event = readEvent(body, CobrancaPixGeradaEvent.class);
        notificarClienteOrcamentoComPixUseCase.execute(event);
        log.info("CobrancaPixGeradaEvent processado via RabbitMQ: ordemServicoId={}", event.ordemServicoId());
    }

    @RabbitListener(queues = "${messaging.rabbit.queues.pagamento-aprovado}")
    public void receivePagamentoAprovado(String body) {
        var event = readEvent(body, PagamentoAprovadoEvent.class);
        aprovarOrcamentoInput.execute(new AprovarOrcamentoCommand(event.ordemServicoId()));
        log.info("PagamentoAprovadoEvent processado via RabbitMQ: ordemServicoId={}", event.ordemServicoId());
    }

    @RabbitListener(queues = "${messaging.rabbit.queues.pagamento-recusado}")
    public void receivePagamentoRecusado(String body) {
        var event = readEvent(body, PagamentoRecusadoEvent.class);
        recusarOrcamentoInput.execute(new RecusarOrcamentoCommand(event.ordemServicoId(), event.motivo()));
        log.info("PagamentoRecusadoEvent processado via RabbitMQ: ordemServicoId={}", event.ordemServicoId());
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
