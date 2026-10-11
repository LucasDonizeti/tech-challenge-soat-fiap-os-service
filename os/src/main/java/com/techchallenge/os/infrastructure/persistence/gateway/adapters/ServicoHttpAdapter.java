package com.techchallenge.os.infrastructure.persistence.gateway.adapters;

import com.techchallenge.os.application.gateway.ports.output.ServicoGateway;
import com.techchallenge.os.application.gateway.ports.output.models.ServicoResumo;

import java.util.Optional;
import java.util.UUID;

public class ServicoHttpAdapter implements ServicoGateway {
    @Override
    public Optional<ServicoResumo> findById(UUID id) {
        return Optional.empty();
    }

    @Override
    public Optional<ServicoResumo> buscarPorCodigo(String codigo) {
        return Optional.empty();
    }
}
