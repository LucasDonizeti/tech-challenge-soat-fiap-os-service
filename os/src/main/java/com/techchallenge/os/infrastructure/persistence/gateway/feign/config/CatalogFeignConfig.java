package com.techchallenge.os.infrastructure.persistence.gateway.feign.config;

import com.techchallenge.os.domain.exceptions.ValidacaoClienteException;
import com.techchallenge.os.domain.exceptions.ValidacaoMROException;
import com.techchallenge.os.domain.exceptions.ValidacaoServicoException;
import com.techchallenge.os.domain.exceptions.ValidacaoVeiculoException;
import com.techchallenge.os.infrastructure.security.ServiceJwtTokenProvider;
import feign.RequestInterceptor;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Autentica chamadas Feign ao catálogo como serviço e traduz erros HTTP em exceções de domínio.
 */
@Configuration
public class CatalogFeignConfig {

    @Bean
    public RequestInterceptor serviceJwtInterceptor(ServiceJwtTokenProvider tokenProvider) {
        return requestTemplate -> requestTemplate.header(
                "Authorization",
                "Bearer " + tokenProvider.createToken()
        );
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
