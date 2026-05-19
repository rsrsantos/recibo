package com.br.rr.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.br.rr.exception.RecursoNaoEncontradoException;
import com.br.rr.models.ProdutoServico;
import com.br.rr.models.Usuario;
import com.br.rr.repository.ProdutoServicoRepository;
import com.br.rr.service.ContaService;
import com.br.rr.service.ProdutoServicoService;

@Service
@Transactional
public class ProdutoServicoServiceImpl implements ProdutoServicoService {

	private final ProdutoServicoRepository repository;
	private final ContaService contaService;

	public ProdutoServicoServiceImpl(ProdutoServicoRepository repository, ContaService contaService) {
		this.repository = repository;
		this.contaService = contaService;
	}

	@Override
	@Transactional(readOnly = true)
	public Page<ProdutoServico> listar(Pageable pageable) {
		return repository.findByUsuario(contaService.usuarioLogado(), pageable);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProdutoServico> listarAtivos() {
		return repository.findByUsuarioAndAtivoTrue(contaService.usuarioLogado());
	}

	@Override
	@Transactional(readOnly = true)
	public ProdutoServico buscarPorId(Long id) {
		return repository.findByIdAndUsuario(id, contaService.usuarioLogado())
				.orElseThrow(() -> new RecursoNaoEncontradoException("Produto/serviço não encontrado: " + id));
	}

	@Override
	public ProdutoServico salvar(ProdutoServico ps) {
		Usuario usuario = contaService.usuarioLogado();
		if (ps.getId() != null) {
			buscarPorId(ps.getId());
		}
		ps.setUsuario(usuario);
		return repository.save(ps);
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
