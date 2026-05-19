-- Dias de carência gratuita após contratar um plano pago.
ALTER TABLE plano ADD COLUMN IF NOT EXISTS carencia_dias INTEGER NOT NULL DEFAULT 30;

-- Coluna referente no recibo (caso V3 ainda não tenha aplicado em alguns ambientes)
ALTER TABLE recibo ADD COLUMN IF NOT EXISTS referente VARCHAR(500);
