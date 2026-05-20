-- Confirmação de e-mail
ALTER TABLE usuario
    ADD COLUMN IF NOT EXISTS email_confirmado  BOOLEAN      NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS token_confirmacao VARCHAR(64)  UNIQUE,
    ADD COLUMN IF NOT EXISTS token_expira_em   TIMESTAMP;

-- Usuários já existentes (cadastrados antes desta feature) são considerados confirmados,
-- inclusive os de OAuth2 (Google), onde o e-mail já é verificado pelo provedor.
UPDATE usuario SET email_confirmado = TRUE;
