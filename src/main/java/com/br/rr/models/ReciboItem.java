package com.br.rr.models;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class ReciboItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Integer qtde = 1;

	@Column(name = "vlr_unitario", nullable = false)
	private BigDecimal vlrUnitario = BigDecimal.ZERO;

	@Column(name = "vlr_total", nullable = false)
	private BigDecimal vlrTotal = BigDecimal.ZERO;

	private String observacao;

	@ManyToOne(optional = false)
	@JoinColumn(name = "recibo_id", nullable = false)
	private Recibo recibo;

	@ManyToOne(optional = false)
	@JoinColumn(name = "produto_servico_id", nullable = false)
	private ProdutoServico produtoServico;

	/** Recalcula o subtotal (unitário x quantidade). */
	public void recalcular() {
		int q = qtde != null ? qtde : 0;
		BigDecimal u = vlrUnitario != null ? vlrUnitario : BigDecimal.ZERO;
		this.vlrTotal = u.multiply(BigDecimal.valueOf(q));
	}

}
