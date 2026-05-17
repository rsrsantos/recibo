package com.br.rr.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(RecursoNaoEncontradoException.class)
	public String recursoNaoEncontrado(RecursoNaoEncontradoException ex, Model model) {
		model.addAttribute("mensagem", ex.getMessage());
		return "erro";
	}

}
