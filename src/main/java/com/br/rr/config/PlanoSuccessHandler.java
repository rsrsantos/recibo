package com.br.rr.config;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class PlanoSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	public static final String SESSION_PLANO = "plano_escolhido";

	public PlanoSuccessHandler() {
		setDefaultTargetUrl("/");
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		HttpSession session = request.getSession(false);
		if (session != null) {
			Long planoId = (Long) session.getAttribute(SESSION_PLANO);
			if (planoId != null) {
				session.removeAttribute(SESSION_PLANO);
				getRedirectStrategy().sendRedirect(request, response, "/planos/ativar?plano=" + planoId);
				return;
			}
		}
		super.onAuthenticationSuccess(request, response, authentication);
	}

}
