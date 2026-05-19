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
import com.br.rr.models.Plano;
import com.br.rr.models.UsuarioPlano;
import com.br.rr.repository.ReciboRepository;
import com.br.rr.service.ContaService;
import com.br.rr.service.PessoaService;
import com.br.rr.service.ReciboService;
import com.br.rr.service.UsuarioPlanoService;

@Service
@Transactional
public class ReciboServiceImpl implements ReciboService {

	private final ReciboRepository repository;
	private final ContaService contaService;
	private final PessoaService pessoaService;
	private final UsuarioPlanoService usuarioPlanoService;

	public ReciboServiceImpl(ReciboRepository repository, ContaService contaService,
			PessoaService pessoaService, UsuarioPlanoService usuarioPlanoService) {
		this.repository = repository;
		this.contaService = contaService;
		this.pessoaService = pessoaService;
		this.usuarioPlanoService = usuarioPlanoService;
	}

	/** Bloqueia se o plano ativo define limite e ele já foi atingido no mês. */
	private void validarLimiteMensal(Usuario usuario) {
		UsuarioPlano ativo = usuarioPlanoService.buscarAtivo(usuario).orElse(null);
		if (ativo == null) {
			return; // sem plano: o paywall já barra o acesso
		}
		Plano plano = ativo.getPlano();
		Integer limite = plano.getLimiteRecibos();
		if (limite == null) {
			return; // ilimitado
		}
		LocalDate hoje = LocalDate.now();
		LocalDate inicio = hoje.withDayOfMonth(1);
		LocalDate fim = hoje.withDayOfMonth(hoje.lengthOfMonth());
		long usados = repository.countNoPeriodo(usuario, inicio, fim);
		if (usados >= limite) {
			throw new NegocioException("Você atingiu o limite de " + limite
					+ " recibos/mês do plano \"" + plano.getNome()
					+ "\". Faça upgrade de plano em Meu Perfil para emitir mais.");
		}
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
		validarLimiteMensal(usuario);
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
