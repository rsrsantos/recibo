package com.br.rr.models;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;

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

	/**
	 * Modelos de recibo liberados por este plano.
	 * Conjunto vazio = todos os modelos liberados (padrão).
	 */
	@ElementCollection(targetClass = ModeloRecibo.class, fetch = FetchType.EAGER)
	@CollectionTable(name = "plano_modelo_recibo",
			joinColumns = @JoinColumn(name = "plano_id"))
	@Column(name = "modelo", length = 20)
	@Enumerated(EnumType.STRING)
	private Set<ModeloRecibo> modelosPermitidos = new HashSet<>();

}
