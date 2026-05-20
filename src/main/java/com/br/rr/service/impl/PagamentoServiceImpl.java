package com.br.rr.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.br.rr.exception.NegocioException;
import com.br.rr.exception.RecursoNaoEncontradoException;
import com.br.rr.models.Pagamento;
import com.br.rr.models.Plano;
import com.br.rr.models.Usuario;
import com.br.rr.repository.PagamentoRepository;
import com.br.rr.repository.PlanoRepository;
import com.br.rr.service.PagamentoService;
import com.br.rr.service.UsuarioPlanoService;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.payment.PaymentCreateRequest;
import com.mercadopago.client.payment.PaymentPayerRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;

@Service
@Transactional
public class PagamentoServiceImpl implements PagamentoService {

	private static final Logger log = LoggerFactory.getLogger(PagamentoServiceImpl.class);

	private final PagamentoRepository pagamentoRepository;
	private final PlanoRepository planoRepository;
	private final UsuarioPlanoService usuarioPlanoService;

	public PagamentoServiceImpl(PagamentoRepository pagamentoRepository,
			PlanoRepository planoRepository, UsuarioPlanoService usuarioPlanoService) {
		this.pagamentoRepository = pagamentoRepository;
		this.planoRepository = planoRepository;
		this.usuarioPlanoService = usuarioPlanoService;
	}

	@Override
	public Pagamento processarPagamento(Usuario usuario, Long planoId, Map<String, Object> formData) {
		Plano plano = planoRepository.findById(planoId)
				.orElseThrow(() -> new NegocioException("Plano não encontrado."));

		// Segurança: impede pagamento duplicado em andamento para o mesmo usuário+plano
		boolean temPendente = pagamentoRepository
				.existsByUsuarioAndPlanoAndMpStatus(usuario, plano, "pending");
		if (temPendente) {
			throw new NegocioException("Já existe um pagamento pendente para este plano.");
		}

		String paymentMethodId = (String) formData.get("payment_method_id");
		String paymentTypeId   = (String) formData.get("payment_type_id");
		String token           = (String) formData.get("token");
		Integer installments   = formData.containsKey("installments")
				? Integer.valueOf(formData.get("installments").toString()) : 1;
		String issuerId        = formData.containsKey("issuer_id")
				? formData.get("issuer_id").toString() : null;

		PaymentPayerRequest payer = PaymentPayerRequest.builder()
				.email(usuario.getEmail())
				.build();

		PaymentCreateRequest.PaymentCreateRequestBuilder builder = PaymentCreateRequest.builder()
				.transactionAmount(plano.getPrecoMensal())
				.description("Assinatura " + plano.getNome() + " - Recibos")
				.paymentMethodId(paymentMethodId)
				.payer(payer);

		if ("pix".equals(paymentTypeId) || "pix".equals(paymentMethodId)) {
			builder.paymentMethodId("pix");
		} else {
			builder.token(token).installments(installments);
			if (issuerId != null) builder.issuerId(issuerId);
		}

		Pagamento pagamento = new Pagamento();
		pagamento.setUsuario(usuario);
		pagamento.setPlano(plano);
		pagamento.setValor(plano.getPrecoMensal());
		pagamento.setMetodo(paymentMethodId);

		try {
			PaymentClient client = new PaymentClient();
			Payment mp = client.create(builder.build());

			pagamento.setMpPaymentId(String.valueOf(mp.getId()));
			pagamento.setMpStatus(mp.getStatus().toString());
			pagamento.setMpStatusDetail(mp.getStatusDetail());

			// Segurança: valida que o valor cobrado bate com o plano
			if (mp.getTransactionAmount() != null) {
				BigDecimal cobrado = mp.getTransactionAmount();
				if (cobrado.compareTo(plano.getPrecoMensal()) != 0) {
					log.warn("Valor divergente MP={} vs Plano={} paymentId={}",
							cobrado, plano.getPrecoMensal(), mp.getId());
				}
			}

			if ("approved".equals(mp.getStatus().toString())) {
				usuarioPlanoService.ativarAposPagamento(usuario, planoId);
			}

			if (mp.getPointOfInteraction() != null
					&& mp.getPointOfInteraction().getTransactionData() != null) {
				pagamento.setQrCode(mp.getPointOfInteraction().getTransactionData().getQrCode());
				pagamento.setQrBase64(mp.getPointOfInteraction().getTransactionData().getQrCodeBase64());
			}

		} catch (MPApiException e) {
			log.error("MP API error {}: {}", e.getStatusCode(), e.getApiResponse().getContent());
			throw new NegocioException("Erro ao processar pagamento: " + e.getMessage());
		} catch (MPException e) {
			log.error("MP error", e);
			throw new NegocioException("Erro de comunicação com o Mercado Pago.");
		}

		return pagamentoRepository.save(pagamento);
	}

