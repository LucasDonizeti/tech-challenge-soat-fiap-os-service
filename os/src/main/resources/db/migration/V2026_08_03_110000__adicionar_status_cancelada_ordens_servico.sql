-- Migration: Adicionar status CANCELADA na tabela ordens_servico

ALTER TABLE ordens_servico
MODIFY COLUMN status ENUM(
    'RECEBIDA',
    'EM_DIAGNOSTICO',
    'AGUARDANDO_APROVACAO',
    'EM_EXECUCAO',
    'FINALIZADA',
    'ENTREGUE',
    'CANCELADA'
) NOT NULL DEFAULT 'RECEBIDA'
COMMENT 'Status da ordem de serviço';
