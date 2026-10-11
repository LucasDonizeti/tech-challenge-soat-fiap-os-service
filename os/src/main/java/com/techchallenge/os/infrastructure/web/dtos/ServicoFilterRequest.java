package com.techchallenge.os.infrastructure.web.dtos;

import lombok.Data;

@Data
public class ServicoFilterRequest {
    
    private String nome;
    private Boolean ativo;
}
