package com.br.rr.service;

import java.util.List;
import java.util.Set;

import com.br.rr.dto.PerfilForm;
import com.br.rr.models.Perfil;

public interface PerfilService {

	/** Perfis do sistema — não podem ser renomeados nem excluídos. */
	Set<String> SISTEMA = Set.of("ADMINISTRADOR", "OPERADOR", "CLIENTE");

	List<Perfil> listar();

	PerfilForm carregarForm(Long id);

	Perfil salvarForm(PerfilForm form);

	void excluir(Long id);

	long contarUsuarios(Long perfilId);

	boolean isSistema(String nome);

}
