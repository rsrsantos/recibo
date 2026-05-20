package com.br.rr.exception;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(RecursoNaoEncontradoException.class)
	public Object recursoNaoEncontrado(RecursoNaoEncontradoException ex,
			HttpServletRequest req, Model model) {
		log.warn("Recurso não encontrado: {}", ex.getMessage());
		if (isAjax(req)) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(Map.of("mensagem", ex.getMessage()));
		}
		model.addAttribute("mensagem", ex.getMessage());
		return "error/404";
	}

	@ExceptionHandler(NegocioException.class)
	public Object negocio(NegocioException ex, HttpServletRequest req, Model model) {
		log.warn("Erro de negócio: {}", ex.getMessage());
		if (isAjax(req)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("mensagem", ex.getMessage()));
		}
		model.addAttribute("mensagem", ex.getMessage());
		return "error/400";
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public Object erroGenerico(Exception ex, HttpServletRequest req, Model model) {
		log.error("Erro interno não tratado", ex);
		if (isAjax(req)) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("mensagem", "Erro interno. Tente novamente."));
		}
		return "error/500";
	}

	private boolean isAjax(HttpServletRequest req) {
		String accept      = req.getHeader("Accept");
		String contentType = req.getHeader("Content-Type");
		return (accept != null && accept.contains("application/json"))
				|| (contentType != null && contentType.contains("application/json"));
	}

}
