package com.techchallenge.os.application.commands;

import com.techchallenge.os.domain.exceptions.ValidacaoOrdemServicoException;
import lombok.Getter;

import java.util.UUID;

@Getter
public class EntregarOrdemServicoCommand {
    
    private final UUID ordemServicoId;
    
    public EntregarOrdemServicoCommand(UUID ordemServicoId) {
        validate(ordemServicoId);
        
        this.ordemServicoId = ordemServicoId;
    }
    
    private void validate(UUID ordemServicoId) {
        if (ordemServicoId == null) {
            throw new ValidacaoOrdemServicoException("Ordem de Serviço ID é obrigatório");
        }
    }
}
