package com.techchallenge.os.infrastructure.persistence.gateway.feign.config;

import com.techchallenge.os.domain.exceptions.ValidacaoClienteException;
import com.techchallenge.os.domain.exceptions.ValidacaoMROException;
import com.techchallenge.os.domain.exceptions.ValidacaoServicoException;
import com.techchallenge.os.domain.exceptions.ValidacaoVeiculoException;
import feign.Request;
import feign.RequestTemplate;
import feign.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de CatalogFeignConfig - Infrastructure Layer")
class CatalogFeignConfigTest {

    private CatalogFeignConfig catalogFeignConfig;
    private CatalogFeignConfig.CatalogErrorDecoder errorDecoder;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private JwtAuthenticationToken jwtAuthenticationToken;

    @Mock
    private Jwt jwt;

    @BeforeEach
    void setUp() {
        catalogFeignConfig = new CatalogFeignConfig();
        errorDecoder = (CatalogFeignConfig.CatalogErrorDecoder) catalogFeignConfig.catalogErrorDecoder();
    }

    @Test
    @DisplayName("Deve criar RequestInterceptor que propaga token JWT")
    void deveCriarRequestInterceptorQuePropagaTokenJWT() {
        // Arrange
        String tokenValue = "test-jwt-token";
        when(jwt.getTokenValue()).thenReturn(tokenValue);
        when(jwtAuthenticationToken.getToken()).thenReturn(jwt);
        when(securityContext.getAuthentication()).thenReturn(jwtAuthenticationToken);
        SecurityContextHolder.setContext(securityContext);

        // Act
        feign.RequestInterceptor interceptor = catalogFeignConfig.jwtPropagationInterceptor();
        RequestTemplate template = new RequestTemplate();
        interceptor.apply(template);

        // Assert
        assertEquals("Bearer " + tokenValue, template.headers().get("Authorization").iterator().next());

        // Cleanup
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Deve criar RequestInterceptor sem token quando autenticação não é JWT")
    void deveCriarRequestInterceptorSemTokenQuandoAutenticacaoNaoEJWT() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        // Act
        feign.RequestInterceptor interceptor = catalogFeignConfig.jwtPropagationInterceptor();
        RequestTemplate template = new RequestTemplate();
        interceptor.apply(template);

        // Assert
        assertNull(template.headers().get("Authorization"));

        // Cleanup
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Deve decodificar erro 404 para cliente não encontrado")
    void deveDecodificarErro404ParaClienteNaoEncontrado() {
        // Arrange
        String methodKey = "CatalogClienteClient#buscarPorId";
        Response response = createResponse(404);

        // Act
        Exception exception = errorDecoder.decode(methodKey, response);

        // Assert
        assertTrue(exception instanceof ValidacaoClienteException);
        assertEquals("Cliente não encontrado no catalog-service", exception.getMessage());
    }

    @Test
    @DisplayName("Deve decodificar erro 404 para veículo não encontrado")
    void deveDecodificarErro404ParaVeiculoNaoEncontrado() {
        // Arrange
        String methodKey = "CatalogVeiculoClient#buscarPorPlaca";
        Response response = createResponse(404);

        // Act
        Exception exception = errorDecoder.decode(methodKey, response);

        // Assert
        assertTrue(exception instanceof ValidacaoVeiculoException);
        assertEquals("Veículo não encontrado no catalog-service", exception.getMessage());
    }

    @Test
    @DisplayName("Deve decodificar erro 404 para MRO não encontrado")
    void deveDecodificarErro404ParaMRONaoEncontrado() {
        // Arrange
        String methodKey = "CatalogMROClient#buscarPorCodigo";
        Response response = createResponse(404);

        // Act
        Exception exception = errorDecoder.decode(methodKey, response);

        // Assert
        assertTrue(exception instanceof ValidacaoMROException);
        assertEquals("MRO não encontrado no catalog-service", exception.getMessage());
    }

    @Test
    @DisplayName("Deve decodificar erro 404 para serviço não encontrado")
    void deveDecodificarErro404ParaServicoNaoEncontrado() {
        // Arrange
        String methodKey = "CatalogServicoClient#buscarPorCodigo";
        Response response = createResponse(404);

        // Act
        Exception exception = errorDecoder.decode(methodKey, response);

        // Assert
        assertTrue(exception instanceof ValidacaoServicoException);
        assertEquals("Serviço não encontrado no catalog-service", exception.getMessage());
    }

    @Test
    @DisplayName("Deve decodificar erro 404 genérico quando não identificar recurso")
    void deveDecodificarErro404GenericoQuandoNaoIdentificarRecurso() {
        // Arrange
        String methodKey = "GenericClient#buscarGenerico";
        Response response = createResponse(404);

        // Act
        Exception exception = errorDecoder.decode(methodKey, response);

        // Assert
        assertTrue(exception instanceof RuntimeException);
        assertTrue(exception.getMessage().contains("Recurso não encontrado no catalog-service"));
        assertTrue(exception.getMessage().contains(methodKey));
    }

    @Test
    @DisplayName("Deve decodificar erro 400 para requisição inválida")
    void deveDecodificarErro400ParaRequisicaoInvalida() {
        // Arrange
        String methodKey = "CatalogClienteClient#buscarPorId";
        Response response = createResponse(400);

        // Act
        Exception exception = errorDecoder.decode(methodKey, response);

        // Assert
        assertTrue(exception instanceof IllegalArgumentException);
        assertTrue(exception.getMessage().contains("Requisição inválida ao catalog-service"));
        assertTrue(exception.getMessage().contains(methodKey));
    }

    @Test
    @DisplayName("Deve decodificar erro 503 para serviço indisponível")
    void deveDecodificarErro503ParaServicoIndisponivel() {
        // Arrange
        String methodKey = "CatalogClienteClient#buscarPorId";
        Response response = createResponse(503);

        // Act
        Exception exception = errorDecoder.decode(methodKey, response);

        // Assert
        assertTrue(exception instanceof RuntimeException);
        assertTrue(exception.getMessage().contains("catalog-service indisponível"));
        assertTrue(exception.getMessage().contains(methodKey));
    }

    @Test
    @DisplayName("Deve decodificar erro 502 para serviço indisponível")
    void deveDecodificarErro502ParaServicoIndisponivel() {
        // Arrange
        String methodKey = "CatalogClienteClient#buscarPorId";
        Response response = createResponse(502);

        // Act
        Exception exception = errorDecoder.decode(methodKey, response);

        // Assert
        assertTrue(exception instanceof RuntimeException);
        assertTrue(exception.getMessage().contains("catalog-service indisponível"));
        assertTrue(exception.getMessage().contains(methodKey));
    }

    @Test
    @DisplayName("Deve decodificar erro 504 para serviço indisponível")
    void deveDecodificarErro504ParaServicoIndisponivel() {
        // Arrange
        String methodKey = "CatalogClienteClient#buscarPorId";
        Response response = createResponse(504);

        // Act
        Exception exception = errorDecoder.decode(methodKey, response);

        // Assert
        assertTrue(exception instanceof RuntimeException);
        assertTrue(exception.getMessage().contains("catalog-service indisponível"));
        assertTrue(exception.getMessage().contains(methodKey));
    }

    @Test
    @DisplayName("Deve usar decoder padrão para status não mapeado")
    void deveUsarDecoderPadraoParaStatusNaoMapeado() {
        // Arrange
        String methodKey = "CatalogClienteClient#buscarPorId";
        Response response = createResponse(500);

        // Act
        Exception exception = errorDecoder.decode(methodKey, response);

        // Assert
        assertNotNull(exception);
    }

    private Response createResponse(int status) {
        Request request = Request.create(
                Request.HttpMethod.GET,
                "http://localhost:8080/api/clientes/1",
                Collections.emptyMap(),
                Request.Body.empty(),
                null
        );

        return Response.builder()
                .status(status)
                .reason("HTTP " + status)
                .request(request)
                .headers(Collections.emptyMap())
                .body(String.valueOf(new byte[0]), StandardCharsets.UTF_8)
                .build();
    }
}
