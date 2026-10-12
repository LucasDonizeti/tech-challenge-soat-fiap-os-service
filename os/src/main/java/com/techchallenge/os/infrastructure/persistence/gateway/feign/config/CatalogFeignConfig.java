package com.techchallenge.os.infrastructure.persistence.gateway.feign.config;

import com.techchallenge.os.domain.exceptions.ValidacaoClienteException;
import com.techchallenge.os.domain.exceptions.ValidacaoMROException;
import com.techchallenge.os.domain.exceptions.ValidacaoServicoException;
import com.techchallenge.os.domain.exceptions.ValidacaoVeiculoException;
import feign.RequestInterceptor;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

/**
 * Configuração Feign compartilhada entre todos os clients do catalog-service.
 * Traduz erros HTTP em exceções de domínio do os-service e propaga o token JWT.
 */
@Configuration
public class CatalogFeignConfig {

    /**
     * Propaga o token JWT da requisição atual para todas as chamadas Feign ao catalog-service.
     * Lê o token do SecurityContext (populado pelo Spring Security OAuth2 Resource Server)
     * e injeta no header Authorization: Bearer <token>.
     */
    @Bean
    public RequestInterceptor jwtPropagationInterceptor() {
        return requestTemplate -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication instanceof JwtAuthenticationToken jwtAuth) {
                String tokenValue = jwtAuth.getToken().getTokenValue();
                requestTemplate.header("Authorization", "Bearer " + tokenValue);
            }
        };
    }

    @Bean
    public ErrorDecoder catalogErrorDecoder() {
        return new CatalogErrorDecoder();
    }

    static class CatalogErrorDecoder implements ErrorDecoder {

        private final ErrorDecoder defaultDecoder = new ErrorDecoder.Default();

        @Override
        public Exception decode(String methodKey, Response response) {
            return switch (response.status()) {
                case 404 -> resolveNotFoundException(methodKey);
                case 400 -> new IllegalArgumentException(
                        "Requisição inválida ao catalog-service [" + methodKey + "]");
                case 503, 502, 504 -> new RuntimeException(
                        "catalog-service indisponível [" + methodKey + "]");
                default -> defaultDecoder.decode(methodKey, response);
            };
        }

        private RuntimeException resolveNotFoundException(String methodKey) {
            String lower = methodKey.toLowerCase();
            if (lower.contains("cliente")) {
                return new ValidacaoClienteException("Cliente não encontrado no catalog-service");
            }
            if (lower.contains("veiculo")) {
                return new ValidacaoVeiculoException("Veículo não encontrado no catalog-service");
            }
            if (lower.contains("mro")) {
                return new ValidacaoMROException("MRO não encontrado no catalog-service");
            }
            if (lower.contains("servico")) {
                return new ValidacaoServicoException("Serviço não encontrado no catalog-service");
            }
            return new RuntimeException("Recurso não encontrado no catalog-service [" + methodKey + "]");
        }
    }
}
