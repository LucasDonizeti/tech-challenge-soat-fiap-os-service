package com.techchallenge.os.application.responses;

import com.techchallenge.os.application.gateway.ports.output.models.ClienteResumo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteResponse {
    
    private UUID id;
    private String nome;
    private String cpf;
    private String cnpj;
    private String email;
    private String tipo;
    private String status;
    
    public static ClienteResponse from(ClienteResumo cliente) {
        if (cliente == null) {
            return null;
        }
        
        String tipo = cliente.getCpf() != null ? "PESSOA_FISICA" : 
                      cliente.getCnpj() != null ? "PESSOA_JURIDICA" : null;
        
        return ClienteResponse.builder()
                .id(cliente.getId())
                .nome(cliente.getNome())
                .cpf(cliente.getCpf())
                .cnpj(cliente.getCnpj())
                .email(cliente.getEmail())
                .tipo(tipo)
                .status(cliente.getStatus())
                .build();
    }
}
