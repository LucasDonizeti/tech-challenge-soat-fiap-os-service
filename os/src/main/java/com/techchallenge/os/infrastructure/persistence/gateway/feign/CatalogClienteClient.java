package com.techchallenge.os.infrastructure.persistence.gateway.feign;

import com.techchallenge.os.infrastructure.persistence.gateway.feign.config.CatalogFeignConfig;
import com.techchallenge.os.infrastructure.persistence.gateway.feign.dto.ClienteCatalogResponse;
import com.techchallenge.os.infrastructure.persistence.gateway.feign.dto.PageResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

/**
 * Feign Client para o recurso Cliente do catalog-service.
 * Endpoints consumidos pelo os-service:
 *  - GET /v1/admin/clientes/{id}         → findById
 *  - GET /v1/admin/clientes/buscar        → findByCPF / findByCNPJ
 */
@FeignClient(
        name = "catalog-cliente",
        url = "${catalog.service.url}",
        configuration = CatalogFeignConfig.class
)
public interface CatalogClienteClient {

    @GetMapping("/v1/admin/clientes/{id}")
    ClienteCatalogResponse buscarPorId(@PathVariable("id") UUID id);

    @GetMapping("/v1/admin/clientes/buscar")
    PageResponse<ClienteCatalogResponse> buscarPorFiltro(
            @RequestParam(value = "cpf",  required = false) String cpf,
            @RequestParam(value = "cnpj", required = false) String cnpj,
            @RequestParam("page") int page,
            @RequestParam("size") int size
    );
}
