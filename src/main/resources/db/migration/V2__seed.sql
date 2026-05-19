-- Dados iniciais.

INSERT INTO perfil (nome) VALUES
    ('ADMINISTRADOR'), ('OPERADOR'), ('CLIENTE');

INSERT INTO tipo_contato (nome, ativo) VALUES
    ('EMAIL', TRUE), ('TELEFONE', TRUE), ('CELULAR', TRUE);

INSERT INTO plano (nome, descricao, preco_mensal, limite_recibos, func_pdf, func_relatorio, ativo)
VALUES ('Gratuito', 'Plano inicial', 0, 10, FALSE, FALSE, TRUE);

-- Pessoa + usuário administrador padrão.
-- Login: admin@recibo.com / adminpadrao  (senha BCrypt; trocar após 1º acesso).
INSERT INTO pessoa (nome, tipo) VALUES ('Administrador', 'FISICA');

INSERT INTO usuario (email, senha_hash, ativo, pessoa_id)
SELECT 'admin@recibo.com',
       '$2a$10$.SvDI76wbdsHT8/5SpPKUe4qXQDBtBBjmtu/NiT3vxz8Q30biQ7fS',
       TRUE, p.id
FROM pessoa p
WHERE p.nome = 'Administrador'
ORDER BY p.id DESC
LIMIT 1;

INSERT INTO usuario_perfil (usuario_id, perfil_id)
SELECT u.id, pf.id
FROM usuario u, perfil pf
WHERE u.email = 'admin@recibo.com' AND pf.nome = 'ADMINISTRADOR';
