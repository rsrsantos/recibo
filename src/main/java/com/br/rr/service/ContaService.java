package com.br.rr.service;

import com.br.rr.dto.CadastroForm;
import com.br.rr.models.Usuario;

public interface ContaService {

	/** Usuário atualmente autenticado. */
	Usuario usuarioLogado();

	/** Cria um novo usuário a partir do formulário de cadastro público. */
	Usuario cadastrar(CadastroForm form);

	/** Confirma o e-mail usando o token enviado. Retorna o usuário ativado. */
	Usuario confirmarEmail(String token);

	/** Reenvia o e-mail de confirmação para um endereço ainda não confirmado. */
	void reenviarConfirmacao(String email);

	/**
	 * Altera a senha do usuário logado, validando a senha atual e a
	 * confirmação da nova senha.
	 */
	void alterarSenha(String senhaAtual, String novaSenha, String confirmacao);

}
