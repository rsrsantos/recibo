package com.br.rr.controllers;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
import com.br.rr.models.Recibo;
import com.br.rr.service.PessoaService;
import com.br.rr.service.ReciboService;

@Controller
public class ReciboController {

	private final ReciboService service;
	private final PessoaService pessoaService;

	public ReciboController(ReciboService service, PessoaService pessoaService) {
		this.service = service;
		this.pessoaService = pessoaService;
	}

	@GetMapping("/recibos")
	public String listar(Model model, @RequestParam(defaultValue = "0") int page) {
		model.addAttribute("recibos",
				service.listar(PageRequest.of(page, 5, Sort.by(Sort.Order.desc("id")))));
		return "recibo/lista";
	}

	@GetMapping("/recibos/novo")
	public String form(Model model) {
		model.addAttribute("reciboForm", new ReciboForm());
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

}
