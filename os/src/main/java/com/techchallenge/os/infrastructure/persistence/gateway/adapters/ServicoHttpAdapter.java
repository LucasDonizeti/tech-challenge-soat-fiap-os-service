package com.techchallenge.os.infrastructure.persistence.gateway.adapters;

import com.techchallenge.os.application.gateway.ports.output.ServicoGateway;
import com.techchallenge.os.application.gateway.ports.output.models.ServicoResumo;
import com.techchallenge.os.infrastructure.persistence.gateway.feign.CatalogServicoClient;
import com.techchallenge.os.infrastructure.persistence.gateway.feign.dto.PageResponse;
import com.techchallenge.os.infrastructure.persistence.gateway.feign.dto.ServicoCatalogResponse;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * Implementação do ServicoGateway via HTTP.
 * Delega as chamadas ao catalog-service usando Feign Client.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ServicoHttpAdapter implements ServicoGateway {

    private final CatalogServicoClient catalogServicoClient;

    @Override
    public Optional<ServicoResumo> findById(UUID id) {
        log.debug("Buscando Serviço por ID no catalog-service: {}", id);
        try {
            ServicoCatalogResponse response = catalogServicoClient.buscarPorId(id);
            return Optional.of(toResumo(response));
        } catch (FeignException.NotFound e) {
            log.debug("Serviço não encontrado no catalog-service para ID: {}", id);
            return Optional.empty();
        }
    }

    @Override
    public Optional<ServicoResumo> buscarPorCodigo(String codigo) {
        log.debug("Buscando Serviço por código no catalog-service: {}", codigo);
        try {
            ServicoCatalogResponse response = catalogServicoClient.buscarPorCodigo(codigo);
            return Optional.of(toResumo(response));
        } catch (FeignException.NotFound e) {
            log.debug("Serviço não encontrado no catalog-service para código: {}", codigo);
            return Optional.empty();
        }
    }

    private ServicoResumo toResumo(ServicoCatalogResponse dto) {
        return ServicoResumo.builder()
                .id(dto.getId())
                .nome(dto.getNome())
                .codigo(dto.getCodigo())
                .descricao(dto.getDescricao())
                .preco(dto.getPreco())
                .ativo(dto.getAtivo())
                .build();
    }
}
