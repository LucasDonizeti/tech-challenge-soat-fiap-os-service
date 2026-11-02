package com.techchallenge.os.infrastructure.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração automática de filas RabbitMQ para Saga Pattern (OS-Service)
 * O OS-Service apenas consome eventos, não publica para o exchange
 */
@Configuration
@ConditionalOnProperty(name = "messaging.type", havingValue = "rabbit")
public class RabbitMQSagaConfig {

    // Filas de saga (OS Service recebe)
    public static final String QUEUE_SAGA_INITIATED = "os.saga.payment.initiated";
    public static final String QUEUE_SAGA_CONFIRMED = "os.saga.payment.confirmed";
    public static final String QUEUE_SAGA_EXPIRED = "os.saga.payment.expired";

    @Bean
    public Queue sagaInitiatedQueue(
            @Value("${messaging.rabbit.queues.saga-initiated:" + QUEUE_SAGA_INITIATED + "}") String queueName) {
        return new Queue(queueName, true);
    }

    @Bean
    public Queue sagaConfirmedQueue(
            @Value("${messaging.rabbit.queues.saga-confirmed:" + QUEUE_SAGA_CONFIRMED + "}") String queueName) {
        return new Queue(queueName, true);
    }

    @Bean
    public Queue sagaExpiredQueue(
            @Value("${messaging.rabbit.queues.saga-expired:" + QUEUE_SAGA_EXPIRED + "}") String queueName) {
        return new Queue(queueName, true);
    }

    @Bean
    public Binding sagaInitiatedBinding(
            @Qualifier("sagaInitiatedQueue") Queue queue,
            TopicExchange billingEventsExchange,
            @Value("${messaging.rabbit.routing-keys.saga-initiated:saga.payment.initiated}") String routingKey) {
        return BindingBuilder.bind(queue).to(billingEventsExchange).with(routingKey);
    }

    @Bean
    public Binding sagaConfirmedBinding(
            @Qualifier("sagaConfirmedQueue") Queue queue,
            TopicExchange billingEventsExchange,
            @Value("${messaging.rabbit.routing-keys.saga-confirmed:saga.payment.confirmed}") String routingKey) {
        return BindingBuilder.bind(queue).to(billingEventsExchange).with(routingKey);
    }

    @Bean
    public Binding sagaExpiredBinding(
            @Qualifier("sagaExpiredQueue") Queue queue,
            TopicExchange billingEventsExchange,
            @Value("${messaging.rabbit.routing-keys.saga-expired:saga.payment.expired}") String routingKey) {
        return BindingBuilder.bind(queue).to(billingEventsExchange).with(routingKey);
    }
}
