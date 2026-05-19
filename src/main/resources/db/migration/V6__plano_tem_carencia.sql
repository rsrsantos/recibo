-- Carência opcional por plano. Default TRUE preserva o comportamento anterior
-- (planos pagos tinham carência fixa).
ALTER TABLE plano ADD COLUMN IF NOT EXISTS tem_carencia BOOLEAN NOT NULL DEFAULT TRUE;
