package com.techchallenge.os.infrastructure.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
@ConditionalOnProperty(name = "messaging.type", havingValue = "rabbit")
public class RabbitMessagingConfig {

    @Bean
    TopicExchange billingEventsExchange(@Value("${messaging.rabbit.exchange}") String exchangeName) {
        return new TopicExchange(exchangeName, true, false);
    }

    @Bean
    Queue osCobrancaPixGeradaQueue(
            @Value("${messaging.rabbit.queues.cobranca-pix-gerada}") String queueName) {
        return new Queue(queueName, true);
    }

    @Bean
    Queue osPagamentoAprovadoQueue(
            @Value("${messaging.rabbit.queues.pagamento-aprovado}") String queueName) {
        return new Queue(queueName, true);
    }

    @Bean
    Queue osPagamentoRecusadoQueue(
            @Value("${messaging.rabbit.queues.pagamento-recusado}") String queueName) {
        return new Queue(queueName, true);
    }

    @Bean
    Binding osCobrancaPixGeradaBinding(
            @Qualifier("osCobrancaPixGeradaQueue") Queue osCobrancaPixGeradaQueue,
            TopicExchange billingEventsExchange,
            @Value("${messaging.rabbit.routing-keys.cobranca-pix-gerada}") String routingKey) {
        return BindingBuilder.bind(osCobrancaPixGeradaQueue).to(billingEventsExchange).with(routingKey);
    }

    @Bean
    Binding osPagamentoAprovadoBinding(
            @Qualifier("osPagamentoAprovadoQueue") Queue osPagamentoAprovadoQueue,
            TopicExchange billingEventsExchange,
            @Value("${messaging.rabbit.routing-keys.pagamento-aprovado}") String routingKey) {
        return BindingBuilder.bind(osPagamentoAprovadoQueue).to(billingEventsExchange).with(routingKey);
    }

    @Bean
    Binding osPagamentoRecusadoBinding(
            @Qualifier("osPagamentoRecusadoQueue") Queue osPagamentoRecusadoQueue,
            TopicExchange billingEventsExchange,
            @Value("${messaging.rabbit.routing-keys.pagamento-recusado}") String routingKey) {
        return BindingBuilder.bind(osPagamentoRecusadoQueue).to(billingEventsExchange).with(routingKey);
    }
}
