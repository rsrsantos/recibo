-- Estrutura de autenticação/autorização (padrão de qualquer sistema).

CREATE TABLE perfil (
    id   BIGSERIAL PRIMARY KEY,
    nome VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE usuario (
    id       BIGSERIAL PRIMARY KEY,
    nome     VARCHAR(120),
    username VARCHAR(60) NOT NULL UNIQUE,
    senha    VARCHAR(100) NOT NULL,
    ativo    BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE usuario_perfil (
    usuario_id BIGINT NOT NULL REFERENCES usuario (id) ON DELETE CASCADE,
    perfil_id  BIGINT NOT NULL REFERENCES perfil (id) ON DELETE CASCADE,
    PRIMARY KEY (usuario_id, perfil_id)
);

-- Perfis padrão do sistema.
INSERT INTO perfil (nome) VALUES
    ('ADMINISTRADOR'),
    ('OPERADOR'),
    ('CLIENTE');

-- O usuário administrador padrão é criado no boot pelo seeder
-- (com a senha já criptografada em BCrypt).
