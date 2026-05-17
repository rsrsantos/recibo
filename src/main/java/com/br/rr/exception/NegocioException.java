package com.br.rr.exception;

/**
 * Lançada quando uma regra de negócio é violada (ex.: recibo sem itens,
 * recibo sem cliente). Tratada de forma amigável na camada web.
 */
public class NegocioException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public NegocioException(String mensagem) {
		super(mensagem);
	}

}
