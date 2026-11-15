-- Migration: Adicionar referência ao pagamento mantido pelo Billing (MongoDB).
-- Como o pagamento pertence a outro serviço/database, não há foreign key local.

ALTER TABLE ordens_servico
ADD COLUMN pagamento_id BINARY(16) NULL
COMMENT 'Identificador do pagamento no Billing'
AFTER data_ultima_mudanca_status;

ALTER TABLE ordens_servico
ADD COLUMN data_pagamento_aprovado DATETIME(6) NULL
COMMENT 'Data e hora da aprovação do pagamento no Billing'
AFTER pagamento_id;
