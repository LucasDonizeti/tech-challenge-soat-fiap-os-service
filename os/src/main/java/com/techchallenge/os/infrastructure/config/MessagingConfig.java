package com.techchallenge.os.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techchallenge.os.application.gateway.ports.output.OsEventPublisher;
import com.techchallenge.os.infrastructure.messaging.LocalOsEventPublisher;
import com.techchallenge.os.infrastructure.messaging.SnsOsEventPublisher;
import io.awspring.cloud.sns.core.SnsTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class MessagingConfig {

    @Bean
    @ConditionalOnMissingBean(ObjectMapper.class)
    public ObjectMapper eventObjectMapper() {
        return new ObjectMapper().findAndRegisterModules();
    }

    @Bean
    @ConditionalOnProperty(name = "messaging.type", havingValue = "local")
    public OsEventPublisher localOsEventPublisher() {
        log.info("Inicializando publicador local de eventos da OS.");
        return new LocalOsEventPublisher();
    }

    @Bean
    @ConditionalOnProperty(name = "messaging.type", havingValue = "aws")
    public OsEventPublisher snsOsEventPublisher(SnsTemplate snsTemplate, ObjectMapper objectMapper) {
        log.info("Inicializando publicador SNS de eventos da OS.");
        return new SnsOsEventPublisher(snsTemplate, objectMapper);
    }
}
