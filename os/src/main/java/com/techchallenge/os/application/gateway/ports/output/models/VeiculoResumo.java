package com.techchallenge.os.application.gateway.ports.output.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;


@Getter
@Setter
@Builder
public class VeiculoResumo {
    private UUID id;

    private String placa;

    private String marca;

    private String modelo;

    private Integer ano;

    private String cor;

    private String status;

    private UUID clienteId;
}
