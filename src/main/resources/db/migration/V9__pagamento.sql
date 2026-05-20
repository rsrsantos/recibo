CREATE TABLE pagamento (
    id              BIGSERIAL PRIMARY KEY,
    usuario_id      BIGINT        NOT NULL REFERENCES usuario(id),
    plano_id        BIGINT        NOT NULL REFERENCES plano(id),
    mp_payment_id   VARCHAR(100),
    mp_status       VARCHAR(50),
    mp_status_detail VARCHAR(150),
    metodo          VARCHAR(50),
    valor           NUMERIC(10,2) NOT NULL,
    criado_em       TIMESTAMP     NOT NULL DEFAULT NOW(),
    atualizado_em   TIMESTAMP
);
