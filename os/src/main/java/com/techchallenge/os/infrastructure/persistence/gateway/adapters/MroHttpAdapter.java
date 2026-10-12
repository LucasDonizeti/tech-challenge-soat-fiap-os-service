package com.techchallenge.os.infrastructure.persistence.gateway.adapters;

import com.techchallenge.os.application.gateway.ports.output.MroGateway;
import com.techchallenge.os.application.gateway.ports.output.models.MROResumo;
import com.techchallenge.os.infrastructure.persistence.gateway.feign.CatalogMroClient;
import com.techchallenge.os.infrastructure.persistence.gateway.feign.dto.MroCatalogResponse;
import com.techchallenge.os.infrastructure.persistence.gateway.feign.dto.PageResponse;
import com.techchallenge.os.infrastructure.persistence.gateway.feign.dto.RetirarEstoqueMroRequest;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * Implementação do MroGateway via HTTP.
 * Delega as chamadas ao catalog-service usando Feign Client.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MroHttpAdapter implements MroGateway {

    private final CatalogMroClient catalogMroClient;

    @Override
    public Optional<MROResumo> findById(UUID id) {
        log.debug("Buscando MRO por ID no catalog-service: {}", id);
        try {
            MroCatalogResponse response = catalogMroClient.buscarPorId(id);
            return Optional.of(toResumo(response));
        } catch (FeignException.NotFound e) {
            log.debug("MRO não encontrado no catalog-service para ID: {}", id);
            return Optional.empty();
        }
    }

    @Override
    public Optional<MROResumo> buscarPorCodigo(String codigo) {
        log.debug("Buscando MRO por código no catalog-service: {}", codigo);
        try {
            MroCatalogResponse response = catalogMroClient.buscarPorCodigo(codigo);
            return Optional.of(toResumo(response));
        } catch (FeignException.NotFound e) {
            log.debug("MRO não encontrado no catalog-service para código: {}", codigo);
            return Optional.empty();
        }
    }

    @Override
    public Optional<MROResumo> debitarEstoque(UUID id, Integer quantidade) {
        log.info("Debitando estoque do MRO {} no catalog-service, quantidade: {}", id, quantidade);
        try {
            MroCatalogResponse response =
                    catalogMroClient.retirarEstoque(id, new RetirarEstoqueMroRequest(quantidade));
            return Optional.of(toResumo(response));
        } catch (FeignException.NotFound e) {
            log.debug("MRO não encontrado no catalog-service ao debitar estoque, ID: {}", id);
            return Optional.empty();
        }
    }

    private MROResumo toResumo(MroCatalogResponse dto) {
        return MROResumo.builder()
                .id(dto.getId())
                .nome(dto.getNome())
                .codigo(dto.getCodigo())
                .descricao(dto.getDescricao())
                .tipo(dto.getTipo())
                .quantidadeEstoque(dto.getQuantidadeEstoque())
                .precoUnitario(dto.getPrecoUnitario())
                .ativo(dto.getAtivo())
                .build();
    }
}
