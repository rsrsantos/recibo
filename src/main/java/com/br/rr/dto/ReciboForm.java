package com.br.rr.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.br.rr.models.ModeloRecibo;

import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReciboForm {

	@NotNull(message = "Selecione o cliente")
	private Long destinatarioId;

	private ModeloRecibo modelo = ModeloRecibo.PADRAO;

	private String nRecibo;

	@NotNull(message = "Informe o valor total")
	private BigDecimal vlrTotal;

	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate dataGeracao;

	private String referente;

	private String observacao;

}
