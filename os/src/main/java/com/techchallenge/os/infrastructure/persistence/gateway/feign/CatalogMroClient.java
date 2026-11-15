package com.techchallenge.os.infrastructure.persistence.gateway.feign;

import com.techchallenge.os.infrastructure.persistence.gateway.feign.config.CatalogFeignConfig;
import com.techchallenge.os.infrastructure.persistence.gateway.feign.dto.MroCatalogResponse;
import com.techchallenge.os.infrastructure.persistence.gateway.feign.dto.RetirarEstoqueMroRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

/**
 * Feign Client para o recurso MRO do catalog-service.
 * Endpoints consumidos pelo os-service:
 *  - GET  /v1/admin/mros/{id}                  → findById
 *  - GET  /v1/admin/mros/codigo/{codigo}        → buscarPorCodigo
 *  - POST /v1/admin/mros/{id}/estoque/retirar   → debitarEstoque
 */
@FeignClient(
        name = "catalog-mro",
        url = "${catalog.service.url}",
        configuration = CatalogFeignConfig.class
)
public interface CatalogMroClient {

    @GetMapping("/v1/admin/mros/{id}")
    MroCatalogResponse buscarPorId(@PathVariable("id") UUID id);

    @GetMapping("/v1/admin/mros/codigo/{codigo}")
    MroCatalogResponse buscarPorCodigo(@PathVariable("codigo") String codigo);

    @PostMapping("/v1/admin/mros/{id}/estoque/retirar")
    MroCatalogResponse retirarEstoque(
            @PathVariable("id") UUID id,
            @RequestBody RetirarEstoqueMroRequest request
    );
}
