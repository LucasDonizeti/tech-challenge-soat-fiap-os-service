package com.techchallenge.os.infrastructure.web.dtos;

import com.techchallenge.os.domain.models.valueobjects.StatusOS;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class OrdemServicoFilterRequest {
    
    private UUID clienteId;
    private UUID veiculoId;
    private StatusOS status;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
}
