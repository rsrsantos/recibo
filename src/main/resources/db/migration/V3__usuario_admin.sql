-- Usuário administrador padrão.
-- Credenciais: admin / adminpadrao  (TROCAR a senha após o primeiro acesso).
-- Senha gravada em BCrypt. Idempotente (não falha se já existir).

INSERT INTO usuario (nome, username, senha, ativo)
VALUES ('Administrador', 'admin',
        '$2a$10$.SvDI76wbdsHT8/5SpPKUe4qXQDBtBBjmtu/NiT3vxz8Q30biQ7fS', TRUE)
ON CONFLICT (username) DO NOTHING;

INSERT INTO usuario_perfil (usuario_id, perfil_id)
SELECT u.id, p.id
FROM usuario u, perfil p
WHERE u.username = 'admin' AND p.nome = 'ADMINISTRADOR'
ON CONFLICT DO NOTHING;
