-- Corrige planos ativos pagos com dtFim = hoje (sem período de uso).
-- Isso aconteceu porque carenciaDias=0 foi usado indevidamente para planos pagos.
UPDATE usuario_plano
SET dt_fim = CURRENT_DATE + INTERVAL '30 days'
WHERE status = 'ATIVO'
  AND dt_fim = CURRENT_DATE
  AND plano_id IN (SELECT id FROM plano WHERE preco_mensal > 0);
