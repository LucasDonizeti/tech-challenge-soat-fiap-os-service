-- Migration: Criar tabela de ordens de serviço
-- cliente_id e veiculo_id são referências ao Catalog e, portanto, não têm FK local.

CREATE TABLE IF NOT EXISTS ordens_servico (
    id BINARY(16) NOT NULL PRIMARY KEY COMMENT 'UUID identificador único da ordem de serviço',
    cliente_id BINARY(16) NOT NULL COMMENT 'ID do cliente no Catalog',
    veiculo_id BINARY(16) NOT NULL COMMENT 'ID do veículo no Catalog',
    status ENUM('RECEBIDA', 'EM_DIAGNOSTICO', 'AGUARDANDO_APROVACAO', 'EM_EXECUCAO', 'FINALIZADA', 'ENTREGUE') NOT NULL DEFAULT 'RECEBIDA' COMMENT 'Status da ordem de serviço',
    data_criacao DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT 'Data e hora de criação da OS'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Tabela de ordens de serviço da oficina';

CREATE INDEX idx_ordens_servico_cliente_id ON ordens_servico(cliente_id) COMMENT 'Índice para busca por cliente';
CREATE INDEX idx_ordens_servico_veiculo_id ON ordens_servico(veiculo_id) COMMENT 'Índice para busca por veículo';
CREATE INDEX idx_ordens_servico_status ON ordens_servico(status) COMMENT 'Índice para busca por status';
CREATE INDEX idx_ordens_servico_data_criacao ON ordens_servico(data_criacao) COMMENT 'Índice para ordenação por data de criação';

ALTER TABLE ordens_servico COMMENT = 'Tabela de ordens de serviço da oficina mecânica';
