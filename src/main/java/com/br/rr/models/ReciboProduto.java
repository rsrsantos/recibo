package com.br.rr.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Entity
public class ReciboProduto {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@ManyToOne
	@JoinColumn(name = "produto_id")
	private Produto produto;

	@ManyToOne
	@JoinColumn(name = "recibo_id")
	@JsonIgnore
	private Recibo recibo;

	private Integer quantidade;

	/** Valor unitário no momento da emissão (snapshot do preço do produto). */
	private Double valor;

	/** Subtotal do item = valor unitário x quantidade. */
	public double getSubtotal() {
		double unitario = valor != null ? valor : 0d;
		int qtd = quantidade != null ? quantidade : 0;
		return unitario * qtd;
	}

}
