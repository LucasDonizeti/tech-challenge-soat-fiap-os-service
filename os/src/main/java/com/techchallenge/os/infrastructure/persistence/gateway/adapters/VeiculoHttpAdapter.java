package com.techchallenge.os.infrastructure.persistence.gateway.adapters;

import com.techchallenge.os.application.gateway.ports.output.VeiculoGateway;
import com.techchallenge.os.application.gateway.ports.output.models.VeiculoResumo;
import com.techchallenge.os.infrastructure.persistence.gateway.feign.CatalogVeiculoClient;
import com.techchallenge.os.infrastructure.persistence.gateway.feign.dto.PageResponse;
import com.techchallenge.os.infrastructure.persistence.gateway.feign.dto.VeiculoCatalogResponse;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * Implementação do VeiculoGateway via HTTP.
 * Delega as chamadas ao catalog-service usando Feign Client.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class VeiculoHttpAdapter implements VeiculoGateway {

    private final CatalogVeiculoClient catalogVeiculoClient;

    @Override
    public Optional<VeiculoResumo> findById(UUID id) {
        log.debug("Buscando Veículo por ID no catalog-service: {}", id);
        try {
            VeiculoCatalogResponse response = catalogVeiculoClient.buscarPorId(id);
            return Optional.of(toResumo(response));
        } catch (FeignException.NotFound e) {
            log.debug("Veículo não encontrado no catalog-service para ID: {}", id);
            return Optional.empty();
        }
    }

    @Override
    public Optional<VeiculoResumo> findByPlaca(String placa) {
        log.debug("Buscando Veículo por placa no catalog-service: {}", placa);
        try {
            VeiculoCatalogResponse response = catalogVeiculoClient.buscarPorPlaca(placa);
            return Optional.of(toResumo(response));
        } catch (FeignException.NotFound e) {
            log.debug("Veículo não encontrado no catalog-service para placa: {}", placa);
            return Optional.empty();
        }
    }

    private VeiculoResumo toResumo(VeiculoCatalogResponse dto) {
        return VeiculoResumo.builder()
                .id(dto.getId())
                .placa(dto.getPlaca())
                .marca(dto.getMarca())
                .modelo(dto.getModelo())
                .ano(dto.getAno())
                .cor(dto.getCor())
                .status(dto.getStatus())
                .clienteId(dto.getClienteId())
                .build();
    }
}
