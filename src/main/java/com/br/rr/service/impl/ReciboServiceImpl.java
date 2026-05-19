package com.br.rr.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.br.rr.dto.ReciboForm;
import com.br.rr.exception.NegocioException;
import com.br.rr.exception.RecursoNaoEncontradoException;
import com.br.rr.models.Pessoa;
import com.br.rr.models.Recibo;
import com.br.rr.models.Usuario;
import com.br.rr.repository.ReciboRepository;
import com.br.rr.service.ContaService;
import com.br.rr.service.PessoaService;
import com.br.rr.service.ReciboService;

@Service
@Transactional
public class ReciboServiceImpl implements ReciboService {

	private final ReciboRepository repository;
	private final ContaService contaService;
	private final PessoaService pessoaService;

	public ReciboServiceImpl(ReciboRepository repository, ContaService contaService,
			PessoaService pessoaService) {
		this.repository = repository;
		this.contaService = contaService;
		this.pessoaService = pessoaService;
	}

	@Override
	@Transactional(readOnly = true)
	public Page<Recibo> listar(Pageable pageable) {
		return repository.findByUsuario(contaService.usuarioLogado(), pageable);
	}

	@Override
	@Transactional(readOnly = true)
	public Recibo buscarPorId(Long id) {
		return repository.findByIdAndUsuario(id, contaService.usuarioLogado())
				.orElseThrow(() -> new RecursoNaoEncontradoException("Recibo não encontrado: " + id));
	}

	@Override
	public Recibo emitir(ReciboForm form) {
		if (form.getDestinatarioId() == null) {
			throw new NegocioException("Selecione o destinatário do recibo.");
		}
		if (form.getVlrTotal() == null || form.getVlrTotal().signum() < 0) {
			throw new NegocioException("Informe um valor total válido.");
		}
		Usuario usuario = contaService.usuarioLogado();
		Pessoa destinatario = pessoaService.buscarPorId(form.getDestinatarioId());

		Recibo recibo = new Recibo();
		recibo.setUsuario(usuario);
		recibo.setDestinatario(destinatario);
		recibo.setReferente(form.getReferente());
		recibo.setObservacao(form.getObservacao());
		recibo.setDataGeracao(form.getDataGeracao() != null ? form.getDataGeracao() : LocalDate.now());
		recibo.setVlrTotal(form.getVlrTotal());

		String numero = form.getNRecibo();
		if (numero == null || numero.isBlank()) {
			numero = String.valueOf(repository.countByUsuario(usuario) + 1);
		}
		recibo.setNRecibo(numero);

		return repository.save(recibo);
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

	@Override
	@Transactional(readOnly = true)
	public BigDecimal somaTotal() {
		return repository.somaTotalPorUsuario(contaService.usuarioLogado());
	}

	@Override
	@Transactional(readOnly = true)
	public List<Recibo> ultimos(int quantidade) {
		return repository.ultimos(contaService.usuarioLogado(), PageRequest.of(0, quantidade));
	}

	@Override
	@Transactional(readOnly = true)
	public long proximoNumero() {
		return repository.countByUsuario(contaService.usuarioLogado()) + 1;
	}

}
