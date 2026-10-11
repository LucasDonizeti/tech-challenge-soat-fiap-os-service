package com.techchallenge.os.infrastructure.persistence.gateway.persistences;

import com.techchallenge.os.application.gateway.ports.output.OrdemServicoGateway;
import com.techchallenge.os.domain.models.OrdemServico;
import com.techchallenge.os.domain.models.valueobjects.StatusOS;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public class OrdemServicoPersistenceAdapter implements OrdemServicoGateway {
    @Override
    public OrdemServico save(OrdemServico ordemServico) {
        return null;
    }

    @Override
    public Optional<OrdemServico> findById(UUID id) {
        return Optional.empty();
    }

    @Override
    public Page<OrdemServico> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public Page<OrdemServico> findByFilters(UUID clienteId, UUID veiculoId, StatusOS status, LocalDateTime dataInicio, LocalDateTime dataFim, Pageable pageable) {
        return null;
    }
}
