package com.techchallenge.os.infrastructure.persistence.gateway.feign.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO auxiliar para deserializar Page<T> do Spring retornado pelos endpoints paginados do catalog-service.
 * O Feign não desserializa Page<T> diretamente — este DTO extrai apenas os campos necessários.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
    private List<T> content;
    private int totalPages;
    private long totalElements;
    private int number;
    private int size;
}
