package com.br.rr.security;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.br.rr.models.Perfil;
import com.br.rr.models.Pessoa;
import com.br.rr.models.TipoPessoa;
import com.br.rr.models.Usuario;
import com.br.rr.repository.PerfilRepository;
import com.br.rr.repository.PessoaRepository;
import com.br.rr.repository.UsuarioRepository;

@Service
public class GoogleUsuarioService {

	private final UsuarioRepository usuarioRepository;
	private final PerfilRepository perfilRepository;
	private final PessoaRepository pessoaRepository;

	public GoogleUsuarioService(UsuarioRepository usuarioRepository,
			PerfilRepository perfilRepository, PessoaRepository pessoaRepository) {
		this.usuarioRepository = usuarioRepository;
		this.perfilRepository = perfilRepository;
		this.pessoaRepository = pessoaRepository;
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Usuario buscarOuCriar(String googleId, String email, String nome) {
		Usuario usuario = usuarioRepository.findByGoogleId(googleId)
				.orElseGet(() -> usuarioRepository.findByEmail(email).orElse(null));

		if (usuario == null) {
			Pessoa pessoa = new Pessoa();
			pessoa.setNome(nome);
			pessoa.setTipo(TipoPessoa.FISICA);
			pessoa = pessoaRepository.save(pessoa);

			Perfil perfilCliente = perfilRepository.findByNome("CLIENTE")
					.orElseThrow(() -> new IllegalStateException("Perfil CLIENTE não encontrado."));

			usuario = new Usuario();
			usuario.setEmail(email);
			usuario.setGoogleId(googleId);
			usuario.setPessoa(pessoa);
			usuario.setAtivo(true);
			usuario.getPerfis().add(perfilCliente);
			usuario = usuarioRepository.save(usuario);
		} else if (usuario.getGoogleId() == null) {
			usuario.setGoogleId(googleId);
			usuario = usuarioRepository.save(usuario);
		}

		return usuario;
	}

}
