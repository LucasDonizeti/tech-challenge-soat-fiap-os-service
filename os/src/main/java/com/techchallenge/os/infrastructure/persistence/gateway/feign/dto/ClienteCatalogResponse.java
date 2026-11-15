package com.techchallenge.os.infrastructure.persistence.gateway.feign.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO de resposta do catalog-service para Cliente.
 * Mapeado a partir de GET /v1/admin/clientes/{id}.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteCatalogResponse {
    private UUID id;
    private String nome;
    private String cpf;
    private String cnpj;
    private String email;
    private String status;
}
