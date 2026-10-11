package com.techchallenge.os.application.gateway.ports.output;

import com.techchallenge.os.application.gateway.ports.output.models.MROResumo;

import java.util.Optional;
import java.util.UUID;

public interface MroGateway {
    Optional<MROResumo> findById(UUID id);
    Optional<MROResumo> debitarEstoque(UUID id, Integer quantidade);
    Optional<MROResumo> buscarPorCodigo(String codigo);

}
