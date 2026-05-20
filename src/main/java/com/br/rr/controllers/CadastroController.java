package com.br.rr.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.br.rr.dto.CadastroForm;
import com.br.rr.exception.NegocioException;
import com.br.rr.service.ContaService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/cadastro")
public class CadastroController {

	private final ContaService contaService;

	public CadastroController(ContaService contaService) {
		this.contaService = contaService;
	}

	@GetMapping
	public String form(@RequestParam(required = false) Long plano, Model model) {
		CadastroForm form = new CadastroForm();
		form.setPlanoId(plano);
		model.addAttribute("cadastroForm", form);
		return "cadastro";
	}

	@PostMapping
	public String cadastrar(@Valid @ModelAttribute CadastroForm cadastroForm,
			BindingResult result, Model model, RedirectAttributes ra) {
		if (result.hasErrors()) {
			return "cadastro";
		}
		try {
			contaService.cadastrar(cadastroForm);
			return "redirect:/cadastro/aguardando-confirmacao?email=" +
					java.net.URLEncoder.encode(cadastroForm.getEmail(), java.nio.charset.StandardCharsets.UTF_8);
		} catch (NegocioException ex) {
			model.addAttribute("erro", ex.getMessage());
			return "cadastro";
		}
	}

	@GetMapping("/aguardando-confirmacao")
	public String aguardando(@RequestParam(required = false) String email, Model model) {
		model.addAttribute("email", email);
		return "cadastro/aguardando-confirmacao";
	}

}
