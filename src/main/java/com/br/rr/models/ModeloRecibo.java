package com.br.rr.models;

public enum ModeloRecibo {

	PADRAO("Recibo Padrão", "Modelo tradicional de recibo, em folha A4.", true),
	BOBINA("Recibo em Bobina", "Formato estreito para impressoras térmicas.", false),
	PROMISSORIA("Nota Promissória", "Título de promessa de pagamento.", false),
	INVERSO("Recibo Inverso", "Recibo emitido pelo pagador.", false),
	VALE("Vale Diverso", "Vale para adiantamentos e diversos.", false),
	MEDICO("Recibo Médico", "Recibo de honorários para área da saúde.", false);

	private final String descricao;
	private final String detalhe;
	/** true quando o layout já está implementado no sistema. */
	private final boolean implementado;

	ModeloRecibo(String descricao, String detalhe, boolean implementado) {
		this.descricao = descricao;
		this.detalhe = detalhe;
		this.implementado = implementado;
	}

	public String getDescricao() {
		return descricao;
	}

	public String getDetalhe() {
		return detalhe;
	}

	public boolean isImplementado() {
		return implementado;
	}
}
