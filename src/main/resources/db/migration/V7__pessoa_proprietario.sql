-- Isolamento por conta: cada cliente (pessoa) pertence ao usuário que o criou.
-- Nulo = pessoa do próprio titular da conta (vinculada via usuario.pessoa_id),
-- que não aparece na lista de clientes.
ALTER TABLE pessoa ADD COLUMN IF NOT EXISTS usuario_id BIGINT REFERENCES usuario (id) ON DELETE CASCADE;

CREATE INDEX IF NOT EXISTS idx_pessoa_usuario ON pessoa (usuario_id);
