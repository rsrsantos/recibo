package com.br.rr.security;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.br.rr.models.Usuario;

@Service
public class GoogleOAuth2UserService extends DefaultOAuth2UserService {

	private final GoogleUsuarioService googleUsuarioService;

	public GoogleOAuth2UserService(GoogleUsuarioService googleUsuarioService) {
		this.googleUsuarioService = googleUsuarioService;
	}

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) {
		OAuth2User oAuth2User = super.loadUser(userRequest);
		Map<String, Object> attrs = oAuth2User.getAttributes();

		String googleId = (String) attrs.get("sub");
		String email    = (String) attrs.get("email");
		String nome     = (String) attrs.getOrDefault("name", email);

		// Operações de DB em serviço separado — garante commit antes de retornar
		Usuario usuario = googleUsuarioService.buscarOuCriar(googleId, email, nome);

		List<SimpleGrantedAuthority> authorities = usuario.getPerfis().stream()
				.map(p -> new SimpleGrantedAuthority("ROLE_" + p.getNome()))
				.toList();

		return new DefaultOAuth2User(authorities, attrs, "email");
	}

}
