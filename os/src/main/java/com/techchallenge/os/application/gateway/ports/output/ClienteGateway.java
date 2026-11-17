package com.techchallenge.os.application.gateway.ports.output;

import com.techchallenge.os.application.gateway.ports.output.models.ClienteResumo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClienteGateway {

    Optional<ClienteResumo> findById(UUID id);

    Optional<ClienteResumo> findByCPF(String cpf);

    Optional<ClienteResumo> findByCNPJ(String cnpj);

}
