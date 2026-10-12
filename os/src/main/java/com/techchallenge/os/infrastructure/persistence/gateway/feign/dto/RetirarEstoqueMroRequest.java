package com.techchallenge.os.infrastructure.persistence.gateway.feign.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de request para POST /v1/admin/mros/{id}/estoque/retirar no catalog-service.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RetirarEstoqueMroRequest {
    private Integer quantidade;
}
