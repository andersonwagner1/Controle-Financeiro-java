-- PostgreSQL. O projeto também está configurado com Hibernate ddl-auto=update.
CREATE TABLE IF NOT EXISTS cf_cartoes_credito (
    id VARCHAR(255) PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    vinculo_id VARCHAR(255) NOT NULL,
    limite NUMERIC(19, 2) NOT NULL,
    dia_fechamento INTEGER NOT NULL,
    dia_vencimento INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS lancamento_cartao (
    id VARCHAR(255) PRIMARY KEY,
    conta_id VARCHAR(255) NOT NULL,
    cartao_credito_id VARCHAR(255) NOT NULL,
    tipo VARCHAR(255) NOT NULL,
    descricao VARCHAR(255),
    categoria VARCHAR(255),
    valor NUMERIC(19, 2) NOT NULL,
    data DATE,
    observacao VARCHAR(255),
    saldo_apos NUMERIC(19, 2),
    transferencia_id VARCHAR(255)
);

CREATE INDEX IF NOT EXISTS idx_lancamento_cartao_cartao
    ON lancamento_cartao (cartao_credito_id);
