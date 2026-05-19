package com.br.rr.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.br.rr.models.Categoria;

public interface CategoriaService {

	Page<Categoria> listar(Pageable pageable);

	Categoria buscarPorId(Long id);

	Categoria salvar(Categoria categoria);

	void excluir(Long id);

	long contar();

}
