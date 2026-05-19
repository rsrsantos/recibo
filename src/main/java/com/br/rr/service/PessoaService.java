package com.br.rr.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.br.rr.dto.PessoaForm;
import com.br.rr.models.Pessoa;
import com.br.rr.models.TipoPessoa;

public interface PessoaService {

	Page<Pessoa> listar(Pageable pageable);

	/** Busca dinâmica por nome (autocomplete). */
	List<Pessoa> buscar(String termo);

	/** Cadastro rápido pelo modal do recibo. */
	Pessoa criarRapido(String nome, TipoPessoa tipo, String documento);

	Pessoa buscarPorId(Long id);

	/** Carrega o form (pessoa + dados de física/jurídica) para edição. */
	PessoaForm carregarForm(Long id);

	/** Cria/atualiza pessoa e o respectivo registro física/jurídica. */
	Pessoa salvar(PessoaForm form);

	void excluir(Long id);

	long contar();

}
