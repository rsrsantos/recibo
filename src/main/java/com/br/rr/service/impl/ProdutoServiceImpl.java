package com.br.rr.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.br.rr.models.Produto;
import com.br.rr.repository.ProdutoRepository;
import com.br.rr.exception.RecursoNaoEncontradoException;
import com.br.rr.service.ProdutoService;

@Service
@Transactional
public class ProdutoServiceImpl implements ProdutoService {

	private final ProdutoRepository produtoRepository;

	public ProdutoServiceImpl(ProdutoRepository produtoRepository) {
		this.produtoRepository = produtoRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public Page<Produto> listar(Pageable pageable) {
		return produtoRepository.findAll(pageable);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Produto> listarTodos() {
		return produtoRepository.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public Produto buscarPorId(long id) {
		return produtoRepository.findById(id)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado: id " + id));
	}

	@Override
	public Produto salvar(Produto produto) {
		return produtoRepository.save(produto);
	}

	@Override
	public void excluir(long id) {
		Produto produto = buscarPorId(id);
		produtoRepository.delete(produto);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Produto> buscarPorNome(String nome) {
		return produtoRepository.findByNomeContainingIgnoreCase(nome);
	}

	@Override
	@Transactional(readOnly = true)
	public long contar() {
		return produtoRepository.count();
	}

}
