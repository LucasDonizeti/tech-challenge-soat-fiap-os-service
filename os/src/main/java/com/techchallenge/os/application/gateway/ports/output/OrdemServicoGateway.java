package com.techchallenge.os.application.gateway.ports.output;

import com.techchallenge.os.domain.models.OrdemServico;
import com.techchallenge.os.domain.models.valueobjects.StatusOS;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface OrdemServicoGateway {
    OrdemServico save(OrdemServico ordemServico);

    Optional<OrdemServico> findById(UUID id);

    Page<OrdemServico> findAll(Pageable pageable);

    Page<OrdemServico> findByFilters(UUID clienteId, UUID veiculoId, StatusOS status,
                                     LocalDateTime dataInicio, LocalDateTime dataFim, Pageable pageable);



}
