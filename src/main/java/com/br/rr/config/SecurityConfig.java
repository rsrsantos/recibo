package com.br.rr.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import com.br.rr.security.GoogleOAuth2UserService;

@Configuration
public class SecurityConfig {

	private final PlanoSuccessHandler planoSuccessHandler;
	private final GoogleOAuth2UserService googleOAuth2UserService;
	private final OAuth2Config oauth2Config;

	public SecurityConfig(PlanoSuccessHandler planoSuccessHandler,
			GoogleOAuth2UserService googleOAuth2UserService,
			OAuth2Config oauth2Config) {
		this.planoSuccessHandler = planoSuccessHandler;
		this.googleOAuth2UserService = googleOAuth2UserService;
		this.oauth2Config = oauth2Config;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		// Redireciona para /login?naoConfirmado quando a conta ainda não foi ativada
		SimpleUrlAuthenticationFailureHandler failureHandler =
				new SimpleUrlAuthenticationFailureHandler() {
					@Override
					public void onAuthenticationFailure(
							jakarta.servlet.http.HttpServletRequest request,
							jakarta.servlet.http.HttpServletResponse response,
							org.springframework.security.core.AuthenticationException exception)
							throws java.io.IOException {
						if (exception.getCause() instanceof DisabledException
								|| exception instanceof DisabledException) {
							getRedirectStrategy().sendRedirect(request, response,
									"/login?naoConfirmado");
						} else {
							getRedirectStrategy().sendRedirect(request, response, "/login?error");
						}
					}
				};

		http
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(
								"/login", "/cadastro", "/cadastro/aguardando-confirmacao",
								"/confirmar-email", "/reenviar-confirmacao",
								"/inicio",
								"/bootstrap/**", "/vendor/**", "/css/**",
								"/js/**", "/images/**", "/webjars/**",
								"/pagamento/webhook")
						.permitAll()
						.requestMatchers("/admin/**").hasRole("ADMINISTRADOR")
						.anyRequest().authenticated())
				.formLogin(form -> form
						.loginPage("/login")
						.usernameParameter("email")
						.passwordParameter("senha")
						.successHandler(planoSuccessHandler)
						.failureHandler(failureHandler)
						.permitAll())
				.logout(logout -> logout
						.logoutSuccessUrl("/login?logout")
						.permitAll())
				.csrf(csrf -> csrf.disable());

		if (oauth2Config.isGoogleEnabled()) {
			http.oauth2Login(oauth -> oauth
					.loginPage("/login")
					.userInfoEndpoint(u -> u.userService(googleOAuth2UserService))
					.successHandler(planoSuccessHandler));
		}

		return http.build();
	}

}
