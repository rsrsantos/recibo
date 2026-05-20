CREATE TABLE IF NOT EXISTS plano_modelo_recibo (
    plano_id BIGINT      NOT NULL REFERENCES plano(id) ON DELETE CASCADE,
    modelo   VARCHAR(20) NOT NULL,
    PRIMARY KEY (plano_id, modelo)
);
