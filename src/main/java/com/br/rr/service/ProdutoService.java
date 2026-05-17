package com.br.rr.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.br.rr.models.Produto;

public interface ProdutoService {

	Page<Produto> listar(Pageable pageable);

	List<Produto> listarTodos();

	Produto buscarPorId(long id);

	Produto salvar(Produto produto);

	void excluir(long id);

	List<Produto> buscarPorNome(String nome);

	long contar();

}
