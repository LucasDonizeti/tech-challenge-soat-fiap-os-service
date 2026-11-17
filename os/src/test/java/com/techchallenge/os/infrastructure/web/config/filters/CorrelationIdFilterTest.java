package com.techchallenge.os.infrastructure.web.config.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de CorrelationIdFilter - Infrastructure Layer")
class CorrelationIdFilterTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private CorrelationIdFilter filter;

    @BeforeEach
    void setUp() {
        MDC.clear();
    }

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    @DisplayName("Deve gerar correlation ID quando não presente no header")
    void deveGerarCorrelationIdQuandoNaoPresenteNoHeader() throws ServletException, IOException {
        // Arrange
        when(request.getHeader(CorrelationIdFilter.CORRELATION_ID_HEADER)).thenReturn(null);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(response).setHeader(eq(CorrelationIdFilter.CORRELATION_ID_HEADER), anyString());
        verify(filterChain).doFilter(request, response);
        // MDC é limpo no finally, então não verificamos após execução
    }

    @Test
    @DisplayName("Deve usar correlation ID do header quando presente")
    void deveUsarCorrelationIdDoHeaderQuandoPresente() throws ServletException, IOException {
        // Arrange
        String correlationId = "existing-correlation-id";
        when(request.getHeader(CorrelationIdFilter.CORRELATION_ID_HEADER)).thenReturn(correlationId);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(response).setHeader(CorrelationIdFilter.CORRELATION_ID_HEADER, correlationId);
        verify(filterChain).doFilter(request, response);
        // MDC é limpo no finally, então não verificamos após execução
    }

    @Test
    @DisplayName("Deve gerar correlation ID quando header está vazio")
    void deveGerarCorrelationIdQuandoHeaderEstaVazio() throws ServletException, IOException {
        // Arrange
        when(request.getHeader(CorrelationIdFilter.CORRELATION_ID_HEADER)).thenReturn("");

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(response).setHeader(eq(CorrelationIdFilter.CORRELATION_ID_HEADER), anyString());
        verify(filterChain).doFilter(request, response);
        // MDC é limpo no finally, então não verificamos após execução
    }

    @Test
    @DisplayName("Deve gerar correlation ID quando header está em branco")
    void deveGerarCorrelationIdQuandoHeaderEstaEmBranco() throws ServletException, IOException {
        // Arrange
        when(request.getHeader(CorrelationIdFilter.CORRELATION_ID_HEADER)).thenReturn("   ");

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(response).setHeader(eq(CorrelationIdFilter.CORRELATION_ID_HEADER), anyString());
        verify(filterChain).doFilter(request, response);
        // MDC é limpo no finally, então não verificamos após execução
    }

    @Test
    @DisplayName("Deve limpar MDC mesmo quando ocorre exceção")
    void deveLimparMDCMesmoQuandoOcorreExcecao() throws ServletException, IOException {
        // Arrange
        when(request.getHeader(CorrelationIdFilter.CORRELATION_ID_HEADER)).thenReturn(null);
        doThrow(new RuntimeException("Test exception")).when(filterChain).doFilter(request, response);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> filter.doFilterInternal(request, response, filterChain));
        assertNull(MDC.get(CorrelationIdFilter.MDC_KEY)); // Deve ser limpo mesmo com exceção
    }

    @Test
    @DisplayName("Deve propagar correlation ID para response")
    void devePropagarCorrelationIdParaResponse() throws ServletException, IOException {
        // Arrange
        String correlationId = "test-correlation-id";
        when(request.getHeader(CorrelationIdFilter.CORRELATION_ID_HEADER)).thenReturn(correlationId);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(response).setHeader(CorrelationIdFilter.CORRELATION_ID_HEADER, correlationId);
    }

    @Test
    @DisplayName("Deve ter constante de header definida")
    void deveTerConstanteDeHeaderDefinida() {
        // Assert
        assertEquals("x-correlation-id", CorrelationIdFilter.CORRELATION_ID_HEADER);
    }

    @Test
    @DisplayName("Deve ter constante de MDC definida")
    void deveTerConstanteDeMDCDefinida() {
        // Assert
        assertEquals("correlationId", CorrelationIdFilter.MDC_KEY);
    }

    @Test
    @DisplayName("Deve executar filter chain")
    void deveExecutarFilterChain() throws ServletException, IOException {
        // Arrange
        when(request.getHeader(CorrelationIdFilter.CORRELATION_ID_HEADER)).thenReturn("test-id");

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
    }
}
