package com.techchallenge.os.infrastructure.persistence.gateway.feign;

import com.techchallenge.os.infrastructure.persistence.gateway.feign.config.CatalogFeignConfig;
import com.techchallenge.os.infrastructure.persistence.gateway.feign.dto.ServicoCatalogResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

/**
 * Feign Client para o recurso Serviço do catalog-service.
 * Endpoints consumidos pelo os-service:
 *  - GET /v1/admin/servicos/{id}           → findById
 *  - GET /v1/admin/servicos/codigo/{codigo} → buscarPorCodigo
 */
@FeignClient(
        name = "catalog-servico",
        url = "${catalog.service.url}",
        configuration = CatalogFeignConfig.class
)
public interface CatalogServicoClient {

    @GetMapping("/v1/admin/servicos/{id}")
    ServicoCatalogResponse buscarPorId(@PathVariable("id") UUID id);

    @GetMapping("/v1/admin/servicos/codigo/{codigo}")
    ServicoCatalogResponse buscarPorCodigo(@PathVariable("codigo") String codigo);
}
