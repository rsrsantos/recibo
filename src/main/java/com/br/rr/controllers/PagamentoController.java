package com.br.rr.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.br.rr.models.Pagamento;
import com.br.rr.models.Plano;
import com.br.rr.repository.PlanoRepository;
import com.br.rr.service.ContaService;
import com.br.rr.service.PagamentoService;

@Controller
@RequestMapping("/pagamento")
public class PagamentoController {

	private final PagamentoService pagamentoService;
	private final PlanoRepository planoRepository;
	private final ContaService contaService;

	@Value("${mercadopago.public-key}")
	private String mpPublicKey;

	public PagamentoController(PagamentoService pagamentoService,
			PlanoRepository planoRepository, ContaService contaService) {
		this.pagamentoService = pagamentoService;
		this.planoRepository = planoRepository;
		this.contaService = contaService;
	}

	/** Tela de checkout com Bricks */
	@GetMapping("/checkout/{planoId}")
	public String checkout(@PathVariable Long planoId, Model model) {
		Plano plano = planoRepository.findById(planoId).orElseThrow();
		model.addAttribute("plano", plano);
		model.addAttribute("mpPublicKey", mpPublicKey);
		return "pagamento/checkout";
	}

	/** Chamado pelo Brick via fetch — cria pagamento no MP */
	@PostMapping("/processar/{planoId}")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> processar(
			@PathVariable Long planoId,
			@RequestBody Map<String, Object> formData) {

		Pagamento pag = pagamentoService.processarPagamento(
				contaService.usuarioLogado(), planoId, formData);

		String status = pag.getMpStatus();

		return ResponseEntity.ok(Map.of(
				"pagamentoId", pag.getId(),
				"status",      status,
				"aprovado",    "approved".equals(status),
				"qrCode",      pag.getQrCode()   != null ? pag.getQrCode()   : "",
				"qrBase64",    pag.getQrBase64() != null ? pag.getQrBase64() : ""
		));
	}

	/** Polling de status para PIX — consulta a API do MP diretamente */
	@GetMapping("/status/{pagamentoId}")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> status(@PathVariable Long pagamentoId) {
		return ResponseEntity.ok(pagamentoService.consultarEAtualizarStatus(pagamentoId));
	}

	/** Comprovante de pagamento */
	@GetMapping("/comprovante/{pagamentoId}")
	public String comprovante(@PathVariable Long pagamentoId, Model model) {
		com.br.rr.models.Pagamento pag = pagamentoService.buscarPorId(pagamentoId,
				contaService.usuarioLogado());
		model.addAttribute("pag", pag);
		return "pagamento/comprovante";
	}

	/** Webhook do Mercado Pago */
	@PostMapping("/webhook")
	@ResponseBody
	public ResponseEntity<Void> webhook(@RequestParam(required = false) String type,
			@RequestBody(required = false) Map<String, Object> body) {
		if ("payment".equals(type) && body != null && body.containsKey("data")) {
			@SuppressWarnings("unchecked")
			Map<String, Object> data = (Map<String, Object>) body.get("data");
			String mpId = String.valueOf(data.get("id"));
			pagamentoService.processarWebhook(mpId);
		}
		return ResponseEntity.ok().build();
	}

}
