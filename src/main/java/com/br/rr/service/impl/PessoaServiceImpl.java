package com.br.rr.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.br.rr.dto.PessoaForm;
import com.br.rr.exception.RecursoNaoEncontradoException;
import com.br.rr.models.Pessoa;
import com.br.rr.models.PessoaFisica;
import com.br.rr.models.PessoaJuridica;
import com.br.rr.models.TipoPessoa;
import com.br.rr.models.Usuario;
import com.br.rr.repository.PessoaFisicaRepository;
import com.br.rr.repository.PessoaJuridicaRepository;
import com.br.rr.repository.PessoaRepository;
import com.br.rr.service.ContaService;
import com.br.rr.service.PessoaService;

@Service
@Transactional
public class PessoaServiceImpl implements PessoaService {

	private final PessoaRepository pessoaRepository;
	private final PessoaFisicaRepository fisicaRepository;
	private final PessoaJuridicaRepository juridicaRepository;
	private final ContaService contaService;

	public PessoaServiceImpl(PessoaRepository pessoaRepository, PessoaFisicaRepository fisicaRepository,
			PessoaJuridicaRepository juridicaRepository, ContaService contaService) {
		this.pessoaRepository = pessoaRepository;
		this.fisicaRepository = fisicaRepository;
		this.juridicaRepository = juridicaRepository;
		this.contaService = contaService;
	}

	@Override
	@Transactional(readOnly = true)
	public Page<Pessoa> listar(Pageable pageable) {
		return pessoaRepository.findByProprietario(contaService.usuarioLogado(), pageable);
	}

	@Override
	@Transactional(readOnly = true)
	public java.util.List<Pessoa> buscar(String termo) {
		if (termo == null || termo.isBlank()) {
			return java.util.Collections.emptyList();
		}
		return pessoaRepository.findByNomeContainingIgnoreCaseAndProprietario(
				termo.trim(), contaService.usuarioLogado());
	}

	@Override
	public Pessoa criarRapido(String nome, TipoPessoa tipo, String documento) {
		Pessoa pessoa = new Pessoa();
		pessoa.setNome(nome);
		pessoa.setTipo(tipo != null ? tipo : TipoPessoa.FISICA);
		pessoa.setProprietario(contaService.usuarioLogado());
		pessoa = pessoaRepository.save(pessoa);

		if (pessoa.getTipo() == TipoPessoa.FISICA) {
			PessoaFisica pf = new PessoaFisica();
			pf.setPessoa(pessoa);
			pf.setCpf(documento);
			fisicaRepository.save(pf);
		} else {
			PessoaJuridica pj = new PessoaJuridica();
			pj.setPessoa(pessoa);
			pj.setCnpj(documento);
			juridicaRepository.save(pj);
		}
		return pessoa;
	}

	@Override
	@Transactional(readOnly = true)
	public Pessoa buscarPorId(Long id) {
		return pessoaRepository.findByIdAndProprietario(id, contaService.usuarioLogado())
				.orElseThrow(() -> new RecursoNaoEncontradoException("Pessoa não encontrada: " + id));
	}

	@Override
	@Transactional(readOnly = true)
	public PessoaForm carregarForm(Long id) {
		Pessoa p = buscarPorId(id);
		PessoaForm f = new PessoaForm();
		f.setId(p.getId());
		f.setNome(p.getNome());
		f.setTipo(p.getTipo());
		if (p.getTipo() == TipoPessoa.FISICA) {
			fisicaRepository.findByPessoaId(id).ifPresent(pf -> {
				f.setCpf(pf.getCpf());
				f.setIdentidade(pf.getIdentidade());
				f.setDtNascimento(pf.getDtNascimento());
			});
		} else {
			juridicaRepository.findByPessoaId(id).ifPresent(pj -> {
				f.setRazao(pj.getRazao());
				f.setFantasia(pj.getFantasia());
				f.setCnpj(pj.getCnpj());
				f.setInscricaoEstadual(pj.getInscricaoEstadual());
				f.setInscricaoMunicipal(pj.getInscricaoMunicipal());
			});
		}
		return f;
	}

	@Override
	public Pessoa salvar(PessoaForm form) {
		Pessoa pessoa;
		if (form.getId() != null) {
			pessoa = buscarPorId(form.getId());
		} else {
			pessoa = new Pessoa();
			pessoa.setProprietario(contaService.usuarioLogado());
		}
		pessoa.setNome(form.getNome());
		pessoa.setTipo(form.getTipo());
		pessoa = pessoaRepository.save(pessoa);

		if (form.getTipo() == TipoPessoa.FISICA) {
			PessoaFisica pf = fisicaRepository.findByPessoaId(pessoa.getId()).orElseGet(PessoaFisica::new);
			pf.setPessoa(pessoa);
			pf.setCpf(form.getCpf());
			pf.setIdentidade(form.getIdentidade());
			pf.setDtNascimento(form.getDtNascimento());
			fisicaRepository.save(pf);
		} else {
			PessoaJuridica pj = juridicaRepository.findByPessoaId(pessoa.getId()).orElseGet(PessoaJuridica::new);
			pj.setPessoa(pessoa);
			pj.setRazao(form.getRazao());
			pj.setFantasia(form.getFantasia());
			pj.setCnpj(form.getCnpj());
			pj.setInscricaoEstadual(form.getInscricaoEstadual());
			pj.setInscricaoMunicipal(form.getInscricaoMunicipal());
			juridicaRepository.save(pj);
		}
		return pessoa;
	}

	@Override
	public void excluir(Long id) {
		pessoaRepository.delete(buscarPorId(id));
	}

	@Override
	@Transactional(readOnly = true)
	public long contar() {
		return pessoaRepository.countByProprietario(contaService.usuarioLogado());
	}

}
