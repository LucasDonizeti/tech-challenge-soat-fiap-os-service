package com.techchallenge.os.infrastructure.persistence.gateway.feign.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO de resposta do catalog-service para Serviço.
 * Mapeado a partir de GET /v1/admin/servicos/{id}.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServicoCatalogResponse {
    private UUID id;
    private String nome;
    private String codigo;
    private String descricao;
    private BigDecimal preco;
    private Boolean ativo;
}
