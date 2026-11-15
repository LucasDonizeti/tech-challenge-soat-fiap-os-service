package com.techchallenge.os.infrastructure.persistence.repositories.impl;


import com.techchallenge.os.domain.models.OrdemServico;
import com.techchallenge.os.domain.models.valueobjects.StatusOS;
import com.techchallenge.os.domain.repositories.OrdemServicoRepository;
import com.techchallenge.os.infrastructure.persistence.entities.OrdemServicoEntity;
import com.techchallenge.os.infrastructure.persistence.mappers.OrdemServicoJpaMapper;
import com.techchallenge.os.infrastructure.persistence.repositories.OrdemServicoJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class OrdemServicoRepositoryImpl implements OrdemServicoRepository {
    
    private final OrdemServicoJpaRepository jpaRepository;
    private final OrdemServicoJpaMapper mapper;
    
    @Override
    public OrdemServico save(OrdemServico ordemServico) {
        OrdemServicoEntity entity = mapper.toEntity(ordemServico);
        OrdemServicoEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }
    
    @Override
    public Optional<OrdemServico> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(entity -> mapper.toDomain(entity));
    }
    
    @Override
    public Page<OrdemServico> findAll(Pageable pageable) {
        Page<OrdemServicoEntity> entities = jpaRepository.findAll(pageable);
        return entities.map(entity -> mapper.toDomain(entity));
    }
    
    @Override
    public Page<OrdemServico> findByFilters(UUID clienteId, UUID veiculoId, StatusOS status,
                                             LocalDateTime dataInicio, LocalDateTime dataFim, Pageable pageable) {
        OrdemServicoEntity.StatusOSEntity statusEntity = status != null ? OrdemServicoEntity.StatusOSEntity.valueOf(status.name()) : null;
        Page<OrdemServicoEntity> entities = jpaRepository.findByFilters(
                clienteId, veiculoId, statusEntity, dataInicio, dataFim, pageable);
        return entities.map(entity -> mapper.toDomain(entity));
    }
}
