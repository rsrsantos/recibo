package com.br.rr.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Recibo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@ManyToOne
	@JoinColumn(name = "empresa_id")
	private Empresa empresa;

	@ManyToOne
	@JoinColumn(name = "cliente_id")
	private Cliente cliente;

	private Integer numeroRecibo;

	private String observacao;

	private Double valorTotal;

	@OneToMany(mappedBy = "recibo", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ReciboProduto> itens = new ArrayList<>();

	@CreationTimestamp
	@DateTimeFormat(pattern = "dd-MM-yyyy")
	private Date dataInclusao;

	/**
	 * Vincula os itens a este recibo e recalcula o valor total a partir
	 * dos subtotais de cada item. Chamado pela camada de serviço antes de salvar.
	 */
	public void recalcularTotal() {
		double total = 0d;
		for (ReciboProduto item : itens) {
			item.setRecibo(this);
			total += item.getSubtotal();
		}
		this.valorTotal = total;
	}

}
