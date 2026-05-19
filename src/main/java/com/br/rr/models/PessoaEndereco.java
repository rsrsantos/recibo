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
public class PessoaEndereco {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String logradouro;

	private String bairro;

	private Integer numero;

	private String cep;

	private String cidade;

	private String estado;

	private boolean principal;

	@ManyToOne(optional = false)
	@JoinColumn(name = "pessoa_id", nullable = false)
	private Pessoa pessoa;

}
