package com.br.rr.service;

import java.util.Optional;

import com.br.rr.models.Usuario;
import com.br.rr.models.UsuarioPlano;

public interface UsuarioPlanoService {

	/** Ativa (ou renova) o plano para o usuário. */
	UsuarioPlano ativar(Usuario usuario, Long planoId);

	/** Retorna o plano ativo mais recente do usuário, se existir. */
	Optional<UsuarioPlano> buscarAtivo(Usuario usuario);

}
