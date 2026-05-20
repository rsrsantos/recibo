package com.br.rr.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.br.rr.security.GoogleOAuth2UserService;

@Configuration
public class SecurityConfig {

	private final PlanoSuccessHandler planoSuccessHandler;
	private final GoogleOAuth2UserService googleOAuth2UserService;

	public SecurityConfig(PlanoSuccessHandler planoSuccessHandler,
			GoogleOAuth2UserService googleOAuth2UserService) {
		this.planoSuccessHandler = planoSuccessHandler;
		this.googleOAuth2UserService = googleOAuth2UserService;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/login", "/cadastro", "/inicio",
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
						.permitAll())
				.oauth2Login(oauth -> oauth
						.loginPage("/login")
						.userInfoEndpoint(u -> u.userService(googleOAuth2UserService))
						.successHandler(planoSuccessHandler))
				.logout(logout -> logout
						.logoutSuccessUrl("/login?logout")
						.permitAll())
				.csrf(csrf -> csrf.disable());
		return http.build();
	}

}
