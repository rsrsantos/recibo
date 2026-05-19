package com.br.rr.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.br.rr.dto.EmitenteForm;
import com.br.rr.models.TipoPessoa;
import com.br.rr.service.EmitenteService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/emitente")
public class EmitenteController {

	private final EmitenteService service;

	public EmitenteController(EmitenteService service) {
		this.service = service;
	}

	@GetMapping
	public String form(Model model) {
		model.addAttribute("emitenteForm", service.carregarForm());
		model.addAttribute("tipos", TipoPessoa.values());
		return "emitente/form";
	}

	@PostMapping
	public String salvar(@Valid @ModelAttribute EmitenteForm emitenteForm,
			BindingResult result, Model model, RedirectAttributes attributes) {
		if (result.hasErrors()) {
			model.addAttribute("tipos", TipoPessoa.values());
			return "emitente/form";
		}
		service.salvar(emitenteForm);
		attributes.addFlashAttribute("success", "Dados do emitente salvos com sucesso!");
		return "redirect:/emitente";
	}

}
