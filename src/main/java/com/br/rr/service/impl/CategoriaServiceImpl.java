package com.br.rr.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.br.rr.exception.RecursoNaoEncontradoException;
import com.br.rr.models.Categoria;
import com.br.rr.models.Usuario;
import com.br.rr.repository.CategoriaRepository;
import com.br.rr.service.CategoriaService;
import com.br.rr.service.ContaService;

@Service
@Transactional
public class CategoriaServiceImpl implements CategoriaService {

	private final CategoriaRepository repository;
	private final ContaService contaService;

	public CategoriaServiceImpl(CategoriaRepository repository, ContaService contaService) {
		this.repository = repository;
		this.contaService = contaService;
	}

	@Override
	@Transactional(readOnly = true)
	public Page<Categoria> listar(Pageable pageable) {
		return repository.findByUsuario(contaService.usuarioLogado(), pageable);
	}

	@Override
	@Transactional(readOnly = true)
	public Categoria buscarPorId(Long id) {
		return repository.findByIdAndUsuario(id, contaService.usuarioLogado())
				.orElseThrow(() -> new RecursoNaoEncontradoException("Categoria não encontrada: " + id));
	}

	@Override
	public Categoria salvar(Categoria categoria) {
		Usuario usuario = contaService.usuarioLogado();
		if (categoria.getId() != null) {
			// garante posse antes de atualizar
			buscarPorId(categoria.getId());
		}
		categoria.setUsuario(usuario);
		return repository.save(categoria);
	}

	@Override
	public void excluir(Long id) {
		repository.delete(buscarPorId(id));
	}

	@Override
	@Transactional(readOnly = true)
	public long contar() {
		return repository.countByUsuario(contaService.usuarioLogado());
	}

}
