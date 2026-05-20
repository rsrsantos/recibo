package com.br.rr.security;

import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.br.rr.models.Usuario;
import com.br.rr.repository.UsuarioRepository;

@Service
public class UsuarioDetailsService implements UserDetailsService {

	private final UsuarioRepository usuarioRepository;

	public UsuarioDetailsService(UsuarioRepository usuarioRepository) {
		this.usuarioRepository = usuarioRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Usuario usuario = usuarioRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));

		// Usuários Google não têm senha — bloqueia login por formulário para eles
		String senha = usuario.getSenhaHash() != null
				? usuario.getSenhaHash() : "{noop}__google_only__";

		return User.withUsername(usuario.getEmail())
				.password(senha)
				.disabled(!usuario.isAtivo())
				.authorities(usuario.getPerfis().stream()
						.map(p -> new SimpleGrantedAuthority("ROLE_" + p.getNome()))
						.collect(Collectors.toList()))
				.build();
	}

}
