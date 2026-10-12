package com.techchallenge.os.infrastructure.persistence.gateway.feign.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO de resposta do catalog-service para MRO (Materiais, Reparos e Óleos).
 * Mapeado a partir de GET /v1/admin/mros/{id}.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MroCatalogResponse {
    private UUID id;
    private String nome;
    private String codigo;
    private String descricao;
    private String tipo;
    private Integer quantidadeEstoque;
    private BigDecimal precoUnitario;
    private Boolean ativo;
}
