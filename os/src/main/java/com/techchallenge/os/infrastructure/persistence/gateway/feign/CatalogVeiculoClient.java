package com.techchallenge.os.infrastructure.persistence.gateway.feign;

import com.techchallenge.os.infrastructure.persistence.gateway.feign.config.CatalogFeignConfig;
import com.techchallenge.os.infrastructure.persistence.gateway.feign.dto.VeiculoCatalogResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

/**
 * Feign Client para o recurso Veículo do catalog-service.
 * Endpoints consumidos pelo os-service:
 *  - GET /v1/admin/veiculos/{id}          → findById
 *  - GET /v1/admin/veiculos/placa/{placa} → findByPlaca
 */
@FeignClient(
        name = "catalog-veiculo",
        url = "${catalog.service.url}",
        configuration = CatalogFeignConfig.class
)
public interface CatalogVeiculoClient {

    @GetMapping("/v1/admin/veiculos/{id}")
    VeiculoCatalogResponse buscarPorId(@PathVariable("id") UUID id);

    @GetMapping("/v1/admin/veiculos/placa/{placa}")
    VeiculoCatalogResponse buscarPorPlaca(@PathVariable("placa") String placa);
}
