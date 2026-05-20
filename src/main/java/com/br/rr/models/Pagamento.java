package com.br.rr.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Pagamento {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "usuario_id")
	private Usuario usuario;

	@ManyToOne(optional = false)
	@JoinColumn(name = "plano_id")
	private Plano plano;

	@Column(name = "mp_payment_id")
	private String mpPaymentId;

	@Column(name = "mp_status")
	private String mpStatus;

	@Column(name = "mp_status_detail", columnDefinition = "TEXT")
	private String mpStatusDetail;

	@Column(name = "qr_code", columnDefinition = "TEXT")
	private String qrCode;

	@Column(name = "qr_base64", columnDefinition = "TEXT")
	private String qrBase64;

	private String metodo;

	@Column(nullable = false)
	private BigDecimal valor;

	@Column(name = "criado_em", nullable = false)
	private LocalDateTime criadoEm = LocalDateTime.now();

	@Column(name = "atualizado_em")
	private LocalDateTime atualizadoEm;

}
