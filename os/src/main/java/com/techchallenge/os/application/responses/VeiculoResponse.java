package com.techchallenge.os.application.responses;

import com.techchallenge.os.application.gateway.ports.output.models.VeiculoResumo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VeiculoResponse {
    
    private UUID id;
    private String placa;
    private String marca;
    private String modelo;
    private Integer ano;
    private String cor;
    private String status;
    
    public static VeiculoResponse from(VeiculoResumo veiculo) {
        if (veiculo == null) {
            return null;
        }
        
        return VeiculoResponse.builder()
                .id(veiculo.getId())
                .placa(veiculo.getPlaca())
                .marca(veiculo.getMarca())
                .modelo(veiculo.getModelo())
                .ano(veiculo.getAno())
                .cor(veiculo.getCor())
                .status(veiculo.getStatus())
                .build();
    }
}
