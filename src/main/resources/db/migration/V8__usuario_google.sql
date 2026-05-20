-- Suporte a login com Google: identificador único do Google e senha opcional
ALTER TABLE usuario ADD COLUMN IF NOT EXISTS google_id VARCHAR(255) UNIQUE;
ALTER TABLE usuario ALTER COLUMN senha_hash DROP NOT NULL;
