package com.br.rr.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.br.rr.models.Cliente;

public interface ClienteService {

	Page<Cliente> listar(Pageable pageable);

	List<Cliente> listarTodos();

	Cliente buscarPorId(long id);

	Cliente salvar(Cliente cliente);

	void excluir(long id);

	List<Cliente> buscarPorNome(String nome);

	long contar();

}
