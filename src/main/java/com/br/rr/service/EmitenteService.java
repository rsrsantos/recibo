package com.br.rr.service;

import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import com.br.rr.dto.EmitenteForm;
import com.br.rr.models.Emitente;

public interface EmitenteService {

	Optional<Emitente> buscarDoUsuarioLogado();

	EmitenteForm carregarForm();

	Emitente salvar(EmitenteForm form);

	/** Retorna os bytes da logo do emitente autenticado, ou null se não houver. */
	byte[] buscarLogo();

	/** Retorna o MIME type da logo ("image/png" ou "image/jpeg"), ou null. */
	String buscarLogoTipo();

	/** Valida e processa upload de logo (PNG/JPEG, máx 500 KB). */
	void salvarLogo(MultipartFile arquivo);

	void removerLogo();

}
