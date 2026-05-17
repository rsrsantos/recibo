package com.br.rr.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.br.rr.dto.ReciboForm;
import com.br.rr.models.Recibo;

public interface ReciboService {

	Page<Recibo> listar(Pageable pageable);

	Recibo buscarPorId(long id);

	/**
	 * Cria um recibo a partir dos dados do formulário: resolve cliente/produtos
	 * pelos ids, congela o preço unitário de cada item, calcula o total e gera
	 * o número sequencial.
	 */
	Recibo emitir(ReciboForm form);

	void excluir(long id);

	long contar();

	double somaValorTotal();

	List<Recibo> ultimos(int quantidade);

}
