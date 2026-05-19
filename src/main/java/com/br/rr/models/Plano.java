package com.br.rr.models;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Plano {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String nome;

	private String descricao;

	@Column(name = "preco_mensal", nullable = false)
	private BigDecimal precoMensal = BigDecimal.ZERO;

	private Integer limiteRecibos;

	private boolean funcPdf;

	private boolean funcRelatorio;

	private boolean ativo = true;

	@Column(name = "tem_carencia", nullable = false)
	private boolean temCarencia = true;

	@Column(name = "carencia_dias", nullable = false)
	private int carenciaDias = 30;

}
