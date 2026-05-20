package com.br.rr.controllers;

import java.util.EnumMap;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.br.rr.dto.ReciboForm;
import com.br.rr.exception.NegocioException;
import com.br.rr.models.ModeloRecibo;
import com.br.rr.models.Recibo;
import com.br.rr.service.AssinaturaGuard;
import com.br.rr.service.ContaService;
import com.br.rr.service.PessoaService;
import com.br.rr.service.ReciboService;

@Controller
public class ReciboController {

	private final ReciboService service;
	private final PessoaService pessoaService;
	private final ContaService contaService;
	private final AssinaturaGuard assinaturaGuard;

	public ReciboController(ReciboService service, PessoaService pessoaService,
			ContaService contaService, AssinaturaGuard assinaturaGuard) {
		this.service = service;
		this.pessoaService = pessoaService;
		this.contaService = contaService;
		this.assinaturaGuard = assinaturaGuard;
	}

	@GetMapping("/recibos")
	public String listar(Model model, @RequestParam(defaultValue = "0") int page) {
		model.addAttribute("recibos",
				service.listar(PageRequest.of(page, 5, Sort.by(Sort.Order.desc("id")))));
		return "recibo/lista";
	}

	@GetMapping("/recibos/novo")
	public String escolherModelo(Model model) {
		var usuario = contaService.usuarioLogado();
		Map<ModeloRecibo, Boolean> liberados = new EnumMap<>(ModeloRecibo.class);
		for (ModeloRecibo m : ModeloRecibo.values()) {
			liberados.put(m, assinaturaGuard.podeUsarModelo(usuario, m));
		}
		model.addAttribute("modelos", ModeloRecibo.values());
		model.addAttribute("liberados", liberados);
		return "recibo/modelos";
	}

	@GetMapping("/recibos/novo/{modelo}")
	public String form(@PathVariable ModeloRecibo modelo, Model model,
			RedirectAttributes attributes) {
		if (!modelo.isImplementado()) {
			attributes.addFlashAttribute("mensagem_erro",
					"O modelo \"" + modelo.getDescricao() + "\" ainda não está disponível.");
			return "redirect:/recibos/novo";
		}
		if (!assinaturaGuard.podeUsarModelo(contaService.usuarioLogado(), modelo)) {
			attributes.addFlashAttribute("mensagem_erro",
					"Seu plano não inclui o modelo \"" + modelo.getDescricao() + "\".");
			return "redirect:/recibos/novo";
		}
		ReciboForm form = new ReciboForm();
		form.setModelo(modelo);
		model.addAttribute("reciboForm", form);
		model.addAttribute("modelo", modelo);
		model.addAttribute("proximoNumero", service.proximoNumero());
		return "recibo/form";
	}

	@PostMapping("/recibos")
	public String emitir(@ModelAttribute ReciboForm reciboForm, RedirectAttributes attributes) {
		try {
			Recibo salvo = service.emitir(reciboForm);
			attributes.addFlashAttribute("success",
					"Recibo nº " + salvo.getNRecibo() + " emitido com sucesso!");
			return "redirect:/recibos";
		} catch (NegocioException ex) {
			attributes.addFlashAttribute("mensagem_erro", ex.getMessage());
			return "redirect:/recibos/novo";
		}
	}

	@GetMapping("/recibos/{id}/excluir")
	public String excluir(@PathVariable Long id, RedirectAttributes attributes) {
		service.excluir(id);
		attributes.addFlashAttribute("success", "Recibo excluído.");
		return "redirect:/recibos";
	}

	@GetMapping("/recibos/{id}/pdf")
	public ResponseEntity<byte[]> pdf(@PathVariable Long id,
			@RequestParam(defaultValue = "1") int vias) {
		byte[] bytes = service.gerarPdf(id, vias);
		return ResponseEntity.ok()
				.contentType(MediaType.APPLICATION_PDF)
				.header(HttpHeaders.CONTENT_DISPOSITION,
						"inline; filename=\"recibo-" + id + ".pdf\"")
				.body(bytes);
	}

}
