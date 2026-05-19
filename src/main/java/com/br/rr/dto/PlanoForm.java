package com.br.rr.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlanoForm {

	private Long id;

	@NotBlank(message = "O nome é obrigatório")
	private String nome;

	private String descricao;

	@NotNull(message = "Informe o preço mensal")
	@PositiveOrZero(message = "O preço não pode ser negativo")
	private BigDecimal precoMensal = BigDecimal.ZERO;

	/** Nulo = recibos ilimitados. */
	private Integer limiteRecibos;

	private boolean funcPdf;

	private boolean funcRelatorio;

	private boolean ativo = true;

	private boolean temCarencia = true;

	@PositiveOrZero(message = "A carência não pode ser negativa")
	private Integer carenciaDias = 30;

}
