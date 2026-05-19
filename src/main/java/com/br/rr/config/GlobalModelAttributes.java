package com.br.rr.config;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.br.rr.models.Usuario;
import com.br.rr.models.UsuarioPlano;
import com.br.rr.repository.UsuarioRepository;
import com.br.rr.service.UsuarioPlanoService;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalModelAttributes {

	private final UsuarioRepository usuarioRepository;
	private final UsuarioPlanoService usuarioPlanoService;

	public GlobalModelAttributes(UsuarioRepository usuarioRepository,
			UsuarioPlanoService usuarioPlanoService) {
		this.usuarioRepository = usuarioRepository;
		this.usuarioPlanoService = usuarioPlanoService;
	}

	@ModelAttribute("currentUri")
	public String currentUri(HttpServletRequest request) {
		return request.getRequestURI();
	}

	/**
	 * Injeta alerta de pagamento quando o plano ativo tem dtFim não nulo e
	 * está vencido ou vence em até 7 dias.
	 * Valor: dias restantes (negativo = já vencido).
	 */
	@ModelAttribute("alertaPlano")
	public Long alertaPlano() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
			return null;
		}
		Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(auth.getName());
		if (usuarioOpt.isEmpty()) return null;

		Optional<UsuarioPlano> planoOpt = usuarioPlanoService.buscarAtivo(usuarioOpt.get());
		if (planoOpt.isEmpty()) return null;

		UsuarioPlano up = planoOpt.get();
		if (up.getDtFim() == null) return null; // plano gratuito sem validade

		long diasRestantes = ChronoUnit.DAYS.between(LocalDate.now(), up.getDtFim());
		if (diasRestantes <= 7) {
			return diasRestantes;
		}
		return null;
	}

}
