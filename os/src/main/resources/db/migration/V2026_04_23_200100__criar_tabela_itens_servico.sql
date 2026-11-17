-- Migration: Criar tabela de itens de serviço
-- servico_id referencia o Catalog e, portanto, não tem FK local.

CREATE TABLE IF NOT EXISTS itens_servico (
    id BINARY(16) NOT NULL PRIMARY KEY COMMENT 'UUID identificador único do item de serviço',
    ordem_servico_id BINARY(16) NOT NULL COMMENT 'ID da ordem de serviço',
    servico_id BINARY(16) NOT NULL COMMENT 'ID do serviço no Catalog',
    status ENUM('PENDENTE', 'EM_ANDAMENTO', 'CONCLUIDO', 'CANCELADO') NOT NULL DEFAULT 'PENDENTE' COMMENT 'Status individual do serviço',
    observacoes VARCHAR(500) NULL COMMENT 'Observações específicas deste serviço',
    valor_servico DECIMAL(10,2) NOT NULL COMMENT 'Valor do serviço (snapshot do preço na criação)',
    valor_mro DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT 'Valor total dos MROs usados neste serviço',
    CONSTRAINT fk_itens_servico_ordem_servico FOREIGN KEY (ordem_servico_id)
        REFERENCES ordens_servico(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Tabela de itens de serviço';

CREATE INDEX idx_itens_servico_ordem_servico_id ON itens_servico(ordem_servico_id) COMMENT 'Índice para busca por ordem de serviço';
CREATE INDEX idx_itens_servico_servico_id ON itens_servico(servico_id) COMMENT 'Índice para busca por serviço';
CREATE INDEX idx_itens_servico_status ON itens_servico(status) COMMENT 'Índice para busca por status';

ALTER TABLE itens_servico COMMENT = 'Tabela de itens de serviço das ordens de serviço';
