package com.br.rr.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.br.rr.dto.UsuarioEditForm;
import com.br.rr.models.Perfil;
import com.br.rr.models.Usuario;

public interface UsuarioAdminService {

	Page<Usuario> listar(Pageable pageable);

	/** Carrega o formulário de edição de um usuário. */
	UsuarioEditForm carregarEdicao(Long id);

	/** Salva nome/e-mail e, opcionalmente, redefine a senha. */
	void salvarEdicao(UsuarioEditForm form);

	/** Ativa/desativa o usuário (não permite desativar a si mesmo). */
	void alternarAtivo(Long id);

	/** Nome do plano ativo do usuário, ou null. */
	String planoAtivoNome(Usuario usuario);

	/** Todos os perfis disponíveis para atribuição. */
	List<Perfil> listarPerfis();

	/** Nome do perfil atual do usuário (o primeiro), ou "—". */
	String perfilAtualNome(Usuario usuario);

	/** Substitui o perfil do usuário (não permite alterar o próprio). */
	void alterarPerfil(Long usuarioId, Long perfilId);

}
