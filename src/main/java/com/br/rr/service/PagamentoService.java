package com.br.rr.service;

import java.util.Map;

import com.br.rr.models.Pagamento;
import com.br.rr.models.Usuario;

public interface PagamentoService {

	Pagamento processarPagamento(Usuario usuario, Long planoId, Map<String, Object> formData);

	void processarWebhook(String mpPaymentId);

	/** Consulta a API do MP, atualiza o banco e ativa o plano se aprovado. */
	Map<String, Object> consultarEAtualizarStatus(Long pagamentoId);

	/** Busca pagamento por id, validando que pertence ao usuário. */
	Pagamento buscarPorId(Long pagamentoId, Usuario usuario);

}
