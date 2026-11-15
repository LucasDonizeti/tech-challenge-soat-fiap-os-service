package com.techchallenge.os.application.gateway.ports.output;

import com.techchallenge.os.application.gateway.ports.output.models.ServicoResumo;

import java.util.Optional;
import java.util.UUID;

public interface ServicoGateway {
    Optional<ServicoResumo> findById(UUID id);
    Optional<ServicoResumo> buscarPorCodigo(String codigo);
}
