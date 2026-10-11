package com.techchallenge.os.infrastructure.persistence.gateway.adapters;

import com.techchallenge.os.application.gateway.ports.output.ClienteGateway;
import com.techchallenge.os.application.gateway.ports.output.models.ClienteResumo;
import java.util.Optional;
import java.util.UUID;

public class ClienteHttpAdapter implements ClienteGateway {

    @Override
    public Optional<ClienteResumo> findById(UUID id) {
        return Optional.empty();
    }

    @Override
    public Optional<ClienteResumo> findByCPF(String cpf) {
        return Optional.empty();
    }

    @Override
    public Optional<ClienteResumo> findByCNPJ(String cnpj) {
        return Optional.empty();
    }
}
