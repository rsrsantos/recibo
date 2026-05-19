package com.br.rr.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.br.rr.dto.PlanoForm;
import com.br.rr.exception.RecursoNaoEncontradoException;
import com.br.rr.models.Plano;
import com.br.rr.repository.PlanoRepository;
import com.br.rr.service.PlanoService;

@Service
@Transactional
public class PlanoServiceImpl implements PlanoService {

	private final PlanoRepository repository;

	public PlanoServiceImpl(PlanoRepository repository) {
		this.repository = repository;
	}

	@Override
	@Transactional(readOnly = true)
	public Page<Plano> listar(Pageable pageable) {
		return repository.findAll(pageable);
	}

	@Override
	@Transactional(readOnly = true)
	public Plano buscarPorId(Long id) {
		return repository.findById(id)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Plano não encontrado: " + id));
	}

	@Override
	@Transactional(readOnly = true)
	public List<Plano> listarAtivos() {
		return repository.findByAtivoTrue();
	}

	@Override
	public Plano salvar(Plano plano) {
		return repository.save(plano);
	}

	@Override
	@Transactional(readOnly = true)
	public PlanoForm carregarForm(Long id) {
		Plano p = buscarPorId(id);
		PlanoForm f = new PlanoForm();
		f.setId(p.getId());
		f.setNome(p.getNome());
		f.setDescricao(p.getDescricao());
		f.setPrecoMensal(p.getPrecoMensal());
		f.setLimiteRecibos(p.getLimiteRecibos());
		f.setFuncPdf(p.isFuncPdf());
		f.setFuncRelatorio(p.isFuncRelatorio());
		f.setAtivo(p.isAtivo());
		f.setTemCarencia(p.isTemCarencia());
		f.setCarenciaDias(p.getCarenciaDias());
		return f;
	}

	@Override
	public Plano salvarForm(PlanoForm form) {
		Plano p = form.getId() != null ? buscarPorId(form.getId()) : new Plano();
		p.setNome(form.getNome());
		p.setDescricao(form.getDescricao());
		p.setPrecoMensal(form.getPrecoMensal());
		p.setLimiteRecibos(form.getLimiteRecibos());
		p.setFuncPdf(form.isFuncPdf());
		p.setFuncRelatorio(form.isFuncRelatorio());
		p.setAtivo(form.isAtivo());
		p.setTemCarencia(form.isTemCarencia());
		p.setCarenciaDias(form.isTemCarencia() && form.getCarenciaDias() != null
				? form.getCarenciaDias() : 0);
		return repository.save(p);
	}

	@Override
	public void excluir(Long id) {
		repository.delete(buscarPorId(id));
	}

}
