package com.br.rr.service;

import java.util.Optional;

import com.br.rr.dto.EmitenteForm;
import com.br.rr.models.Emitente;

public interface EmitenteService {

	Optional<Emitente> buscarDoUsuarioLogado();

	EmitenteForm carregarForm();

	Emitente salvar(EmitenteForm form);

}
