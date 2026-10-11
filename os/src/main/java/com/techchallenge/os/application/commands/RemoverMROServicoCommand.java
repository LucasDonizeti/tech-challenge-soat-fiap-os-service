package com.techchallenge.os.application.commands;

import com.techchallenge.os.domain.exceptions.ValidacaoOrdemServicoException;
import lombok.Getter;

import java.util.UUID;

@Getter
public class RemoverMROServicoCommand {
    
    private final UUID ordemServicoId;
    private final UUID itemServicoId;
    private final UUID itemMroId;
    
    public RemoverMROServicoCommand(UUID ordemServicoId, UUID itemServicoId, UUID itemMroId) {
        validate(ordemServicoId, itemServicoId, itemMroId);
        
        this.ordemServicoId = ordemServicoId;
        this.itemServicoId = itemServicoId;
        this.itemMroId = itemMroId;
    }
    
    private void validate(UUID ordemServicoId, UUID itemServicoId, UUID itemMroId) {
        if (ordemServicoId == null) {
            throw new ValidacaoOrdemServicoException("Ordem de Serviço ID é obrigatório");
        }
        if (itemServicoId == null) {
            throw new ValidacaoOrdemServicoException("Item de Serviço ID é obrigatório");
        }
        if (itemMroId == null) {
            throw new ValidacaoOrdemServicoException("Item de MRO ID é obrigatório");
        }
    }
}
