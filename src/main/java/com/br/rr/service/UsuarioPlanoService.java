package com.br.rr.service;

import java.util.Optional;

import com.br.rr.models.Usuario;
import com.br.rr.models.UsuarioPlano;

public interface UsuarioPlanoService {

	/** Ativa plano com lógica de carência (seleção manual / gratuito). */
	UsuarioPlano ativar(Usuario usuario, Long planoId);

	/**
	 * Ativa ou renova o plano após pagamento confirmado.
	 * Sempre define dtFim = hoje + 30 dias, ignorando carenciaDias.
	 */
	UsuarioPlano ativarAposPagamento(Usuario usuario, Long planoId);

	/** Retorna o plano ativo mais recente do usuário, se existir. */
	Optional<UsuarioPlano> buscarAtivo(Usuario usuario);

}
