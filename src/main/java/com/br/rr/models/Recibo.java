package com.br.rr.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Recibo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "n_recibo", length = 30, updatable = false)
	private String nRecibo;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20, updatable = false)
	private ModeloRecibo modelo = ModeloRecibo.PADRAO;

	@Column(name = "data_geracao", nullable = false, updatable = false)
	private LocalDate dataGeracao = LocalDate.now();

	@Column(name = "vlr_total", nullable = false, updatable = false)
	private BigDecimal vlrTotal = BigDecimal.ZERO;

	@Column(length = 500, updatable = false)
	private String referente;

	@Column(updatable = false)
	private String observacao;

	@ManyToOne(optional = false)
	@JoinColumn(name = "usuario_id", nullable = false, updatable = false)
	private Usuario usuario;

	@ManyToOne(optional = false)
	@JoinColumn(name = "destinatario_id", nullable = false, updatable = false)
	private Pessoa destinatario;

	@OneToMany(mappedBy = "recibo", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ReciboItem> itens = new ArrayList<>();

	/** Vincula itens e recalcula o total a partir dos subtotais. */
	public void recalcularTotal() {
		BigDecimal total = BigDecimal.ZERO;
		for (ReciboItem item : itens) {
			item.setRecibo(this);
			item.recalcular();
			total = total.add(item.getVlrTotal());
		}
		this.vlrTotal = total;
	}

}
