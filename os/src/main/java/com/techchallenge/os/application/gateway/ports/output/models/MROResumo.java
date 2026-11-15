package com.techchallenge.os.application.gateway.ports.output.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
public class MROResumo {
    private UUID id;

    private String nome;

    private String codigo;

    private String descricao;

    private String tipo;

    private Integer quantidadeEstoque;

    private BigDecimal precoUnitario;

    private Boolean ativo;
}
