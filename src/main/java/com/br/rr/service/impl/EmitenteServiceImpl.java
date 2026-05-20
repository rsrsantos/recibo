package com.br.rr.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.br.rr.dto.EmitenteForm;
import com.br.rr.exception.NegocioException;
import com.br.rr.models.Emitente;
import com.br.rr.models.Usuario;
import com.br.rr.repository.EmitenteRepository;
import com.br.rr.service.ContaService;
import com.br.rr.service.EmitenteService;

@Service
@Transactional
public class EmitenteServiceImpl implements EmitenteService {

	private final EmitenteRepository repository;
	private final ContaService contaService;

	public EmitenteServiceImpl(EmitenteRepository repository, ContaService contaService) {
		this.repository = repository;
		this.contaService = contaService;
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Emitente> buscarDoUsuarioLogado() {
		return repository.findByUsuario(contaService.usuarioLogado());
	}

	@Override
	@Transactional(readOnly = true)
	public EmitenteForm carregarForm() {
		EmitenteForm form = new EmitenteForm();
		buscarDoUsuarioLogado().ifPresent(e -> {
			form.setNome(e.getNome());
			form.setTipo(e.getTipo());
			form.setDocumento(e.getDocumento());
			form.setRazaoSocial(e.getRazaoSocial());
			form.setTelefone(e.getTelefone());
			form.setEmail(e.getEmail());
			form.setCep(e.getCep());
			form.setLogradouro(e.getLogradouro());
			form.setNumero(e.getNumero());
			form.setBairro(e.getBairro());
			form.setCidade(e.getCidade());
			form.setEstado(e.getEstado());
		});
		return form;
	}

	@Override
	public Emitente salvar(EmitenteForm form) {
		Usuario usuario = contaService.usuarioLogado();
		Emitente emitente = repository.findByUsuario(usuario).orElseGet(Emitente::new);
		emitente.setUsuario(usuario);
		emitente.setNome(form.getNome());
		emitente.setTipo(form.getTipo());
		emitente.setDocumento(form.getDocumento());
		emitente.setRazaoSocial(form.getRazaoSocial());
		emitente.setTelefone(form.getTelefone());
		emitente.setEmail(form.getEmail());
		emitente.setCep(form.getCep());
		emitente.setLogradouro(form.getLogradouro());
		emitente.setNumero(form.getNumero());
		emitente.setBairro(form.getBairro());
		emitente.setCidade(form.getCidade());
		emitente.setEstado(form.getEstado());
		return repository.save(emitente);
	}

	@Override
	@Transactional(readOnly = true)
	public byte[] buscarLogo() {
		return buscarDoUsuarioLogado().map(Emitente::getLogo).orElse(null);
	}

	@Override
	@Transactional(readOnly = true)
	public String buscarLogoTipo() {
		return buscarDoUsuarioLogado().map(Emitente::getLogoTipo).orElse(null);
	}

	@Override
	public void salvarLogo(MultipartFile arquivo) {
		if (arquivo == null || arquivo.isEmpty()) {
			throw new NegocioException("Nenhum arquivo selecionado.");
		}
		String mime = arquivo.getContentType();
		if (!List.of("image/png", "image/jpeg").contains(mime)) {
			throw new NegocioException("Formato inválido. Envie PNG ou JPEG.");
		}
		if (arquivo.getSize() > 512_000) {
			throw new NegocioException("Imagem muito grande. Tamanho máximo: 500 KB.");
		}
		try {
			byte[] bytes = arquivo.getBytes();
			Usuario usuario = contaService.usuarioLogado();
			Emitente emitente = repository.findByUsuario(usuario).orElseGet(Emitente::new);
			emitente.setUsuario(usuario);
			emitente.setLogo(bytes);
			emitente.setLogoTipo(mime);
			repository.save(emitente);
		} catch (IOException e) {
			throw new NegocioException("Erro ao processar a imagem.");
		}
	}

	@Override
	public void removerLogo() {
		buscarDoUsuarioLogado().ifPresent(e -> {
			e.setLogo(null);
			e.setLogoTipo(null);
			repository.save(e);
		});
	}

}
