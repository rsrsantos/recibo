package com.br.rr.models;

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
public class PessoaContato {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String valor;

	private boolean ativo = true;

	@ManyToOne(optional = false)
	@JoinColumn(name = "tipo_contato_id", nullable = false)
	private TipoContato tipoContato;

	@ManyToOne(optional = false)
	@JoinColumn(name = "pessoa_id", nullable = false)
	private Pessoa pessoa;

}
