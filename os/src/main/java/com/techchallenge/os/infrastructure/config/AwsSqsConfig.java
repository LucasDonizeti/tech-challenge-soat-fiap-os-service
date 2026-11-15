package com.techchallenge.os.infrastructure.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "messaging.type", havingValue = "aws")
public class AwsSqsConfig {

    @Value("${aws.sqs.queues.cobranca-pix-gerada:}")
    private String cobrancaPixGeradaQueue;

    @Value("${aws.sqs.queues.pagamento-aprovado:}")
    private String pagamentoAprovadoQueue;

    @Value("${aws.sqs.queues.pagamento-recusado:}")
    private String pagamentoRecusadoQueue;

    @Value("${aws.sqs.queues.saga-initiated:}")
    private String sagaInitiatedQueue;

    @Value("${aws.sqs.queues.saga-expired:}")
    private String sagaExpiredQueue;

    @PostConstruct
    void validateQueues() {
        requireQueue(cobrancaPixGeradaQueue, "AWS_SQS_QUEUE_COBRANCA_PIX_GERADA");
        requireQueue(pagamentoAprovadoQueue, "AWS_SQS_QUEUE_PAGAMENTO_APROVADO");
        requireQueue(pagamentoRecusadoQueue, "AWS_SQS_QUEUE_PAGAMENTO_RECUSADO");
        requireQueue(sagaInitiatedQueue, "AWS_SQS_QUEUE_SAGA_INITIATED");
        requireQueue(sagaExpiredQueue, "AWS_SQS_QUEUE_SAGA_EXPIRED");
    }

    private void requireQueue(String queue, String environmentVariable) {
        if (queue == null || queue.isBlank()) {
            throw new IllegalStateException("Configure " + environmentVariable + " para iniciar o listener SQS.");
        }
    }
}
