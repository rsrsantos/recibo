package com.br.rr.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CadastroForm {

	@NotBlank(message = "O nome é obrigatório")
	private String nome;

	@NotBlank(message = "O e-mail é obrigatório")
	@Email(message = "E-mail inválido")
	private String email;

	@NotBlank(message = "A senha é obrigatória")
	@Size(min = 6, message = "A senha deve ter ao menos 6 caracteres")
	private String senha;

	@NotBlank(message = "Confirme a senha")
	private String confirmacao;

	/** ID do plano escolhido na landing page (opcional). */
	private Long planoId;

}
