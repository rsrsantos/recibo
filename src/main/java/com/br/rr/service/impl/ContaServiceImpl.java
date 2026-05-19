package com.br.rr.service.impl;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.br.rr.dto.CadastroForm;
import com.br.rr.exception.NegocioException;
import com.br.rr.exception.RecursoNaoEncontradoException;
import com.br.rr.models.Perfil;
import com.br.rr.models.Pessoa;
import com.br.rr.models.TipoPessoa;
import com.br.rr.models.Usuario;
import com.br.rr.repository.PerfilRepository;
import com.br.rr.repository.PessoaRepository;
import com.br.rr.repository.UsuarioRepository;
import com.br.rr.service.ContaService;

@Service
@Transactional
public class ContaServiceImpl implements ContaService {

	private final UsuarioRepository usuarioRepository;
	private final PasswordEncoder passwordEncoder;
	private final PessoaRepository pessoaRepository;
	private final PerfilRepository perfilRepository;

	public ContaServiceImpl(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder,
			PessoaRepository pessoaRepository, PerfilRepository perfilRepository) {
		this.usuarioRepository = usuarioRepository;
		this.passwordEncoder = passwordEncoder;
		this.pessoaRepository = pessoaRepository;
		this.perfilRepository = perfilRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public Usuario usuarioLogado() {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		return usuarioRepository.findByEmail(email)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Usuário logado não encontrado."));
	}

	@Override
	public void alterarSenha(String senhaAtual, String novaSenha, String confirmacao) {
		Usuario usuario = usuarioLogado();

		if (senhaAtual == null || !passwordEncoder.matches(senhaAtual, usuario.getSenhaHash())) {
			throw new NegocioException("A senha atual está incorreta.");
		}
		if (novaSenha == null || novaSenha.length() < 6) {
			throw new NegocioException("A nova senha deve ter ao menos 6 caracteres.");
		}
		if (!novaSenha.equals(confirmacao)) {
			throw new NegocioException("A confirmação não confere com a nova senha.");
		}

		usuario.setSenhaHash(passwordEncoder.encode(novaSenha));
		usuarioRepository.save(usuario);
	}

	@Override
	public Usuario cadastrar(CadastroForm form) {
		if (usuarioRepository.findByEmail(form.getEmail()).isPresent()) {
			throw new NegocioException("Este e-mail já está cadastrado.");
		}
		if (!form.getSenha().equals(form.getConfirmacao())) {
			throw new NegocioException("A confirmação de senha não confere.");
		}

		Pessoa pessoa = new Pessoa();
		pessoa.setNome(form.getNome());
		pessoa.setTipo(TipoPessoa.FISICA);
		pessoa = pessoaRepository.save(pessoa);

		Usuario usuario = new Usuario();
		usuario.setEmail(form.getEmail());
		usuario.setSenhaHash(passwordEncoder.encode(form.getSenha()));
		usuario.setPessoa(pessoa);
		usuario.setAtivo(true);

		Perfil perfilCliente = perfilRepository.findByNome("CLIENTE")
				.orElseThrow(() -> new RecursoNaoEncontradoException("Perfil CLIENTE não encontrado."));
		usuario.getPerfis().add(perfilCliente);

		return usuarioRepository.save(usuario);
	}

}
