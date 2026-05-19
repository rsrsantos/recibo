package com.br.rr.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class PessoaJuridica {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String razao;

	private String fantasia;

	private String cnpj;

	private String inscricaoEstadual;

	private String inscricaoMunicipal;

	@OneToOne(optional = false)
	@JoinColumn(name = "pessoa_id", nullable = false, unique = true)
	private Pessoa pessoa;

}
