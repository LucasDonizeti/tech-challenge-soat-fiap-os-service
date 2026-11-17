package com.techchallenge.os.infrastructure.web.dtos;

import com.techchallenge.os.application.commands.AdicionarServicoOrdemCommand;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class AdicionarServicoOrdemRequest {
    
    @NotNull(message = "Serviço ID é obrigatório")
    private UUID servicoId;
    
    public AdicionarServicoOrdemCommand toCommand(UUID ordemServicoId) {
        return new AdicionarServicoOrdemCommand(ordemServicoId, servicoId);
    }
}
