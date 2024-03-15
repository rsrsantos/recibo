package com.AppRH.AppRH.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotEmpty;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
public class Candidato {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(unique = true)
	private String rg;
	
	@NotEmpty
	@Column(name="nome_candidato")
	private String nomeCandidato;
	
	@NotEmpty
	private String email;
	
	@ManyToOne
	@JoinColumn(name = "vaga_codigo")
	private Vaga vaga;

	
	
}
