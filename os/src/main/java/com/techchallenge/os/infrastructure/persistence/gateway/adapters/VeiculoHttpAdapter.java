package com.techchallenge.os.infrastructure.persistence.gateway.adapters;

import com.techchallenge.os.application.gateway.ports.output.VeiculoGateway;
import com.techchallenge.os.application.gateway.ports.output.models.VeiculoResumo;

import java.util.Optional;
import java.util.UUID;

public class VeiculoHttpAdapter implements VeiculoGateway {

    @Override
    public Optional<VeiculoResumo> findById(UUID id) {
        return Optional.empty();
    }

    @Override
    public Optional<VeiculoResumo> findByPlaca(String placa) {
        return Optional.empty();
    }
}
