package com.br.rr.dto;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

/**
 * Dados que chegam do formulário de emissão de recibo. Desacopla a tela
 * da entidade JPA; a conversão para {@code Recibo} é feita na camada de serviço.
 */
@Getter
@Setter
public class ReciboForm {

	@NotNull(message = "Selecione um cliente")
	private Long clienteId;

	private String observacao;

	private List<ItemReciboForm> itens = new ArrayList<>();

}
