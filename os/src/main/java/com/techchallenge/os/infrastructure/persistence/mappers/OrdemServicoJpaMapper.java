package com.techchallenge.os.infrastructure.persistence.mappers;

import com.techchallenge.os.domain.models.OrdemServico;
import com.techchallenge.os.domain.models.valueobjects.StatusOS;
import com.techchallenge.os.infrastructure.persistence.entities.OrdemServicoEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrdemServicoJpaMapper {

    private final ItemServicoJpaMapper itemServicoJpaMapper;
    
    public OrdemServicoEntity toEntity(OrdemServico ordemServico) {
        if (ordemServico == null) {
            return null;
        }
        
        OrdemServicoEntity entity = OrdemServicoEntity.builder()
                .id(ordemServico.getId())
                .clienteId(ordemServico.getCliente())
                .veiculoId(ordemServico.getVeiculo())
                .status(ordemServico.getStatus() != null ? OrdemServicoEntity.StatusOSEntity.valueOf(ordemServico.getStatus().name()) : null)
                .dataCriacao(ordemServico.getDataCriacao())
                .dataInicioExecucao(ordemServico.getDataInicioExecucao())
                .dataFinalizacao(ordemServico.getDataFinalizacao())
                .dataUltimaMudancaStatus(ordemServico.getDataUltimaMudancaStatus())
                .build();
        
        if (ordemServico.getItensServico() != null && !ordemServico.getItensServico().isEmpty()) {
            entity.setItensServico(itemServicoJpaMapper.toEntityList(ordemServico.getItensServico(), ordemServico.getId()));
        }
        
        return entity;
    }
    
    public OrdemServico toDomain(OrdemServicoEntity entity) {
        if (entity == null) {
            return null;
        }
        
        StatusOS statusOS = entity.getStatus() != null ? StatusOS.valueOf(entity.getStatus().name()) : null;
        
        OrdemServico ordemServico = OrdemServico.reconstruir(
                entity.getId(),
                entity.getClienteId(),
                entity.getVeiculoId(),
                statusOS,
                entity.getDataCriacao(),
                entity.getDataInicioExecucao(),
                entity.getDataFinalizacao(),
                entity.getDataUltimaMudancaStatus()
        );
        
        if (entity.getItensServico() != null && !entity.getItensServico().isEmpty()) {
            var items = itemServicoJpaMapper.toDomainList(entity.getItensServico());
            items.forEach(ordemServico::adicionarItemServicoSemValidacao);
        }
        
        return ordemServico;
    }
}
