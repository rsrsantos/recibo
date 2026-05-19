package com.br.rr.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

	private final PlanoSuccessHandler planoSuccessHandler;

	public SecurityConfig(PlanoSuccessHandler planoSuccessHandler) {
		this.planoSuccessHandler = planoSuccessHandler;
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
								"/js/**", "/images/**", "/webjars/**")
						.permitAll()
						.requestMatchers("/admin/**").hasRole("ADMINISTRADOR")
						.anyRequest().authenticated())
				.formLogin(form -> form
						.loginPage("/login")
						.usernameParameter("email")
						.passwordParameter("senha")
						.successHandler(planoSuccessHandler)
						.permitAll())
				.logout(logout -> logout
						.logoutSuccessUrl("/login?logout")
						.permitAll())
				.csrf(csrf -> csrf.disable());
		return http.build();
	}

}
