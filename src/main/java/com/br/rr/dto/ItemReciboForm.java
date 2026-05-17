package com.br.rr.dto;

import lombok.Getter;
import lombok.Setter;

/** Um item informado no formulário de emissão de recibo. */
@Getter
@Setter
public class ItemReciboForm {

	private Long produtoId;

	private Integer quantidade;

}
