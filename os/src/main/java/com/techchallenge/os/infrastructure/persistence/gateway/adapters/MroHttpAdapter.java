package com.techchallenge.os.infrastructure.persistence.gateway.adapters;

import com.techchallenge.os.application.gateway.ports.output.MroGateway;
import com.techchallenge.os.application.gateway.ports.output.models.MROResumo;

import java.util.Optional;
import java.util.UUID;

public class MroHttpAdapter implements MroGateway {
    @Override
    public Optional<MROResumo> findById(UUID id) {
        return Optional.empty();
    }

    @Override
    public Optional<MROResumo> debitarEstoque(UUID id, Integer quantidade) {
        return Optional.empty();
    }

    @Override
    public Optional<MROResumo> buscarPorCodigo(String codigo) {
        return Optional.empty();
    }
}
