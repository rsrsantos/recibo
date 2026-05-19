package com.br.rr.dto;

import com.br.rr.models.TipoPessoa;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmitenteForm {

	@NotBlank(message = "O nome é obrigatório")
	private String nome;

	@NotNull(message = "O tipo é obrigatório")
	private TipoPessoa tipo = TipoPessoa.FISICA;

	private String documento;
	private String razaoSocial;
	private String telefone;
	private String email;
	private String cep;
	private String logradouro;
	private String numero;
	private String bairro;
	private String cidade;
	private String estado;

}
