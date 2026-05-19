package com.br.rr.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioEditForm {

	private Long id;

	@NotBlank(message = "O nome é obrigatório")
	private String nome;

	@NotBlank(message = "O e-mail é obrigatório")
	@Email(message = "E-mail inválido")
	private String email;

	/** Opcional: se preenchido, redefine a senha do usuário (mín. 6 chars). */
	private String novaSenha;

}
