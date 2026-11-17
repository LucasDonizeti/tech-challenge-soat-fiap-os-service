package com.techchallenge.os.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class MessagingConfigTest {

    @Test
    void providesObjectMapperForMessagingAdapters() {
        ObjectMapper objectMapper = new MessagingConfig().eventObjectMapper();

        assertNotNull(objectMapper);
    }
}
