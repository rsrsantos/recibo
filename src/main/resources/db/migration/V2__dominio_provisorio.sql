-- Schema de domínio PROVISÓRIO, espelhando as entidades JPA atuais
-- (Cliente, Produto, Empresa, Recibo, ReciboProduto).
-- Será revisado quando a modelagem final (draw.io) for definida.

CREATE TABLE cliente (
    id       BIGSERIAL PRIMARY KEY,
    nome     VARCHAR(255) NOT NULL,
    cpf      VARCHAR(255) NOT NULL,
    telefone VARCHAR(255)
);

CREATE TABLE produto (
    id    BIGSERIAL PRIMARY KEY,
    nome  VARCHAR(255) NOT NULL,
    valor DOUBLE PRECISION
);

CREATE TABLE empresa (
    id            BIGSERIAL PRIMARY KEY,
    razao_social  VARCHAR(255),
    nome_fantasia VARCHAR(255),
    cnpj          VARCHAR(255),
    telefone      VARCHAR(255),
    endereco      VARCHAR(255),
    bairro        VARCHAR(255),
    cep           VARCHAR(255),
    logo          BYTEA
);

CREATE TABLE recibo (
    id            BIGSERIAL PRIMARY KEY,
    empresa_id    BIGINT REFERENCES empresa (id),
    cliente_id    BIGINT REFERENCES cliente (id),
    numero_recibo INTEGER,
    observacao    VARCHAR(255),
    valor_total   DOUBLE PRECISION,
    data_inclusao TIMESTAMP
);

CREATE TABLE recibo_produto (
    id         BIGSERIAL PRIMARY KEY,
    produto_id BIGINT REFERENCES produto (id),
    recibo_id  BIGINT REFERENCES recibo (id),
    quantidade INTEGER,
    valor      DOUBLE PRECISION
);
