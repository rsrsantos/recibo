-- Dados do emitente: um por usuário, aparecem no cabeçalho do recibo.
CREATE TABLE emitente (
    id           BIGSERIAL PRIMARY KEY,
    nome         VARCHAR(150) NOT NULL,
    tipo         VARCHAR(20)  NOT NULL CHECK (tipo IN ('FISICA', 'JURIDICA')),
    documento    VARCHAR(20),
    razao_social VARCHAR(150),
    telefone     VARCHAR(20),
    email        VARCHAR(120),
    cep          VARCHAR(10),
    logradouro   VARCHAR(150),
    numero       VARCHAR(20),
    bairro       VARCHAR(100),
    cidade       VARCHAR(100),
    estado       VARCHAR(2),
    usuario_id   BIGINT NOT NULL UNIQUE REFERENCES usuario (id) ON DELETE CASCADE
);
