package com.br.rr.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.br.rr.models.Usuario;
import com.br.rr.repository.UsuarioRepository;
import com.br.rr.service.AssinaturaGuard;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Paywall: se a assinatura está bloqueada, só libera a tela de plano
 * (/perfil), ativação de plano (/planos/**), logout e recursos estáticos.
 */
@Component
public class PaywallInterceptor implements HandlerInterceptor {

	private final AssinaturaGuard guard;
	private final UsuarioRepository usuarioRepository;

	public PaywallInterceptor(AssinaturaGuard guard, UsuarioRepository usuarioRepository) {
		this.guard = guard;
		this.usuarioRepository = usuarioRepository;
	}

	private static final String[] LIBERADOS = {
			"/perfil", "/planos", "/logout", "/login", "/cadastro", "/inicio",
			"/css/", "/js/", "/vendor/", "/bootstrap/", "/images/", "/webjars/",
			"/error", "/favicon"
	};

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
			Object handler) throws Exception {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
			return true; // não autenticado: Spring Security trata
		}

		String uri = request.getRequestURI();
		String ctx = request.getContextPath();
		if (ctx != null && !ctx.isEmpty() && uri.startsWith(ctx)) {
			uri = uri.substring(ctx.length());
		}
		for (String libre : LIBERADOS) {
			if (uri.equals(libre) || uri.startsWith(libre)) {
				return true;
			}
		}

		Usuario usuario = usuarioRepository.findByEmail(auth.getName()).orElse(null);
		if (usuario == null || !guard.bloqueado(usuario)) {
			return true;
		}

		response.sendRedirect((ctx != null ? ctx : "") + "/perfil#planos");
		return false;
	}

}
