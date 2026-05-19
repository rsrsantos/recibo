package com.br.rr.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.br.rr.dto.ReciboForm;
import com.br.rr.models.Recibo;

public interface ReciboService {

	Page<Recibo> listar(Pageable pageable);

	Recibo buscarPorId(Long id);

	Recibo emitir(ReciboForm form);

	void excluir(Long id);

	long contar();

	BigDecimal somaTotal();

	List<Recibo> ultimos(int quantidade);

	/** Próximo número sugerido (sequencial por usuário). */
	long proximoNumero();

}
