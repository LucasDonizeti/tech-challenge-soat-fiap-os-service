package com.techchallenge.os.infrastructure.persistence.repositories;
import com.techchallenge.os.infrastructure.persistence.entities.ItemServicoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ItemServicoJpaRepository extends JpaRepository<ItemServicoEntity, UUID> {
    
    List<ItemServicoEntity> findByOrdemServicoId(UUID ordemServicoId);
    
    List<ItemServicoEntity> findByOrdemServicoIdAndStatus(UUID ordemServicoId, ItemServicoEntity.StatusItemServicoEntity status);
}
