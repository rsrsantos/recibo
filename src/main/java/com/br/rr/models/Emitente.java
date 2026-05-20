package com.br.rr.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class Emitente {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 150)
	private String nome;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private TipoPessoa tipo = TipoPessoa.FISICA;

	@Column(length = 20)
	private String documento;

	@Column(name = "razao_social", length = 150)
	private String razaoSocial;

	@Column(length = 20)
	private String telefone;

	@Column(length = 120)
	private String email;

	@Column(length = 10)
	private String cep;

	@Column(length = 150)
	private String logradouro;

	@Column(length = 20)
	private String numero;

	@Column(length = 100)
	private String bairro;

	@Column(length = 100)
	private String cidade;

	@Column(length = 2)
	private String estado;

	/** Imagem da logo armazenada diretamente no banco (PNG ou JPEG, máx. 500 KB). */
	@Column(columnDefinition = "bytea")
	private byte[] logo;

	/** MIME type da logo: "image/png" ou "image/jpeg". */
	@Column(name = "logo_tipo", length = 20)
	private String logoTipo;

	@OneToOne
	@JoinColumn(name = "usuario_id", nullable = false, unique = true)
	private Usuario usuario;

}
