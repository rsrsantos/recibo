package com.br.rr.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.br.rr.models.Usuario;
import com.br.rr.repository.UsuarioRepository;
import com.br.rr.service.AssinaturaGuard;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalModelAttributes {

	private final UsuarioRepository usuarioRepository;
	private final AssinaturaGuard assinaturaGuard;

	@Value("${app.whatsapp:}")
	private String whatsapp;

	public GlobalModelAttributes(UsuarioRepository usuarioRepository,
			AssinaturaGuard assinaturaGuard) {
		this.usuarioRepository = usuarioRepository;
		this.assinaturaGuard = assinaturaGuard;
	}

	@ModelAttribute("whatsapp")
	public String whatsapp() {
		return (whatsapp != null && !whatsapp.isBlank() && !whatsapp.startsWith("55SEU")) ? whatsapp : null;
	}

	@ModelAttribute("currentUri")
	public String currentUri(HttpServletRequest request) {
		return request.getRequestURI();
	}

	private Usuario usuarioLogado() {
		try {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
				return null;
			}
			if (auth instanceof OAuth2AuthenticationToken oauth2) {
				String googleId = oauth2.getPrincipal().getAttribute("sub");
				if (googleId != null) {
					return usuarioRepository.findByGoogleId(googleId).orElse(null);
				}
				String email = oauth2.getPrincipal().getAttribute("email");
				return email != null ? usuarioRepository.findByEmail(email).orElse(null) : null;
			}
			return usuarioRepository.findByEmail(auth.getName()).orElse(null);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Dias restantes do plano para o modal de lembrete (≤7 dias ou vencido).
	 * Negativo = vencido. Null = não exibir.
	 */
	@ModelAttribute("alertaPlano")
	public Long alertaPlano() {
		try {
			Usuario usuario = usuarioLogado();
			if (usuario == null) return null;
			Long dias = assinaturaGuard.diasRestantes(usuario);
			return (dias != null && dias <= 7) ? dias : null;
		} catch (Exception e) { return null; }
	}

	@ModelAttribute("planoBloqueado")
	public boolean planoBloqueado() {
		try {
			Usuario usuario = usuarioLogado();
			return usuario != null && assinaturaGuard.bloqueado(usuario);
		} catch (Exception e) { return false; }
	}

	@ModelAttribute("limiteRecibosAtingido")
	public boolean limiteRecibosAtingido() {
		try {
			Usuario usuario = usuarioLogado();
			return usuario != null && assinaturaGuard.limiteRecibosAtingido(usuario);
		} catch (Exception e) { return false; }
	}

	/** Nome da pessoa logada para exibição na navbar (evita mostrar Google ID). */
	@ModelAttribute("nomeUsuario")
	public String nomeUsuario() {
		try {
			Usuario usuario = usuarioLogado();
			if (usuario == null) return null;
			if (usuario.getPessoa() != null && usuario.getPessoa().getNome() != null) {
				return usuario.getPessoa().getNome();
			}
			return usuario.getEmail();
		} catch (Exception e) {
			return null;
		}
	}

}
