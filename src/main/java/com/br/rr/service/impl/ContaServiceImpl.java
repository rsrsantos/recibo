package com.br.rr.service.impl;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.br.rr.exception.NegocioException;
import com.br.rr.exception.RecursoNaoEncontradoException;
import com.br.rr.models.Usuario;
import com.br.rr.repository.UsuarioRepository;
import com.br.rr.service.ContaService;

@Service
@Transactional
public class ContaServiceImpl implements ContaService {

	private final UsuarioRepository usuarioRepository;
	private final PasswordEncoder passwordEncoder;

	public ContaServiceImpl(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
		this.usuarioRepository = usuarioRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	@Transactional(readOnly = true)
	public Usuario usuarioLogado() {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		return usuarioRepository.findByUsername(username)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Usuário logado não encontrado."));
	}

	@Override
	public void alterarSenha(String senhaAtual, String novaSenha, String confirmacao) {
		Usuario usuario = usuarioLogado();

		if (senhaAtual == null || !passwordEncoder.matches(senhaAtual, usuario.getSenha())) {
			throw new NegocioException("A senha atual está incorreta.");
		}
		if (novaSenha == null || novaSenha.length() < 6) {
			throw new NegocioException("A nova senha deve ter ao menos 6 caracteres.");
		}
		if (!novaSenha.equals(confirmacao)) {
			throw new NegocioException("A confirmação não confere com a nova senha.");
		}

		usuario.setSenha(passwordEncoder.encode(novaSenha));
		usuarioRepository.save(usuario);
	}

}
