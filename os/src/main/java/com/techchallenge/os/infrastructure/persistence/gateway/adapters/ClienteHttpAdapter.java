package com.techchallenge.os.infrastructure.persistence.gateway.adapters;

import com.techchallenge.os.application.gateway.ports.output.ClienteGateway;
import com.techchallenge.os.application.gateway.ports.output.models.ClienteResumo;
import com.techchallenge.os.infrastructure.persistence.gateway.feign.CatalogClienteClient;
import com.techchallenge.os.infrastructure.persistence.gateway.feign.dto.ClienteCatalogResponse;
import com.techchallenge.os.infrastructure.persistence.gateway.feign.dto.PageResponse;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * Implementação do ClienteGateway via HTTP.
 * Delega as chamadas ao catalog-service usando Feign Client.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ClienteHttpAdapter implements ClienteGateway {

    private final CatalogClienteClient catalogClienteClient;

    @Override
    public Optional<ClienteResumo> findById(UUID id) {
        log.debug("Buscando Cliente por ID no catalog-service: {}", id);
        try {
            ClienteCatalogResponse response = catalogClienteClient.buscarPorId(id);
            return Optional.of(toResumo(response));
        } catch (FeignException.NotFound e) {
            log.debug("Cliente não encontrado no catalog-service para ID: {}", id);
            return Optional.empty();
        }
    }

    @Override
    public Optional<ClienteResumo> findByCPF(String cpf) {
        log.debug("Buscando Cliente por CPF no catalog-service: {}", cpf);
        try {
            PageResponse<ClienteCatalogResponse> page =
                    catalogClienteClient.buscarPorFiltro(cpf, null, 0, 1);
            return page.getContent().stream()
                    .findFirst()
                    .map(this::toResumo);
        } catch (FeignException e) {
            log.warn("Erro ao buscar Cliente por CPF no catalog-service: {}", e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<ClienteResumo> findByCNPJ(String cnpj) {
        log.debug("Buscando Cliente por CNPJ no catalog-service: {}", cnpj);
        try {
            PageResponse<ClienteCatalogResponse> page =
                    catalogClienteClient.buscarPorFiltro(null, cnpj, 0, 1);
            return page.getContent().stream()
                    .findFirst()
                    .map(this::toResumo);
        } catch (FeignException e) {
            log.warn("Erro ao buscar Cliente por CNPJ no catalog-service: {}", e.getMessage());
            return Optional.empty();
        }
    }

    private ClienteResumo toResumo(ClienteCatalogResponse dto) {
        return ClienteResumo.builder()
                .id(dto.getId())
                .nome(dto.getNome())
                .cpf(dto.getCpf())
                .cnpj(dto.getCnpj())
                .email(dto.getEmail())
                .status(dto.getStatus())
                .build();
    }
}
