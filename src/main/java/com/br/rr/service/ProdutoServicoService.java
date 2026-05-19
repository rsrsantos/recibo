package com.br.rr.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.br.rr.models.ProdutoServico;

public interface ProdutoServicoService {

	Page<ProdutoServico> listar(Pageable pageable);

	List<ProdutoServico> listarAtivos();

	ProdutoServico buscarPorId(Long id);

	ProdutoServico salvar(ProdutoServico produtoServico);

	void excluir(Long id);

	long contar();

}
