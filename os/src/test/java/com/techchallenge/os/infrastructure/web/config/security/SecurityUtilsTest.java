package com.techchallenge.os.infrastructure.web.config.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Testes de SecurityUtils - Infrastructure Layer")
class SecurityUtilsTest {

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Deve retornar CPF quando JWT está presente")
    void deveRetornarCPFQuandoJWTEstaPresente() {
        // Arrange
        Jwt jwt = mock(Jwt.class);
        when(jwt.getSubject()).thenReturn("52998224725");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(jwt);

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(context);

        // Act
        String cpf = SecurityUtils.getUsuarioCpf();

        // Assert
        assertEquals("52998224725", cpf);
    }

    @Test
    @DisplayName("Deve retornar null quando não há autenticação")
    void deveRetornarNullQuandoNaoHaAutenticacao() {
        // Arrange
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(context);

        // Act
        String cpf = SecurityUtils.getUsuarioCpf();

        // Assert
        assertNull(cpf);
    }

    @Test
    @DisplayName("Deve retornar null quando principal não é JWT")
    void deveRetornarNullQuandoPrincipalNaoEJWT() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn("not-a-jwt");

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(context);

        // Act
        String cpf = SecurityUtils.getUsuarioCpf();

        // Assert
        assertNull(cpf);
    }

    @Test
    @DisplayName("Deve retornar clienteId quando claim está presente")
    void deveRetornarClienteIdQuandoClaimEstaPresente() {
        // Arrange
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaim("clienteId")).thenReturn("cliente-123");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(jwt);

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(context);

        // Act
        String clienteId = SecurityUtils.getClienteId();

        // Assert
        assertEquals("cliente-123", clienteId);
    }

    @Test
    @DisplayName("Deve retornar null quando claim clienteId não está presente")
    void deveRetornarNullQuandoClaimClienteIdNaoEstaPresente() {
        // Arrange
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaim("clienteId")).thenReturn(null);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(jwt);

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(context);

        // Act
        String clienteId = SecurityUtils.getClienteId();

        // Assert
        assertNull(clienteId);
    }

    @Test
    @DisplayName("Deve retornar null quando não há autenticação ao buscar clienteId")
    void deveRetornarNullQuandoNaoHaAutenticacaoAoBuscarClienteId() {
        // Arrange
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(context);

        // Act
        String clienteId = SecurityUtils.getClienteId();

        // Assert
        assertNull(clienteId);
    }
}
