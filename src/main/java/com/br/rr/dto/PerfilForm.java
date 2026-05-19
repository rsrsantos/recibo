package com.br.rr.dto;

import jakarta.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PerfilForm {

	private Long id;

	@NotBlank(message = "O nome do perfil é obrigatório")
	private String nome;

}
