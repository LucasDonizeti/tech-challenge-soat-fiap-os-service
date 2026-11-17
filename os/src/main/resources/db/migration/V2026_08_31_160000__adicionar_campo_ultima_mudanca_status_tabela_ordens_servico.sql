-- Migration: Adicionar campo data_ultima_mudanca_status na tabela ordens_servico

ALTER TABLE ordens_servico
ADD COLUMN data_ultima_mudanca_status DATETIME(6) NULL
COMMENT 'Data e hora da última mudança de status da OS (usado para cálculo de tempo entre mudanças)'
AFTER data_finalizacao;

CREATE INDEX idx_ordens_servico_data_ultima_mudanca_status ON ordens_servico(data_ultima_mudanca_status) COMMENT 'Índice para busca por data de última mudança de status';

ALTER TABLE ordens_servico COMMENT = 'Tabela de ordens de serviço da oficina mecânica com controle de tempo entre mudanças de status';
