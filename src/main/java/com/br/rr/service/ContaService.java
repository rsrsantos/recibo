package com.br.rr.service;

import com.br.rr.models.Usuario;

public interface ContaService {

	/** Usuário atualmente autenticado. */
	Usuario usuarioLogado();

	/**
	 * Altera a senha do usuário logado, validando a senha atual e a
	 * confirmação da nova senha.
	 */
	void alterarSenha(String senhaAtual, String novaSenha, String confirmacao);

}
