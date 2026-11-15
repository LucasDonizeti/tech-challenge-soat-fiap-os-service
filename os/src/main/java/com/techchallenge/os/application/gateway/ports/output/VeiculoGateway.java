package com.techchallenge.os.application.gateway.ports.output;

import com.techchallenge.os.application.gateway.ports.output.models.VeiculoResumo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VeiculoGateway {

    Optional<VeiculoResumo> findById(UUID id);

    Optional<VeiculoResumo> findByPlaca(String placa);

}
