package com.techchallenge.os.application.gateway.ports.output.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class ClienteResumo {
    private UUID id;

    private String nome;

    private String cpf;

    private String cnpj;

    private String email;

    private String status;
}
