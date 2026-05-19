-- Schema do domínio (conforme docs/Modelagem Recibo.drawio.xml)
-- + perfil/usuario_perfil (autorização por papel, mantido fora do diagrama).

CREATE TABLE pessoa (
    id   BIGSERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    tipo VARCHAR(20)  NOT NULL CHECK (tipo IN ('FISICA', 'JURIDICA'))
);

CREATE TABLE pessoa_fisica (
    id            BIGSERIAL PRIMARY KEY,
    cpf           VARCHAR(14),
    identidade    VARCHAR(20),
    dt_nascimento DATE,
    pessoa_id     BIGINT NOT NULL UNIQUE REFERENCES pessoa (id) ON DELETE CASCADE
);

CREATE TABLE pessoa_juridica (
    id                  BIGSERIAL PRIMARY KEY,
    razao               VARCHAR(150),
    fantasia            VARCHAR(150),
    cnpj                VARCHAR(18),
    inscricao_estadual  VARCHAR(30),
    inscricao_municipal VARCHAR(30),
    pessoa_id           BIGINT NOT NULL UNIQUE REFERENCES pessoa (id) ON DELETE CASCADE
);

CREATE TABLE pessoa_endereco (
    id         BIGSERIAL PRIMARY KEY,
    logradouro VARCHAR(150),
    bairro     VARCHAR(100),
    numero     INTEGER,
    cep        VARCHAR(10),
    cidade     VARCHAR(100),
    estado     VARCHAR(2),
    principal  BOOLEAN NOT NULL DEFAULT FALSE,
    pessoa_id  BIGINT NOT NULL REFERENCES pessoa (id) ON DELETE CASCADE
);

CREATE TABLE tipo_contato (
    id    BIGSERIAL PRIMARY KEY,
    nome  VARCHAR(50) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE pessoa_contato (
    id              BIGSERIAL PRIMARY KEY,
    valor           VARCHAR(150) NOT NULL,
    ativo           BOOLEAN NOT NULL DEFAULT TRUE,
    tipo_contato_id BIGINT NOT NULL REFERENCES tipo_contato (id),
    pessoa_id       BIGINT NOT NULL REFERENCES pessoa (id) ON DELETE CASCADE
);

CREATE TABLE usuario (
    id         BIGSERIAL PRIMARY KEY,
    email      VARCHAR(120) NOT NULL UNIQUE,
    senha_hash VARCHAR(100) NOT NULL,
    ativo      BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em  TIMESTAMP NOT NULL DEFAULT NOW(),
    pessoa_id  BIGINT UNIQUE REFERENCES pessoa (id)
);

CREATE TABLE perfil (
    id   BIGSERIAL PRIMARY KEY,
    nome VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE usuario_perfil (
    usuario_id BIGINT NOT NULL REFERENCES usuario (id) ON DELETE CASCADE,
    perfil_id  BIGINT NOT NULL REFERENCES perfil (id) ON DELETE CASCADE,
    PRIMARY KEY (usuario_id, perfil_id)
);

CREATE TABLE plano (
    id             BIGSERIAL PRIMARY KEY,
    nome           VARCHAR(80) NOT NULL,
    descricao      VARCHAR(255),
    preco_mensal   NUMERIC(10, 2) NOT NULL DEFAULT 0,
    limite_recibos INTEGER,
    func_pdf       BOOLEAN NOT NULL DEFAULT FALSE,
    func_relatorio BOOLEAN NOT NULL DEFAULT FALSE,
    ativo          BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE usuario_plano (
    id         BIGSERIAL PRIMARY KEY,
    dt_inicio  DATE NOT NULL,
    dt_fim     DATE,
    status     VARCHAR(20) NOT NULL,
    usuario_id BIGINT NOT NULL REFERENCES usuario (id) ON DELETE CASCADE,
    plano_id   BIGINT NOT NULL REFERENCES plano (id)
);

CREATE TABLE categoria (
    id         BIGSERIAL PRIMARY KEY,
    nome       VARCHAR(80) NOT NULL,
    descricao  VARCHAR(255),
    ativo      BOOLEAN NOT NULL DEFAULT TRUE,
    usuario_id BIGINT NOT NULL REFERENCES usuario (id) ON DELETE CASCADE
);

CREATE TABLE produto_servico (
    id            BIGSERIAL PRIMARY KEY,
    nome          VARCHAR(120) NOT NULL,
    descricao     VARCHAR(255),
    servico       BOOLEAN NOT NULL DEFAULT FALSE,
    preco         NUMERIC(12, 2) NOT NULL DEFAULT 0,
    data_cadastro DATE NOT NULL DEFAULT CURRENT_DATE,
    ativo         BOOLEAN NOT NULL DEFAULT TRUE,
    categoria_id  BIGINT REFERENCES categoria (id),
    usuario_id    BIGINT NOT NULL REFERENCES usuario (id) ON DELETE CASCADE
);

CREATE TABLE recibo (
    id              BIGSERIAL PRIMARY KEY,
    n_recibo        VARCHAR(30),
    data_geracao    DATE NOT NULL DEFAULT CURRENT_DATE,
    vlr_total       NUMERIC(12, 2) NOT NULL DEFAULT 0,
    observacao      VARCHAR(255),
    usuario_id      BIGINT NOT NULL REFERENCES usuario (id) ON DELETE CASCADE,
    destinatario_id BIGINT NOT NULL REFERENCES pessoa (id)
);

CREATE TABLE recibo_item (
    id                  BIGSERIAL PRIMARY KEY,
    qtde                INTEGER NOT NULL DEFAULT 1,
    vlr_unitario        NUMERIC(12, 2) NOT NULL DEFAULT 0,
    vlr_total           NUMERIC(12, 2) NOT NULL DEFAULT 0,
    observacao          VARCHAR(255),
    recibo_id           BIGINT NOT NULL REFERENCES recibo (id) ON DELETE CASCADE,
    produto_servico_id  BIGINT NOT NULL REFERENCES produto_servico (id)
);
