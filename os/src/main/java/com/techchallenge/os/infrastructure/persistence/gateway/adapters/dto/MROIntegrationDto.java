package com.techchallenge.os.infrastructure.persistence.gateway.adapters.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MROIntegrationDto {

    private UUID id;
    private String nome;
    private String codigo;
    private String descricao;
    private String tipo; // PECA ou INSUMO
    private Integer quantidadeEstoque;
    private BigDecimal precoUnitario;
    private Boolean ativo;

    public boolean isPeca() {
        return "PECA".equals(tipo);
    }

    public boolean isInsumo() {
        return "INSUMO".equals(tipo);
    }

    public boolean temEstoqueSuficiente(Integer quantidade) {
        return quantidadeEstoque != null && quantidadeEstoque >= quantidade;
    }
}
