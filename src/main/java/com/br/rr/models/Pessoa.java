package com.br.rr.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Pessoa {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "O nome é obrigatório")
	@Column(nullable = false, length = 150)
	private String nome;

	@NotNull(message = "O tipo é obrigatório")
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private TipoPessoa tipo;

	/** Conta dona deste cliente. Nulo = pessoa do próprio titular. */
	@ManyToOne
	@JoinColumn(name = "usuario_id")
	private Usuario proprietario;

}
