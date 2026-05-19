package com.br.rr.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.br.rr.dto.PlanoForm;
import com.br.rr.models.Plano;

public interface PlanoService {

	Page<Plano> listar(Pageable pageable);

	List<Plano> listarAtivos();

	Plano buscarPorId(Long id);

	Plano salvar(Plano plano);

	PlanoForm carregarForm(Long id);

	Plano salvarForm(PlanoForm form);

	void excluir(Long id);

}
