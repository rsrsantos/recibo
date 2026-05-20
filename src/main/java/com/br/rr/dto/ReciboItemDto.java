package com.br.rr.dto;

import lombok.Getter;

@Getter
public class ReciboItemDto {

	private final String descricao;
	private final Integer qtde;
	private final String vlrUnitario;
	private final String vlrTotal;

	public ReciboItemDto(String descricao, Integer qtde, String vlrUnitario, String vlrTotal) {
		this.descricao = descricao;
		this.qtde = qtde;
		this.vlrUnitario = vlrUnitario;
		this.vlrTotal = vlrTotal;
	}

}
