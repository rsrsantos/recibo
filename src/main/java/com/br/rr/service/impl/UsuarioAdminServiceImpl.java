package com.br.rr.service.impl;

import java.util.HashSet;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.br.rr.dto.UsuarioEditForm;
import com.br.rr.exception.NegocioException;
import com.br.rr.exception.RecursoNaoEncontradoException;
import com.br.rr.models.Perfil;
import com.br.rr.models.Usuario;
import com.br.rr.repository.PerfilRepository;
import com.br.rr.repository.UsuarioRepository;
import com.br.rr.service.UsuarioAdminService;
import com.br.rr.service.UsuarioPlanoService;

@Service
@Transactional
public class UsuarioAdminServiceImpl implements UsuarioAdminService {

	private final UsuarioRepository usuarioRepository;
	private final UsuarioPlanoService usuarioPlanoService;
	private final PerfilRepository perfilRepository;
	private final PasswordEncoder passwordEncoder;

	public UsuarioAdminServiceImpl(UsuarioRepository usuarioRepository,
			UsuarioPlanoService usuarioPlanoService, PerfilRepository perfilRepository,
			PasswordEncoder passwordEncoder) {
		this.usuarioRepository = usuarioRepository;
		this.usuarioPlanoService = usuarioPlanoService;
		this.perfilRepository = perfilRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	@Transactional(readOnly = true)
	public Page<Usuario> listar(Pageable pageable) {
		return usuarioRepository.findAll(pageable);
	}

	@Override
	public void alternarAtivo(Long id) {
		Usuario usuario = usuarioRepository.findById(id)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado: " + id));

		String emailLogado = SecurityContextHolder.getContext().getAuthentication().getName();
		if (usuario.getEmail().equals(emailLogado)) {
			throw new NegocioException("Você não pode desativar a si mesmo.");
		}

		usuario.setAtivo(!usuario.isAtivo());
		usuarioRepository.save(usuario);
	}

	@Override
	@Transactional(readOnly = true)
	public String planoAtivoNome(Usuario usuario) {
		return usuarioPlanoService.buscarAtivo(usuario)
				.map(up -> up.getPlano().getNome())
				.orElse(null);
	}

	@Override
	@Transactional(readOnly = true)
	public UsuarioEditForm carregarEdicao(Long id) {
		Usuario u = usuarioRepository.findById(id)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado: " + id));
		UsuarioEditForm f = new UsuarioEditForm();
		f.setId(u.getId());
		f.setEmail(u.getEmail());
		f.setNome(u.getPessoa() != null ? u.getPessoa().getNome() : "");
		return f;
	}

	@Override
	public void salvarEdicao(UsuarioEditForm form) {
		Usuario u = usuarioRepository.findById(form.getId())
				.orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado: " + form.getId()));

		usuarioRepository.findByEmail(form.getEmail())
				.filter(outro -> !outro.getId().equals(u.getId()))
				.ifPresent(outro -> {
					throw new NegocioException("Já existe outro usuário com este e-mail.");
				});

		u.setEmail(form.getEmail());
		if (u.getPessoa() != null) {
			u.getPessoa().setNome(form.getNome());
		}

		String nova = form.getNovaSenha();
		if (nova != null && !nova.isBlank()) {
			if (nova.length() < 6) {
				throw new NegocioException("A nova senha deve ter ao menos 6 caracteres.");
			}
			u.setSenhaHash(passwordEncoder.encode(nova));
		}

		usuarioRepository.save(u);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Perfil> listarPerfis() {
		return perfilRepository.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public String perfilAtualNome(Usuario usuario) {
		return usuario.getPerfis().stream()
				.map(Perfil::getNome)
				.findFirst()
				.orElse("—");
	}

	@Override
	public void alterarPerfil(Long usuarioId, Long perfilId) {
		Usuario usuario = usuarioRepository.findById(usuarioId)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado: " + usuarioId));

		String emailLogado = SecurityContextHolder.getContext().getAuthentication().getName();
		if (usuario.getEmail().equals(emailLogado)) {
			throw new NegocioException("Você não pode alterar o seu próprio perfil.");
		}

		Perfil perfil = perfilRepository.findById(perfilId)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Perfil não encontrado: " + perfilId));

		usuario.setPerfis(new HashSet<>(java.util.List.of(perfil)));
		usuarioRepository.save(usuario);
	}

}
