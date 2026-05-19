package com.br.rr.service;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.br.rr.models.Plano;
import com.br.rr.models.Usuario;
import com.br.rr.models.UsuarioPlano;
import com.br.rr.repository.ReciboRepository;

// ReciboRepository is injected here to avoid a circular dependency:
// ReciboServiceImpl → AssinaturaGuard would form a cycle if AssinaturaGuard depended on ReciboService.

/**
 * Fonte única da regra de bloqueio por assinatura.
 * Usado pelo interceptor (paywall) e pela camada de view (aviso).
 */
@Component
public class AssinaturaGuard {

	private final UsuarioPlanoService usuarioPlanoService;
	private final ReciboRepository reciboRepository;

	public AssinaturaGuard(UsuarioPlanoService usuarioPlanoService,
			ReciboRepository reciboRepository) {
		this.usuarioPlanoService = usuarioPlanoService;
		this.reciboRepository = reciboRepository;
	}

	/** Administradores não assinam plano e nunca são bloqueados. */
	public boolean isAdministrador(Usuario usuario) {
		return usuario.getPerfis().stream()
				.anyMatch(p -> "ADMINISTRADOR".equalsIgnoreCase(p.getNome()));
	}

	/**
	 * Bloqueado quando: não é admin E (não tem plano ativo OU o plano ativo
	 * tem validade e ela já passou).
	 */
	public boolean bloqueado(Usuario usuario) {
		if (usuario == null || isAdministrador(usuario)) {
			return false;
		}
		Optional<UsuarioPlano> ativo = usuarioPlanoService.buscarAtivo(usuario);
		if (ativo.isEmpty()) {
			return true;
		}
		LocalDate dtFim = ativo.get().getDtFim();
		// !isAfter cobre dtFim = hoje (carenciaDias=0) e dtFim no passado
		return dtFim != null && !dtFim.isAfter(LocalDate.now());
	}

	/** Dias restantes do plano ativo (negativo = vencido); null se não aplicável. */
	public Long diasRestantes(Usuario usuario) {
		if (usuario == null || isAdministrador(usuario)) {
			return null;
		}
		Optional<UsuarioPlano> ativo = usuarioPlanoService.buscarAtivo(usuario);
		if (ativo.isEmpty() || ativo.get().getDtFim() == null) {
			return null;
		}
		return java.time.temporal.ChronoUnit.DAYS.between(
				LocalDate.now(), ativo.get().getDtFim());
	}

	/**
	 * Retorna true quando o usuário tem plano ativo com limite de recibos
	 * e já atingiu (ou ultrapassou) esse limite no mês corrente.
	 */
	public boolean limiteRecibosAtingido(Usuario usuario) {
		if (usuario == null || isAdministrador(usuario)) {
			return false;
		}
		Optional<UsuarioPlano> ativoOpt = usuarioPlanoService.buscarAtivo(usuario);
		if (ativoOpt.isEmpty()) {
			return false; // sem plano: o paywall já bloqueia
		}
		Plano plano = ativoOpt.get().getPlano();
		Integer limite = plano.getLimiteRecibos();
		if (limite == null) {
			return false; // ilimitado
		}
		LocalDate hoje = LocalDate.now();
		long usados = reciboRepository.countNoPeriodo(
				usuario, hoje.withDayOfMonth(1), hoje.withDayOfMonth(hoje.lengthOfMonth()));
		return usados >= limite;
	}

}
