package com.br.rr.exception;

/**
 * Lançada pela camada de serviço quando uma entidade solicitada por id não existe.
 */
public class RecursoNaoEncontradoException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public RecursoNaoEncontradoException(String mensagem) {
		super(mensagem);
	}

}