	@Override
	public void processarWebhook(String mpPaymentId) {
		try {
			PaymentClient client = new PaymentClient();
			Payment mp = client.get(Long.valueOf(mpPaymentId));

			pagamentoRepository.findByMpPaymentId(mpPaymentId).ifPresent(pag -> {
				String statusAnterior = pag.getMpStatus();
				pag.setMpStatus(mp.getStatus().toString());
				pag.setMpStatusDetail(mp.getStatusDetail());
				pag.setAtualizadoEm(LocalDateTime.now());
				pagamentoRepository.save(pag);

				// Idempotência: só ativa se ainda não estava aprovado
				if ("approved".equals(mp.getStatus().toString())
						&& !"approved".equals(statusAnterior)) {
					usuarioPlanoService.ativarAposPagamento(pag.getUsuario(), pag.getPlano().getId());
				}
			});
		} catch (Exception e) {
			log.error("Erro ao processar webhook MP paymentId={}", mpPaymentId, e);
		}
	}

	/**
	 * Consulta o status diretamente na API do MP e atualiza o banco.
	 * Necessário para PIX quando não há webhook configurado (ambiente local).
	 */
	@Override
	public Map<String, Object> consultarEAtualizarStatus(Long pagamentoId) {
		Pagamento pag = pagamentoRepository.findById(pagamentoId).orElse(null);
		if (pag == null) return Map.of("status", "not_found", "aprovado", false, "expirado", false);

		// PIX expira em 10 minutos — se criado há mais de 10min e ainda pending, marca expirado
		boolean expirado = false;
		if ("pending".equals(pag.getMpStatus()) && pag.getCriadoEm() != null
				&& pag.getCriadoEm().isBefore(LocalDateTime.now().minusMinutes(10))) {
			expirado = true;
			pag.setMpStatus("expired");
			pag.setAtualizadoEm(LocalDateTime.now());
			pagamentoRepository.save(pag);
			return Map.of("status", "expired", "aprovado", false, "expirado", true);
		}

		// Consulta a API do MP para ter o status mais recente
		try {
			PaymentClient client = new PaymentClient();
			Payment mp = client.get(Long.valueOf(pag.getMpPaymentId()));
			String statusNovo = mp.getStatus().toString();

			if (!statusNovo.equals(pag.getMpStatus())) {
				String statusAnterior = pag.getMpStatus();
				pag.setMpStatus(statusNovo);
				pag.setMpStatusDetail(mp.getStatusDetail());
				pag.setAtualizadoEm(LocalDateTime.now());
				pagamentoRepository.save(pag);

				if ("approved".equals(statusNovo) && !"approved".equals(statusAnterior)) {
					usuarioPlanoService.ativarAposPagamento(pag.getUsuario(), pag.getPlano().getId());
				}
			}
		} catch (Exception e) {
			log.warn("Erro ao consultar MP para pagamentoId={}: {}", pagamentoId, e.getMessage());
		}

		boolean aprovado = "approved".equals(pag.getMpStatus());
		return Map.of("status", pag.getMpStatus(), "aprovado", aprovado, "expirado", expirado);
	}

	@Override
	@Transactional(readOnly = true)
	public Pagamento buscarPorId(Long pagamentoId, Usuario usuario) {
		Pagamento pag = pagamentoRepository.findById(pagamentoId)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Pagamento não encontrado."));
		if (!pag.getUsuario().getId().equals(usuario.getId())) {
			throw new RecursoNaoEncontradoException("Pagamento não encontrado.");
		}
		return pag;
	}

}
