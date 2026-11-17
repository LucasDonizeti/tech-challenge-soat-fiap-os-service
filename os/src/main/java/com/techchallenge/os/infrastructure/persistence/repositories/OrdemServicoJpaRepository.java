package com.techchallenge.os.infrastructure.persistence.repositories;

import com.techchallenge.os.infrastructure.persistence.entities.OrdemServicoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface OrdemServicoJpaRepository extends JpaRepository<OrdemServicoEntity, UUID>, JpaSpecificationExecutor<OrdemServicoEntity> {
    
    @Query("SELECT os FROM OrdemServicoEntity os WHERE " +
           "(:clienteId IS NULL OR os.clienteId = :clienteId) AND " +
           "(:veiculoId IS NULL OR os.veiculoId = :veiculoId) AND " +
           "(:status IS NULL OR os.status = :status) AND " +
           "(:dataInicio IS NULL OR os.dataCriacao >= :dataInicio) AND " +
           "(:dataFim IS NULL OR os.dataCriacao <= :dataFim)")
    Page<OrdemServicoEntity> findByFilters(
            @Param("clienteId") UUID clienteId,
            @Param("veiculoId") UUID veiculoId,
            @Param("status") OrdemServicoEntity.StatusOSEntity status,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim,
            Pageable pageable
    );
    
}
