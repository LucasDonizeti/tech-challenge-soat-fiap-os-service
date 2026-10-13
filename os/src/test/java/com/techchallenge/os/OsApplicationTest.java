package com.techchallenge.os;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de OsApplication - Application Boot")
class OsApplicationTest {

    @Test
    @DisplayName("Deve ter classe de aplicação definida")
    void deveTerClasseDeAplicacaoDefinida() {
        // Assert
        assertNotNull(OsApplication.class);
    }

    @Test
    @DisplayName("Deve ter anotação SpringBootApplication")
    void deveTerAnotacaoSpringBootApplication() {
        // Assert
        assertTrue(OsApplication.class.isAnnotationPresent(org.springframework.boot.autoconfigure.SpringBootApplication.class));
    }

    @Test
    @DisplayName("Deve ter anotação EnableFeignClients")
    void deveTerAnotacaoEnableFeignClients() {
        // Assert
        assertTrue(OsApplication.class.isAnnotationPresent(org.springframework.cloud.openfeign.EnableFeignClients.class));
    }

    @Test
    @DisplayName("Deve ter anotação EnableSpringDataWebSupport")
    void deveTerAnotacaoEnableSpringDataWebSupport() {
        // Assert
        assertTrue(OsApplication.class.isAnnotationPresent(org.springframework.data.web.config.EnableSpringDataWebSupport.class));
    }
}
