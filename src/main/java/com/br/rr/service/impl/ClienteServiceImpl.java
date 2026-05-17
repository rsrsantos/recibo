package com.br.rr.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.br.rr.models.Cliente;
import com.br.rr.repository.ClienteRepository;
import com.br.rr.exception.RecursoNaoEncontradoException;
import com.br.rr.service.ClienteService;

@Service
@Transactional
public class ClienteServiceImpl implements ClienteService {

	private final ClienteRepository clienteRepository;

	public ClienteServiceImpl(ClienteRepository clienteRepository) {
		this.clienteRepository = clienteRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public Page<Cliente> listar(Pageable pageable) {
		return clienteRepository.findAll(pageable);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Cliente> listarTodos() {
		return clienteRepository.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public Cliente buscarPorId(long id) {
		return clienteRepository.findById(id)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado: id " + id));
	}

	@Override
	public Cliente salvar(Cliente cliente) {
		return clienteRepository.save(cliente);
	}

	@Override
	public void excluir(long id) {
		Cliente cliente = buscarPorId(id);
		clienteRepository.delete(cliente);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Cliente> buscarPorNome(String nome) {
		return clienteRepository.findByNomeContainingIgnoreCase(nome);
	}

	@Override
	@Transactional(readOnly = true)
	public long contar() {
		return clienteRepository.count();
	}

}
