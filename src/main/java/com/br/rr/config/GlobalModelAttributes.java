package com.br.rr.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.br.rr.models.Usuario;
import com.br.rr.repository.UsuarioRepository;
import com.br.rr.service.AssinaturaGuard;
import com.br.rr.service.ContaService;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalModelAttributes {

	private final UsuarioRepository usuarioRepository;
	private final AssinaturaGuard assinaturaGuard;

	public GlobalModelAttributes(UsuarioRepository usuarioRepository,
			AssinaturaGuard assinaturaGuard) {
		this.usuarioRepository = usuarioRepository;
		this.assinaturaGuard = assinaturaGuard;
	}



	@ModelAttribute("currentUri")
	public String currentUri(HttpServletRequest request) {
		return request.getRequestURI();
	}

	private Usuario usuarioLogado() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
			return null;
		}
		return usuarioRepository.findByEmail(auth.getName()).orElse(null);
	}

	/**
	 * Dias restantes do plano para o modal de lembrete (≤7 dias ou vencido).
	 * Negativo = vencido. Null = não exibir.
	 */
	@ModelAttribute("alertaPlano")
	public Long alertaPlano() {
		Usuario usuario = usuarioLogado();
		if (usuario == null) return null;
		Long dias = assinaturaGuard.diasRestantes(usuario);
		return (dias != null && dias <= 7) ? dias : null;
	}

	/** Indica que a conta está bloqueada por assinatura (paywall). */
	@ModelAttribute("planoBloqueado")
	public boolean planoBloqueado() {
		Usuario usuario = usuarioLogado();
		return usuario != null && assinaturaGuard.bloqueado(usuario);
	}

	/** Indica que o limite mensal de recibos do plano ativo foi atingido. */
	@ModelAttribute("limiteRecibosAtingido")
	public boolean limiteRecibosAtingido() {
		Usuario usuario = usuarioLogado();
		return usuario != null && assinaturaGuard.limiteRecibosAtingido(usuario);
	}

}
