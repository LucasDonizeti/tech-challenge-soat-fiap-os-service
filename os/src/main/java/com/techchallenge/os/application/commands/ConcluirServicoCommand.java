package com.techchallenge.os.application.commands;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConcluirServicoCommand {
    
    private UUID ordemServicoId;
    private UUID itemServicoId;
}
