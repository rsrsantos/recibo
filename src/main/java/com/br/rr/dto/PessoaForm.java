package com.br.rr.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.br.rr.models.TipoPessoa;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PessoaForm {

	private Long id;

	@NotBlank(message = "O nome é obrigatório")
	private String nome;

	@NotNull(message = "O tipo é obrigatório")
	private TipoPessoa tipo = TipoPessoa.FISICA;

	// Pessoa física
	private String cpf;
	private String identidade;
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate dtNascimento;

	// Pessoa jurídica
	private String razao;
	private String fantasia;
	private String cnpj;
	private String inscricaoEstadual;
	private String inscricaoMunicipal;

}
