package com.br.rr.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.br.rr.dto.PerfilForm;
import com.br.rr.exception.NegocioException;
import com.br.rr.exception.RecursoNaoEncontradoException;
import com.br.rr.models.Perfil;
import com.br.rr.repository.PerfilRepository;
import com.br.rr.repository.UsuarioRepository;
import com.br.rr.service.PerfilService;

@Service
@Transactional
public class PerfilServiceImpl implements PerfilService {

	private final PerfilRepository perfilRepository;
	private final UsuarioRepository usuarioRepository;

	public PerfilServiceImpl(PerfilRepository perfilRepository, UsuarioRepository usuarioRepository) {
		this.perfilRepository = perfilRepository;
		this.usuarioRepository = usuarioRepository;
	}

	@Override
	public boolean isSistema(String nome) {
		return nome != null && SISTEMA.contains(nome.trim().toUpperCase());
	}

	@Override
	@Transactional(readOnly = true)
	public List<Perfil> listar() {
		return perfilRepository.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public long contarUsuarios(Long perfilId) {
		return usuarioRepository.contarPorPerfil(perfilId);
	}

	@Override
	@Transactional(readOnly = true)
	public PerfilForm carregarForm(Long id) {
		Perfil p = perfilRepository.findById(id)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Perfil não encontrado: " + id));
		PerfilForm f = new PerfilForm();
		f.setId(p.getId());
		f.setNome(p.getNome());
		return f;
	}

	@Override
	public Perfil salvarForm(PerfilForm form) {
		String nome = form.getNome() != null ? form.getNome().trim().toUpperCase() : "";

		if (form.getId() != null) {
			Perfil p = perfilRepository.findById(form.getId())
					.orElseThrow(() -> new RecursoNaoEncontradoException("Perfil não encontrado: " + form.getId()));
			if (isSistema(p.getNome())) {
				throw new NegocioException("O perfil de sistema \"" + p.getNome()
						+ "\" não pode ser renomeado.");
			}
			p.setNome(nome);
			return perfilRepository.save(p);
		}

		perfilRepository.findByNome(nome).ifPresent(existente -> {
			throw new NegocioException("Já existe um perfil com o nome \"" + nome + "\".");
		});

		Perfil novo = new Perfil();
		novo.setNome(nome);
		return perfilRepository.save(novo);
	}

	@Override
	public void excluir(Long id) {
		Perfil p = perfilRepository.findById(id)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Perfil não encontrado: " + id));

		if (isSistema(p.getNome())) {
			throw new NegocioException("O perfil de sistema \"" + p.getNome()
					+ "\" não pode ser excluído.");
		}
		if (usuarioRepository.contarPorPerfil(id) > 0) {
			throw new NegocioException("Há usuários usando este perfil. "
					+ "Reatribua-os antes de excluir.");
		}
		perfilRepository.delete(p);
	}

}
