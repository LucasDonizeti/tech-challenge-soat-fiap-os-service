package com.techchallenge.os.infrastructure.persistence.gateway.feign.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO de resposta do catalog-service para Veículo.
 * Mapeado a partir de GET /v1/admin/veiculos/{id}.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VeiculoCatalogResponse {
    private UUID id;
    private String placa;
    private String marca;
    private String modelo;
    private Integer ano;
    private String cor;
    private String status;
    private UUID clienteId;
}
